<%--
    mailing_action.jsp: The action menu of the newsletter-manager's mailing panel.
    
    Created:    2017-09-21 23:50 by Christian Berndt
    Modified:   2017-09-26 23:57 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp"%>

<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>

<%
	ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

	Mailing mailing = (Mailing) row.getObject();

	String editURL = (String) request.getAttribute("editURL");
	String viewURL = (String) request.getAttribute("viewURL");

	editURL = HttpUtil.setParameter(editURL, renderResponse.getNamespace() + "mailingId",
	        mailing.getMailingId());
	viewURL = HttpUtil.setParameter(viewURL, renderResponse.getNamespace() + "mailingId",
	        mailing.getMailingId());

	boolean hasUpdatePermission = MailingPermission.contains(permissionChecker, mailing,
	        NewsletterActionKeys.UPDATE);
	boolean hasViewPermission = MailingPermission.contains(permissionChecker, mailing,
	        NewsletterActionKeys.VIEW);
	boolean hasDeletePermission = MailingPermission.contains(permissionChecker, mailing,
	        NewsletterActionKeys.DELETE);
	boolean hasPermissionsPermission = MailingPermission.contains(permissionChecker, mailing,
	        NewsletterActionKeys.PERMISSIONS);
%>

<liferay-ui:icon-menu showWhenSingleIcon="true">

    <c:if test="<%=hasViewPermission%>">

        <liferay-ui:icon iconCssClass="icon-eye-open" message="view" 
            url="<%= editURL %>" />

    </c:if>

    <c:if test="<%=hasUpdatePermission%>">

        <liferay-ui:icon iconCssClass="icon-edit" message="edit" 
            url="<%= viewURL %>" />

    </c:if>

    <c:if test="<%=hasDeletePermission%>">

        <portlet:actionURL var="deleteURL">
            <portlet:param name="className" value="<%= Mailing.class.getName() %>"/>        
            <portlet:param name="cmd" value="<%= Constants.DELETE %>"/>
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="mailingId"
                value="<%=String.valueOf(mailing.getMailingId())%>" />
        </portlet:actionURL>

        <liferay-ui:icon-delete message="delete" url="<%=deleteURL%>" />

    </c:if>

    <c:if test="<%= hasPermissionsPermission %>">

        <liferay-security:permissionsURL
            modelResource="<%= Mailing.class.getName() %>"
            modelResourceDescription="<%= String.valueOf(mailing.getMailingId()) %>"
            resourcePrimKey="<%= String.valueOf(mailing.getMailingId()) %>"
            var="permissionsEntryURL"
            windowState="<%= LiferayWindowState.POP_UP.toString() %>" />

        <liferay-ui:icon iconCssClass="icon-cog" message="permissions"
            method="get" url="<%= permissionsEntryURL %>"
            useDialog="<%= true %>" />
    </c:if>

</liferay-ui:icon-menu>
