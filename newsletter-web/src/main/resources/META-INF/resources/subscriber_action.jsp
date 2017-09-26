<%--
    subscriber_action.jsp: The action menu of the newsletter-manager's subscriber panel.
    
    Created:    2017-09-26 23:38 by Christian Berndt
    Modified:   2017-09-26 23:38 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp"%>

<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>

<%
    ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

    Subscriber subscriber = (Subscriber) row.getObject();

    String editURL = (String) request.getAttribute("editURL");
    String viewURL = (String) request.getAttribute("viewURL");

    editURL = HttpUtil.setParameter(editURL, renderResponse.getNamespace() + "subscriberId",
            subscriber.getSubscriberId());
    viewURL = HttpUtil.setParameter(viewURL, renderResponse.getNamespace() + "subscriberId",
            subscriber.getSubscriberId());

    boolean hasUpdatePermission = SubscriberPermission.contains(permissionChecker, subscriber,
            NewsletterActionKeys.UPDATE);
    boolean hasViewPermission = SubscriberPermission.contains(permissionChecker, subscriber,
            NewsletterActionKeys.VIEW);
    boolean hasDeletePermission = SubscriberPermission.contains(permissionChecker, subscriber,
            NewsletterActionKeys.DELETE);
    boolean hasPermissionsPermission = SubscriberPermission.contains(permissionChecker, subscriber,
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
            <portlet:param name="subscriberId"
                value="<%=String.valueOf(subscriber.getSubscriberId())%>" />
        </portlet:actionURL>

        <liferay-ui:icon-delete message="delete" url="<%=deleteURL%>" />

    </c:if>

    <c:if test="<%= hasPermissionsPermission %>">

        <liferay-security:permissionsURL
            modelResource="<%= Subscriber.class.getName() %>"
            modelResourceDescription="<%= String.valueOf(subscriber.getSubscriberId()) %>"
            resourcePrimKey="<%= String.valueOf(subscriber.getSubscriberId()) %>"
            var="permissionsEntryURL"
            windowState="<%= LiferayWindowState.POP_UP.toString() %>" />

        <liferay-ui:icon iconCssClass="icon-cog" message="permissions"
            method="get" url="<%= permissionsEntryURL %>"
            useDialog="<%= true %>" />
    </c:if>

</liferay-ui:icon-menu>
