<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html lang="<%= I18n.getLocale(request,response) %>">
    <head>
        <title>Full Metal Galaxy - Planetes</title>

        <meta http-equiv="cache-control" content="no-cache">
        <meta http-equiv="pragma" content="no-cache">

        <%@include file="include/meta.jsp"%>
        <style type="text/css">@import url( /appMain.css );</style>
        <meta name='gwt:property' id='GameEngine' />
        
    </head>

<body>
<jsp:include page="include/header.jsp" />
    
    <div id="new" style="display:none; width:100%; height:100%; "></div>
    
    <script type="text/javascript" language="javascript" src="game/game.nocache.js"></script>

<jsp:include page="include/footer.jsp" />
</body>
</html>
