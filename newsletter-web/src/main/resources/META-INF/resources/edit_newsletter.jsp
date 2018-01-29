<%--
    edit_newsletter.jsp: edit a newsletter.

    Created:     2017-09-17 00:02 by Christian Berndt
    Modified:    2018-01-29 13:18 by Christian Berndt
    Version:     1.0.3
--%>

<%@ include file="/init.jsp"%>

<%
    String cmd = Constants.ADD; 
    
    String redirect = ParamUtil.getString(request, "redirect");

    Newsletter newsletter = (Newsletter) request.getAttribute(NewsletterWebKeys.NEWSLETTER);

    String title = LanguageUtil.get(request, "new-newsletter");

    boolean hasUpdatePermission = false;
    boolean hasViewPermission = false;
    boolean hasDeletePermission = false;
    boolean hasPermissionsPermission = false;

    if (newsletter != null) {
        
        cmd = Constants.UPDATE;

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

    String backURL = ParamUtil.getString(request, "backURL", redirect);

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(redirect);

    renderResponse.setTitle(title);

    request.setAttribute("showTitle", "true"); // used by inofix-theme
%>

<div class="container-fluid-1280">

    <portlet:actionURL name="editNewsletter" var="updateNewsletterURL">
        <portlet:param name="mvcRenderCommandName"
            value="editNewsletter" />
    </portlet:actionURL>

    <aui:form method="post" action="<%=updateNewsletterURL%>" name="fm">

        <aui:input name="<%=Constants.CMD%>" type="hidden"
            value="<%=cmd%>" />
        <aui:input name="redirect" type="hidden" value="<%=redirect%>" />
        <aui:input name="userId" type="hidden"
            value="<%=String.valueOf(themeDisplay.getUserId())%>" />

        <aui:model-context bean="<%=newsletter%>"
            model="<%=Newsletter.class%>" />

        <div class="lfr-form-content">

            <aui:fieldset-group markupView="<%= markupView %>">

                <aui:fieldset>
                    <aui:input name="backURL" type="hidden"
                        value="<%=backURL%>" />

                    <aui:input name="redirect" type="hidden"
                        value="<%=redirect%>" />

                    <aui:input name="newsletterId" type="hidden"
                        disabled="<%=!hasUpdatePermission%>" />

                    <aui:input name="title" />
                    <aui:input name="template" />
                    <aui:input name="fromAddress" />
                    <aui:input name="fromName" />
                    <aui:input name="useHttps"
                        helpMessage="use-https-help" />
                </aui:fieldset>

            </aui:fieldset-group>
        </div>

        <aui:button-row>
            <aui:button cssClass="btn-lg"
                disabled="<%= !hasUpdatePermission %>" type="submit" />
            <aui:button cssClass="btn-lg" href="<%= redirect %>"
                type="cancel" />
        </aui:button-row>

    </aui:form>
</div>
