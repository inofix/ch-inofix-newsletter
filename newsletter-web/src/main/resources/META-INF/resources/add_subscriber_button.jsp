<%--
    add_subscriber_button.jsp: add a subscriber
    
    Created:    2017-09-02 11:33 by Christian Berndt
    Modified:   2017-09-02 11:33 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<c:if test="<%=NewsletterPortletPermission.contains(permissionChecker, scopeGroupId,
                        NewsletterActionKeys.ADD_SUBSCRIBER)%>">

    <liferay-frontend:add-menu>

        <portlet:renderURL var="addSubscriberURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="mvcPath" value="/edit_subscriber.jsp" />
        </portlet:renderURL>

        <liferay-frontend:add-menu-item
            title='<%=LanguageUtil.get(request, "add-subscriber")%>'
            url="<%=addSubscriberURL.toString()%>" />

    </liferay-frontend:add-menu>

</c:if>
