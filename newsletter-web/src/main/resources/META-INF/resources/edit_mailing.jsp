<%--
    edit_mailing.jsp: edit a single mailing.

    Created:     2017-09-05 23:10 by Christian Berndt
    Modified:    2017-11-01 00:45 by Christian Berndt
    Version:     1.0.4
--%>

<%@ include file="/init.jsp"%>

<%
    Mailing mailing = (Mailing) request.getAttribute(NewsletterWebKeys.MAILING);

    boolean disabled = false;

    String title = LanguageUtil.get(request, "new-mailing");

    boolean hasUpdatePermission = false;
    boolean hasViewPermission = false;
    boolean hasDeletePermission = false;
    boolean hasPermissionsPermission = false;

    if (mailing != null) {

        disabled = mailing.isSent(); 
        
        title = LanguageUtil.format(request, "edit-mailing-x", String.valueOf(mailing.getTitle()));

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

    boolean reverse = false;

    Sort sort = new Sort("title_sortable", reverse);

    Hits hits = NewsletterServiceUtil.search(themeDisplay.getUserId(), scopeGroupId, 0, null, 0, 20, sort);
    
    List<Newsletter> newsletters = NewsletterUtil.getNewsletters(hits);
    
    if (newsletters.size() == 0) {
        disabled = true; 
    }

    String redirect = ParamUtil.getString(request, "redirect");

    String backURL = ParamUtil.getString(request, "backURL", redirect);

    portletDisplay.setShowBackIcon(true);
    portletDisplay.setURLBack(redirect);

    renderResponse.setTitle(title);

    request.setAttribute("showTitle", "true"); // used by inofix-theme
%>

<div class="container-fluid-1280">

    <portlet:actionURL var="updateMailingURL">
        <portlet:param name="mvcPath" value="/edit_mailing.jsp" />
    </portlet:actionURL>

    <aui:form method="post" action="<%=updateMailingURL%>" name="fm">
    
        <aui:input name="cmd" type="hidden" 
            value="<%= Constants.UPDATE %>"/>
        <aui:input name="className" type="hidden"
            value="<%= Mailing.class.getName() %>" />
        <aui:input name="userId" type="hidden"
            value="<%=String.valueOf(themeDisplay.getUserId())%>" />
    
        <aui:model-context bean="<%=mailing%>"
            model="<%=Mailing.class%>" />
    
        <div class="lfr-form-content">
                            
            <c:if test="<%= newsletters.size() == 0 %>">
                <aui:alert type="warning" closeable="<%= false %>">
                    <liferay-ui:message key="you-must-create-a-newsletter-before-you-can-add-a-mailing"/>
                    <aui:a href="<%= redirect %>" label="back"/>
                </aui:alert>
            </c:if>
        
            <aui:fieldset-group markupView="<%= markupView %>">
            
                <aui:fieldset>
            
                    <aui:input name="backURL" type="hidden"
                        value="<%=backURL%>" />
    
                    <aui:input name="redirect" type="hidden"
                        value="<%=redirect%>" />
    
                    <aui:input name="mailingId" type="hidden"
                        disabled="<%=!hasUpdatePermission%>" />
                
                    <aui:input name="title" disabled="<%= disabled %>"/>
                    <aui:input name="template" disabled="<%= disabled %>"/>
                    
                    <aui:select name="newsletterId"
                        disabled="<%=disabled%>"
                        helpMessage="newsletter-id-help" label="newsletter"
                        inlineField="true">
                        <aui:option label="select-newsletter" value="0" />
                        <%
                            for (Newsletter newsletter : newsletters) {
                        %>
                        <aui:option label="<%=newsletter.getTitle()%>"
                            value="<%=newsletter.getNewsletterId()%>"
                            selected="<%=mailing.getNewsletterId() == newsletter
                                                .getNewsletterId()%>" />
                        <%
                            }
                        %>
                    </aui:select>  
                                  
                    <aui:input name="articleId" disabled="<%= disabled %>"/>
                    <aui:input name="articleGroupId" disabled="<%= disabled %>"/>
                    <aui:input name="publishDate" disabled="<%= disabled %>"/>
                    <aui:input name="sent" disabled="<%= true %>"/>
                    <aui:input name="sendDate"  disabled="<%= disabled %>"/>
                
                </aui:fieldset>
        
            </aui:fieldset-group>
        </div>
                           
        <aui:button-row>
            <aui:button cssClass="btn-lg" disabled="<%= !hasUpdatePermission || disabled %>" type="submit" />           
            <aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
        </aui:button-row>
        
    </aui:form>
</div>
