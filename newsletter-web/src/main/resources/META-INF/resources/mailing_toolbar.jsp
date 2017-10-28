<%--
    mailing_toolbar.jsp: The toolbar of the mailing panel
    
    Created:    2017-10-13 23:37 by Christian Berndt
    Modified:   2017-10-28 19:39 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp"%>

<%
    String[] columns = (String[])request.getAttribute("view_mailings.jsp-columns");

    String orderByCol = ParamUtil.getString(request, "orderByCol", "modified-date");

    String orderByType = ParamUtil.getString(request, "orderByType", "desc");

    String searchContainerId = ParamUtil.getString(request, "searchContainerId");
    
    int total = GetterUtil.getInteger(request.getAttribute("view_mailings.jsp-total"));
    
    PortletURL portletURL = renderResponse.createRenderURL();
    portletURL.setParameters(renderRequest.getParameterMap());
%>

<liferay-frontend:management-bar
    disabled="<%= total == 0 %>"
    includeCheckBox="<%= true %>"
    searchContainerId="<%= searchContainerId %>"
>

    <liferay-frontend:management-bar-filters>
        <liferay-frontend:management-bar-sort
            orderByCol="<%= orderByCol %>"
            orderByType="<%= orderByType %>"
            orderColumns='<%= columns %>'
            portletURL="<%= portletURL %>"
        />
    </liferay-frontend:management-bar-filters>

    <liferay-frontend:management-bar-buttons>
        <liferay-util:include page="/display_style_buttons.jsp"
            servletContext="<%=application%>" />
    </liferay-frontend:management-bar-buttons>

    <liferay-frontend:management-bar-action-buttons>
    
        <liferay-ui:icon-menu cssClass="pull-left">
            <liferay-ui:icon iconCssClass="icon-remove" message="delete" url="<%= "javascript:" + renderResponse.getNamespace() + "deleteEntries();" %>" />
        </liferay-ui:icon-menu>

    </liferay-frontend:management-bar-action-buttons>
</liferay-frontend:management-bar>

<aui:script>
    function <portlet:namespace />deleteEntries() {
        if (confirm('<%= UnicodeLanguageUtil.get(request, "are-you-sure-you-want-to-delete-the-selected-entries") %>')) {
            var form = AUI.$(document.<portlet:namespace />fm);

            form.attr('method', 'post');
            form.fm('className').val('<%= Mailing.class.getName() %>');
            form.fm('<%= Constants.CMD %>').val('<%= Constants.DELETE %>');
            form.fm('deleteMailingIds').val(Liferay.Util.listCheckedExcept(form, '<portlet:namespace />allRowIds'));

            submitForm(form);
        }
    }
</aui:script>
