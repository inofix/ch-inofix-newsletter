<%--
    newsletter_action.jsp: The action menu of the newsletter-manager's newsletter panel.
    
    Created:    2017-09-26 18:25 by Christian Berndt
    Modified:   2017-09-26 18:25 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp"%>

<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>

<%
    ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

    Newsletter newsletter = (Newsletter) row.getObject();

    String editURL = (String) request.getAttribute("editURL");
    String viewURL = (String) request.getAttribute("viewURL");

    editURL = HttpUtil.setParameter(editURL, renderResponse.getNamespace() + "newsletterId",
            newsletter.getNewsletterId());
    viewURL = HttpUtil.setParameter(viewURL, renderResponse.getNamespace() + "newsletterId",
            newsletter.getNewsletterId());

    boolean hasUpdatePermission = NewsletterPermission.contains(permissionChecker, newsletter,
            NewsletterActionKeys.UPDATE);
    boolean hasViewPermission = NewsletterPermission.contains(permissionChecker, newsletter,
            NewsletterActionKeys.VIEW);
    boolean hasDeletePermission = NewsletterPermission.contains(permissionChecker, newsletter,
            NewsletterActionKeys.DELETE);
    boolean hasPermissionsPermission = NewsletterPermission.contains(permissionChecker, newsletter,
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
            <portlet:param name="cmd" value="<%= Constants.DELETE %>"/>
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="newsletterId"
                value="<%=String.valueOf(newsletter.getNewsletterId())%>" />
        </portlet:actionURL>

        <liferay-ui:icon-delete message="delete" url="<%=deleteURL%>" />

    </c:if>

    <c:if test="<%= hasPermissionsPermission %>">

        <liferay-security:permissionsURL
            modelResource="<%= Newsletter.class.getName() %>"
            modelResourceDescription="<%= String.valueOf(newsletter.getNewsletterId()) %>"
            resourcePrimKey="<%= String.valueOf(newsletter.getNewsletterId()) %>"
            var="permissionsEntryURL"
            windowState="<%= LiferayWindowState.POP_UP.toString() %>" />

        <liferay-ui:icon iconCssClass="icon-cog" message="permissions"
            method="get" url="<%= permissionsEntryURL %>"
            useDialog="<%= true %>" />
    </c:if>

</liferay-ui:icon-menu>
