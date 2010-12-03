
<%@ page import="com.fullmetalgalaxy.server.*" %>
<style type="text/css">@import url( /style.css );</style>

<meta name="author" content="Vincent Legendre" />
<meta name="keywords" content="full, metal, planete, jeu, online, strategie" />
<meta name="description" content="Full Metal Galaxy est un jeu de strategie online par navigateur bas&eacute; sur le jeu de plateau Full Metal Planete" />


<% if( Auth.isUserLogged(request,response) ) { %>
<meta name='gwt:property' id='fmp_userlogin' content='<%= Auth.getUserLogin(request,response) %>' />
<meta name='gwt:property' id='fmp_userpseudo' content='<%= Auth.getUserPseudo(request,response) %>' />
<meta name='gwt:property' id='fmp_userid' content='<%= Auth.getUserAccount(request,response).getId() %>' />
	<% if( Auth.isUserAdmin(request,response) ) { %>
<meta name='gwt:property' id='fmp_useradmin' content='true' />
	<% } %>
<% } else { %>
<% } %>

<%-- 
<meta name="gwt:property" content="locale=<%=request.getLocale().getLanguage()%>_<%=request.getLocale().getCountry()%>" />
 --%>
<meta name="gwt:property" content="locale=fr" />
