package ch.inofix.newsletter.search;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

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
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import ch.inofix.newsletter.model.Newsletter;
import ch.inofix.newsletter.service.NewsletterLocalService;
import ch.inofix.newsletter.service.permission.NewsletterPermission;

/**
 * @author Christian Berndt
 * @created 2016-10-15 23:14
 * @modified 2017-03-16 16:06
 * @version 1.0.1
 */
public class NewsletterIndexer extends BaseIndexer<Newsletter> {

	public static final String CLASS_NAME = Newsletter.class.getName();

	public NewsletterIndexer() {
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
		return NewsletterPermission.contains(permissionChecker, entryClassPK, ActionKeys.VIEW);
	}

	@Override
	protected void doDelete(Newsletter newsletter) throws Exception {
		deleteDocument(newsletter.getCompanyId(), newsletter.getNewsletterId());
	}

	@Override
	protected Document doGetDocument(Newsletter newsletter) throws Exception {
		Document document = getBaseModelDocument(CLASS_NAME, newsletter);
		document.addTextSortable(Field.TITLE, newsletter.getTitle());

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

		Newsletter newsletter = _newsletterLocalService.getNewsletter(classPK);

		doReindex(newsletter);
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {

		long companyId = GetterUtil.getLong(ids[0]);

		// TODO: what about the group?
		reindexNewsletters(companyId);
		// reindexNewsletters(companyId, groupId);
	}

	@Override
	protected void doReindex(Newsletter newsletter) throws Exception {

		Document document = getDocument(newsletter);

		IndexWriterHelperUtil.updateDocument(getSearchEngineId(), newsletter.getCompanyId(), document,
				isCommitImmediately());
	}

	protected void reindexNewsletters(long companyId) throws PortalException {

		final IndexableActionableDynamicQuery indexableActionableDynamicQuery = _newsletterLocalService
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
				.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<Newsletter>() {

					@Override
					public void performAction(Newsletter newsletter) {
						try {
							Document document = getDocument(newsletter);

							indexableActionableDynamicQuery.addDocuments(document);
						} catch (PortalException pe) {
							if (_log.isWarnEnabled()) {
								_log.warn("Unable to index newsletter " + newsletter.getNewsletterId(), pe);
							}
						}
					}

				});
		indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

		indexableActionableDynamicQuery.performActions();
	}

	@Reference(unbind = "-")
	protected void setNewsletterLocalService(NewsletterLocalService newsletterLocalService) {

		_newsletterLocalService = newsletterLocalService;
	}

	private static final Log _log = LogFactoryUtil.getLog(NewsletterIndexer.class);

	private NewsletterLocalService _newsletterLocalService;
}
