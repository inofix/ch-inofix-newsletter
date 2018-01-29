<%--
    add_mailing_button.jsp: add a mailing
    
    Created:    2017-09-02 11:33 by Christian Berndt
    Modified:   2018-01-29 11:37 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp" %>

<c:if test="<%=NewsletterPortletPermission.contains(permissionChecker, scopeGroupId,
                        NewsletterActionKeys.ADD_MAILING)%>">

    <liferay-frontend:add-menu>

        <portlet:renderURL var="addMailingURL">
            <portlet:param name="redirect" value="<%=currentURL%>" />
            <portlet:param name="mvcPath" value="/edit_mailing.jsp" />
        </portlet:renderURL>

        <liferay-frontend:add-menu-item
            title='<%=LanguageUtil.get(request, "add-mailing")%>'
            url="<%=addMailingURL.toString()%>" />

    </liferay-frontend:add-menu>

</c:if>
