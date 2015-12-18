<%@ page import="com.fullmetalgalaxy.server.*, com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>

<meta name="author" content="Vincent Legendre" />
<meta name="keywords" content="full, metal, planete, jeu, online, strategie" />
<meta name="description" content="Full Metal Galaxy est un jeu de strategie online par navigateur bas&eacute; sur le jeu de plateau Full Metal Planete" />

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<!-- Tile color for Win8 -->
<meta name="msapplication-TileColor" content="#ffffff">

<!-- Add to homescreen for Chrome on Android -->
<meta name="mobile-web-app-capable" content="yes">
<meta name="application-name" content="Full Metal Galaxy">
<link rel="icon" sizes="192x192" href="images/logo_192.png">

<!-- Add to homescreen for Safari on iOS -->
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="white">
<meta name="apple-mobile-web-app-title" content="Full Metal Galaxy">
<link rel="apple-touch-icon" href="images/logo_192.png">

<!-- Tile icon for Win8 (144x144) -->
<meta name="msapplication-TileImage" content="images/logo_144.png">

<link rel="stylesheet" href="/style.css" type="text/css" media="screen" />
<!--[if lte IE 7]>
<link rel="stylesheet" href="/style_ie7.css" type="text/css" media="screen" />
<![endif]-->

<%
long id = 0;
try
{
	id = Long.parseLong(request.getParameter("id"));
} catch(Exception e) {}
String pseudo = Auth.getUserPseudo(request,response);
String channel = request.getParameter("channel");
boolean withoutChannel = (channel != null) && (channel.equalsIgnoreCase( "no" ));
%>
<meta name='gwt:property' id='fmp_userpseudo' content='<%= pseudo %>' />

<% if( Auth.isUserLogged(request,response) ) { %>
	<meta name='gwt:property' id='fmp_userid' content='<%= Auth.getUserAccount(request,response).getId() %>' />
	<% if( Auth.isUserAdmin(request,response) ) { %>
		<meta name='gwt:property' id='fmp_useradmin' content='true' />
	<% } %>
<% } else { %>
<% } %>


<meta name="gwt:property" content="locale=<%=I18n.getLocale(request,response)%>" />
<meta name='gwt:property' id='fmp_servertime' content='<%= System.currentTimeMillis() %>' />
