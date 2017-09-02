<%--
    add_newsletter_button.jsp: add a newsletter
    
    Created:    2017-09-02 11:33 by Christian Berndt
    Modified:   2017-09-02 11:33 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<c:if test="<%=NewsletterPortletPermission.contains(permissionChecker, scopeGroupId,
                        NewsletterActionKeys.ADD_NEWSLETTER)%>">

    <liferay-frontend:add-menu>

        <portlet:renderURL var="addNewsletterURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="mvcPath" value="/edit_newsletter.jsp" />
        </portlet:renderURL>

        <liferay-frontend:add-menu-item
            title='<%=LanguageUtil.get(request, "add-newsletter")%>'
            url="<%=addNewsletterURL.toString()%>" />

    </liferay-frontend:add-menu>

</c:if>