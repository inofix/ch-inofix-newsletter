<%--
    edit_mailing.jsp: edit a single mailing.

    Created:     2017-09-05 23:10 by Christian Berndt
    Modified:    2017-09-05 23:10 19:19 by Christian Berndt
    Version:     1.0.0
--%>

<%@ include file="/init.jsp"%>

<%@page import="java.util.Calendar"%>

<%
    Mailing mailing = (Mailing) request.getAttribute(NewsletterWebKeys.MAILING);

    String title = LanguageUtil.get(request, "new-mailing");

    boolean hasUpdatePermission = false;
    boolean hasViewPermission = false;
    boolean hasDeletePermission = false;
    boolean hasPermissionsPermission = false;

    if (mailing != null) {

        title = LanguageUtil.format(request, "edit-mailing-x",
                String.valueOf(mailing.getTitle()));

        hasUpdatePermission = MailingPermission.contains(permissionChecker, mailing,
                NewsletterActionKeys.UPDATE);
        hasViewPermission = MailingPermission.contains(permissionChecker, mailing,
                NewsletterActionKeys.VIEW);
        hasDeletePermission = MailingPermission.contains(permissionChecker, mailing,
                NewsletterActionKeys.DELETE);
        hasPermissionsPermission = MailingPermission.contains(permissionChecker, mailing,
                NewsletterActionKeys.PERMISSIONS);

    } else {

        mailing = MailingServiceUtil.createMailing();
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

    <portlet:actionURL name="updateMailing" var="updateMailingURL">
        <portlet:param name="mvcPath" value="/edit_mailing.jsp" />
    </portlet:actionURL>

    <aui:form method="post" action="<%=updateMailingURL%>" name="fm">
    
        <aui:input name="cmd" type="hidden" 
            value="<%= Constants.UPDATE %>"/>
        <aui:input name="userId" type="hidden"
            value="<%=String.valueOf(themeDisplay.getUserId())%>" />
    
        <aui:model-context bean="<%=mailing%>"
            model="<%=Mailing.class%>" />
    
        <div class="lfr-form-content">
        
            <aui:fieldset-group markupView="<%= markupView %>">
            
                <aui:input name="title"/>
                <aui:input name="template"/>
                <aui:input name="newsletterId"/>
                <aui:input name="articleId"/>
                <aui:input name="articleGroupId"/>
                <aui:input name="publishDate"/>
                <aui:input name="sent"/>
                <aui:input name="sendDate"/>
        
            </aui:fieldset-group>
        </div>
                           
        <aui:button-row>
            <aui:button cssClass="btn-lg" disabled="<%= !hasUpdatePermission %>" type="submit" />           
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
        
    </aui:form>
</div>
