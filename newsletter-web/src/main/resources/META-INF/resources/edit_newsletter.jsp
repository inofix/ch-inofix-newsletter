<%--
    edit_newsletter.jsp: edit a newsletter.

    Created:     2017-09-17 00:02 by Christian Berndt
    Modified:    2017-09-17 00:02 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp"%>

<%@page import="java.util.Calendar"%>

<%
    Newsletter newsletter = (Newsletter) request.getAttribute(NewsletterWebKeys.MAILING);

    String title = LanguageUtil.get(request, "new-newsletter");

    boolean hasUpdatePermission = false;
    boolean hasViewPermission = false;
    boolean hasDeletePermission = false;
    boolean hasPermissionsPermission = false;

    if (newsletter != null) {

        title = LanguageUtil.format(request, "edit-newsletter-x",
                String.valueOf(newsletter.getTitle()));

        hasUpdatePermission = NewsletterPermission.contains(permissionChecker, newsletter,
                NewsletterActionKeys.UPDATE);
        hasViewPermission = NewsletterPermission.contains(permissionChecker, newsletter,
                NewsletterActionKeys.VIEW);
        hasDeletePermission = NewsletterPermission.contains(permissionChecker, newsletter,
                NewsletterActionKeys.DELETE);
        hasPermissionsPermission = NewsletterPermission.contains(permissionChecker, newsletter,
                NewsletterActionKeys.PERMISSIONS);

    } else {

        newsletter = NewsletterServiceUtil.createNewsletter();
        hasUpdatePermission = true;

    }

    String redirect = ParamUtil.getString(request, "redirect");

    String backURL = ParamUtil.getString(request, "backURL", redirect);

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(redirect);

    renderResponse.setTitle(title);

    request.setAttribute("showTitle", "true"); // used by inofix-theme
%>

<div class="container-fluid-1280">

    <portlet:actionURL name="updateNewsletter" var="updateNewsletterURL">
        <portlet:param name="mvcPath" value="/edit_newsletter.jsp" />
    </portlet:actionURL>

    <aui:form method="post" action="<%=updateNewsletterURL%>" name="fm">
    
        <aui:input name="cmd" type="hidden" 
            value="<%= Constants.UPDATE %>"/>
        <aui:input name="className" type="hidden"
            value="<%= Newsletter.class.getName() %>" />
        <aui:input name="userId" type="hidden"
            value="<%=String.valueOf(themeDisplay.getUserId())%>" />
    
        <aui:model-context bean="<%=newsletter%>"
            model="<%=Newsletter.class%>" />
    
        <div class="lfr-form-content">
        
            <aui:fieldset-group markupView="<%= markupView %>">
            
                <aui:input name="title"/>
                <aui:input name="template"/>
                <aui:input name="fromAddress"/>
                <aui:input name="fromName"/>
                <aui:input name="useHttps"/>
        
            </aui:fieldset-group>
        </div>
                           
        <aui:button-row>
            <aui:button cssClass="btn-lg" disabled="<%= !hasUpdatePermission %>" type="submit" />           
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
        
    </aui:form>
</div>
