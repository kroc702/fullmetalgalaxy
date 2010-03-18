
<%@ page import="com.fullmetalgalaxy.server.*" %>
<style type="text/css">@import url( /style.css );</style>

<META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=windows-1252">
<meta name="author" content="Vincent Legendre" />
<meta name="keywords" content="full, metal, planete, jeu, online, strategie" />
<meta name="description" content="Full Metal Galaxy est un jeu de strategie online par navigateur bas&eacute; sur le jeu de plateau Full Metal Planete" />


<% if( Auth.isUserLogged(request,response) ) { %>
<meta name='gwt:property' id='fmp_user' content='<%= Auth.getUserLogin(request,response) %>' />
<meta name='gwt:property' id='fmp_userid' content='<%= Auth.getUserAccount(request,response).getId() %>' />
<% } else { %>
<% } %>

<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<%-- 
<meta name="gwt:property" content="locale=<%=request.getLocale().getLanguage()%>_<%=request.getLocale().getCountry()%>" />
 --%>
<meta name="gwt:property" content="locale=fr" />
