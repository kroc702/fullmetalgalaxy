<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.server.forum.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<head>
<title>Full Metal Planete/Galaxy Online</title>

<style type="text/css">@import url( /style.css );</style>


</head>
<body>

<div style="display:none">
Full Metal Planete ou Full Metal Galaxy est un jeu web gratuit de strategie et tactique, 
ou wargame online par navigateur.
Il ce joue tour par tour ou en mode asynchrone.
Vous déplacez vos pions sur une carte pavée d'hexagones.
</div>


<%@include file="include/mytopmenu.jsp"%>

<div style="width:999px; margin: 0 auto; text-align: left;">
<div style="position:absolute;">

<jsp:include page="<%= I18n.localize(request,response,\"/index.html\") %>" />

<div id="menu" class="bloc" style="width:180px; height:280px; position:absolute; top:130px; left:0px;" >
   <jsp:include page="<%= I18n.localize(request,response,\"/menu.html\") %>" />
</div>

<div id="news" class="bloc" style="width:290px; height:280px; position:absolute; top:130px; left:665px; font-size: 8pt;" >
<%= News.getHtml() %>

<br/>
compte: <%= GlobalVars.getAccountCount() %>

</div>

</div>
</div>

<%@include file="include/analiticscript.html"%>

</body>
</HTML>