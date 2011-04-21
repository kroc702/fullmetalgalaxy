<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Profil du joueur</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
EbAccount account = FmgDataStore.dao().find( EbAccount.class, id );
if( account == null ) 
{ 
	out.println("<h2>Le profil " + request.getParameter( "id" ) + " n'existe pas.</h2>" );
	return;
}
if( Auth.isUserAdmin( request, response ) )
{
	out.println("<a href=\"/account.jsp?id="+id+"\">editer</a><br/>" );
}
%>

<h2> <%= account.getPseudo() %> </h2>
<% if(account.isAllowPrivateMsg() && account.haveEmail()) { %>
<a href="/privatemsg.jsp?id=<%= account.getId()%>">Ecrire un message</a><br/>
<% } %>

<pre><%= account.getDescription() %></pre>


<%@include file="include/footer.jsp"%>
</body>
</html>
