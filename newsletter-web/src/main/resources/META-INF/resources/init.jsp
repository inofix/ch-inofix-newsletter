<%--
    init.jsp: Common imports and initialization code.

    Created:     2016-10-05 15:44 by Christian Berndt
    Modified:    2017-10-14 21:17 by Christian Berndt
    Version:     1.2.2
--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend"%>
<%@taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@page import="ch.inofix.newsletter.constants.NewsletterActionKeys"%>
<%@page import="ch.inofix.newsletter.constants.PortletKeys"%>
<%@page import="ch.inofix.newsletter.exception.EmailAddressException"%>
<%@page import="ch.inofix.newsletter.exception.NoSuchNewsletterException"%>
<%@page import="ch.inofix.newsletter.model.Mailing"%>
<%@page import="ch.inofix.newsletter.model.Newsletter"%>
<%@page import="ch.inofix.newsletter.model.Subscriber"%>
<%@page import="ch.inofix.newsletter.web.internal.constants.NewsletterWebKeys"%>
<%@page import="ch.inofix.newsletter.web.internal.search.MailingSearch"%>
<%@page import="ch.inofix.newsletter.web.internal.search.MailingSearchTerms"%>
<%@page import="ch.inofix.newsletter.web.internal.search.NewsletterSearch"%>
<%@page import="ch.inofix.newsletter.web.internal.search.NewsletterSearchTerms"%>
<%@page import="ch.inofix.newsletter.web.internal.search.SubscriberSearch"%>
<%@page import="ch.inofix.newsletter.web.internal.search.SubscriberSearchTerms"%>
<%@page import="ch.inofix.newsletter.security.ActionKeys"%>
<%@page import="ch.inofix.newsletter.service.MailingServiceUtil"%>
<%@page import="ch.inofix.newsletter.service.NewsletterServiceUtil"%>
<%@page import="ch.inofix.newsletter.service.SubscriberServiceUtil"%>
<%@page import="ch.inofix.newsletter.service.permission.MailingPermission"%>
<%@page import="ch.inofix.newsletter.service.permission.NewsletterPermission"%>
<%@page import="ch.inofix.newsletter.service.permission.NewsletterPortletPermission"%>
<%@page import="ch.inofix.newsletter.service.permission.SubscriberPermission"%>
<%@page import="ch.inofix.newsletter.util.MailingUtil"%>
<%@page import="ch.inofix.newsletter.util.NewsletterUtil"%>
<%@page import="ch.inofix.newsletter.util.SubscriberUtil"%>

<%@page import="com.liferay.portal.kernel.exception.SystemException"%>
<%@page import="com.liferay.portal.kernel.exception.PortalException"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.language.UnicodeLanguageUtil"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.model.User"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.portlet.PortalPreferences"%>
<%@page import="com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.search.Document"%>
<%@page import="com.liferay.portal.kernel.search.Field"%>
<%@page import="com.liferay.portal.kernel.search.Hits"%>
<%@page import="com.liferay.portal.kernel.search.Indexer"%>
<%@page import="com.liferay.portal.kernel.search.IndexerRegistryUtil"%>
<%@page import="com.liferay.portal.kernel.search.SearchContextFactory"%>
<%@page import="com.liferay.portal.kernel.search.SearchContext"%>
<%@page import="com.liferay.portal.kernel.search.Sort"%>
<%@page import="com.liferay.portal.kernel.security.auth.PrincipalException"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.util.HttpUtil"%>
<%@page import="com.liferay.portal.kernel.util.OrderByComparator"%>
<%@page import="com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.PortalUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>

<%@page import="java.io.StringWriter"%>
<%@page import="java.io.PrintWriter"%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="javax.portlet.PortletURL"%>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
    PortalPreferences portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(request);

    long articleGroupId = GetterUtil.getLong(portletPreferences.getValue("articleGroupId", ""));
    
    String markupView = "lexicon";

    String newsletterStructureId = portletPreferences.getValue("newsletterStructureId", "");

    boolean showSearchSpeed = false;
    
    String tabs1 = ParamUtil.getString(request, "tabs1", "mailings");
%>
