package ch.inofix.newsletter.web.internal.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import ch.inofix.newsletter.model.Subscriber;
//import ch.inofix.newsletter.util.PortletKey;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 *
 * @author Christian Berndt
 * @created 2017-03-16 16:22
 * @modified 2017-03-16 16:22
 * @version 1.0.0
 *
 */
public class SubscriberSearch extends SearchContainer<Subscriber> {

	static List<String> headerNames = new ArrayList<String>();
	static Map<String, String> orderableHeaders = new HashMap<String, String>();

	// The list of header names corresponds bean properties of
	// ch.inofix.newsletter.model.SubscriberImpl
	static {
		headerNames.add("create-date");
		headerNames.add("modified-date");
		headerNames.add("title");
		headerNames.add("user-name");

		orderableHeaders.put("create-date", "create-date");
		orderableHeaders.put("modified-date", "modified-date");
		orderableHeaders.put("title", "title");
		orderableHeaders.put("user-name", "user-name");
	}

	public static final String EMPTY_RESULTS_MESSAGE = "there-are-no-subscribers";

	public SubscriberSearch(PortletRequest portletRequest, PortletURL iteratorURL) {
		this(portletRequest, DEFAULT_CUR_PARAM, iteratorURL);
	}

	public SubscriberSearch(PortletRequest portletRequest, String curParam, PortletURL iteratorURL) {

		super(portletRequest, new SubscriberDisplayTerms(portletRequest), new SubscriberSearchTerms(portletRequest),
				curParam, DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		SubscriberDisplayTerms displayTerms = (SubscriberDisplayTerms) getDisplayTerms();

		iteratorURL.setParameter(SubscriberDisplayTerms.CREATE_DATE, String.valueOf(displayTerms.getCreateDate()));
		iteratorURL.setParameter(SubscriberDisplayTerms.MODIFIED_DATE, String.valueOf(displayTerms.getModifiedDate()));
		iteratorURL.setParameter(SubscriberDisplayTerms.TITLE, String.valueOf(displayTerms.getTitle()));
		iteratorURL.setParameter(SubscriberDisplayTerms.USER_NAME, String.valueOf(displayTerms.getUserName()));

		try {
			PortalPreferences preferences = PortletPreferencesFactoryUtil.getPortalPreferences(portletRequest);

			String orderByCol = ParamUtil.getString(portletRequest, "orderByCol");
			String orderByType = ParamUtil.getString(portletRequest, "orderByType");

			if (Validator.isNotNull(orderByCol) && Validator.isNotNull(orderByType)) {
				// TODO:
				// preferences.setValue(PortletKey.NEWSLETTER,
				// "subscribers-order-by-col", orderByCol);
				// preferences.setValue(PortletKey.NEWSLETTER,
				// "subscribers-order-by-type", orderByType);
				// } else {
				// orderByCol = preferences.getValue(PortletKey.NEWSLETTER,
				// "subscribers-order-by-col", "modified-date");
				// orderByType = preferences.getValue(PortletKey.NEWSLETTER,
				// "subscribers-order-by-type", "desc");
			}

			setOrderableHeaders(orderableHeaders);
			setOrderByCol(orderByCol);
			setOrderByType(orderByType);

		} catch (Exception e) {
			_log.error(e);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(SubscriberSearch.class);

}
