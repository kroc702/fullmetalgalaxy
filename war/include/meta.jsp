
<%@ page import="com.fullmetalgalaxy.server.*, com.fullmetalgalaxy.model.*" %>
<style type="text/css">@import url( /style.css );</style>

<meta name="author" content="Vincent Legendre" />
<meta name="keywords" content="full, metal, planete, jeu, online, strategie" />
<meta name="description" content="Full Metal Galaxy est un jeu de strategie online par navigateur bas&eacute; sur le jeu de plateau Full Metal Planete" />

<%
long id = 0;
try
{
	id = Long.parseLong(request.getParameter("id"));
} catch(Exception e) {}
String pseudo = Auth.getUserPseudo(request,response);
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
