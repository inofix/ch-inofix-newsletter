<%--
    display_styles_buttons.jsp: Select the display style of the newsletter manager's panels.
    
    Created:    2017-10-13 23:41 by Christian Berndt
    Modified:   2017-10-13 23:41 by Christian Berndt
    Version:    1.0.0
--%>

<%@ include file="/init.jsp" %>

<%
    String navigation = ParamUtil.getString(request, "navigation", "all");

    String displayStyle = ParamUtil.getString(request, "displayStyle");

    if (Validator.isNull(displayStyle)) {
        displayStyle = portalPreferences.getValue(PortletKeys.NEWSLETTER_MANAGER, "display-style", "list");
//         displayStyle = portalPreferences.getValue(PortletKeys.TIMETRACKER, "display-style", "descriptive");
    }
    
    PortletURL displayStyleURL = renderResponse.createRenderURL();
    // TODO: implement displayStyle descriptive
%>

<liferay-frontend:management-bar-display-buttons
    displayViews='<%= new String[] {"list"} %>'
    portletURL="<%= displayStyleURL %>"
    selectedDisplayStyle="<%= displayStyle %>"
/>
