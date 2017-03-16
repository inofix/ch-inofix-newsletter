package ch.inofix.newsletter.search;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import ch.inofix.newsletter.model.Subscriber;
import ch.inofix.newsletter.service.SubscriberLocalService;
import ch.inofix.newsletter.service.permission.SubscriberPermission;

/**
 * @author Christian Berndt
 * @created 2017-03-16 16:19
 * @modified 2017-03-16 16:50
 * @version 1.0.1
 */
@Component(immediate = true, service = Indexer.class)
public class SubscriberIndexer extends BaseIndexer<Subscriber> {

    public static final String CLASS_NAME = Subscriber.class.getName();

    public SubscriberIndexer() {
        setDefaultSelectedFieldNames(Field.ASSET_TAG_NAMES, Field.COMPANY_ID, Field.ENTRY_CLASS_NAME,
                Field.ENTRY_CLASS_PK, Field.GROUP_ID, Field.MODIFIED_DATE, Field.SCOPE_GROUP_ID, Field.TITLE, Field.UID,
                Field.URL);
        setFilterSearch(true);
        setPermissionAware(true);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    public boolean hasPermission(PermissionChecker permissionChecker, String entryClassName, long entryClassPK,
            String actionId) throws Exception {
        return SubscriberPermission.contains(permissionChecker, entryClassPK, ActionKeys.VIEW);
    }

    @Override
    protected void doDelete(Subscriber subscriber) throws Exception {
        deleteDocument(subscriber.getCompanyId(), subscriber.getSubscriberId());
    }

    @Override
    protected Document doGetDocument(Subscriber subscriber) throws Exception {
        Document document = getBaseModelDocument(CLASS_NAME, subscriber);
        document.addTextSortable(Field.TITLE, subscriber.getTitle());

        return document;
    }

    @Override
    protected Summary doGetSummary(Document document, Locale locale, String snippet, PortletRequest portletRequest,
            PortletResponse portletResponse) throws Exception {

        Summary summary = createSummary(document, Field.TITLE, Field.URL);

        return summary;
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {

        Subscriber subscriber = _subscriberLocalService.getSubscriber(classPK);

        doReindex(subscriber);
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {

        long companyId = GetterUtil.getLong(ids[0]);

        // TODO: what about the group?
        reindexSubscribers(companyId);
        // reindexSubscribers(companyId, groupId);
    }

    @Override
    protected void doReindex(Subscriber subscriber) throws Exception {

        Document document = getDocument(subscriber);

        IndexWriterHelperUtil.updateDocument(getSearchEngineId(), subscriber.getCompanyId(), document,
                isCommitImmediately());
    }

    protected void reindexSubscribers(long companyId) throws PortalException {

        final IndexableActionableDynamicQuery indexableActionableDynamicQuery = _subscriberLocalService
                .getIndexableActionableDynamicQuery();

        indexableActionableDynamicQuery.setAddCriteriaMethod(new ActionableDynamicQuery.AddCriteriaMethod() {

            @Override
            public void addCriteria(DynamicQuery dynamicQuery) {

                Property statusProperty = PropertyFactoryUtil.forName("status");

                Integer[] statuses = { WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_IN_TRASH };

                dynamicQuery.add(statusProperty.in(statuses));
            }

        });
        indexableActionableDynamicQuery.setCompanyId(companyId);
        // TODO: what about the group?
        // indexableActionableDynamicQuery.setGroupId(groupId);
        indexableActionableDynamicQuery
                .setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<Subscriber>() {

                    @Override
                    public void performAction(Subscriber subscriber) {
                        try {
                            Document document = getDocument(subscriber);

                            indexableActionableDynamicQuery.addDocuments(document);
                        } catch (PortalException pe) {
                            if (_log.isWarnEnabled()) {
                                _log.warn("Unable to index subscriber " + subscriber.getSubscriberId(), pe);
                            }
                        }
                    }

                });
        indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

        indexableActionableDynamicQuery.performActions();
    }

    @Reference(unbind = "-")
    protected void setSubscriberLocalService(SubscriberLocalService subscriberLocalService) {

        _subscriberLocalService = subscriberLocalService;
    }

    private static final Log _log = LogFactoryUtil.getLog(SubscriberIndexer.class);

    private SubscriberLocalService _subscriberLocalService;
}
