<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - EMail</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<h1>Envoyer un email</h1>

<%
if( !Auth.isUserLogged( request, response ) )
{
	out.println("<h2>Vous devez être connecté pour envoyer un email a un autre joueur.</h2>" );
	return;
}
List<EbAccount> accounts = ServerUtil.findRequestedAccounts(request);
if( accounts.isEmpty() ) 
{ 
	out.println("<h2>Aucun destinataires.</h2>" );
	return;
}
String subject = request.getParameter("subject");
if( subject == null ) subject = "";
String msg = request.getParameter("msg");
if( msg == null ) msg = "";
%>

<p>Attention : votre email sera envoyé a votre/vos destinataire(s) pour lui permetre de répondre.</p>

<form name="myform" action="/PMServlet" method="post" enctype="multipart/form-data" accept-charset="utf-8">
<input type="hidden" name="fromid" value="<%= Auth.getUserAccount( request, response ).getId() %>"/>

<% for( EbAccount account : accounts ) { 
	if( !account.allowMsgFromPlayer() || !account.haveEmail() )
	{
		out.println("<h2>"+ account.getPseudo() + " ne souhaite pas être contacté par email.</h2>" );
	}
}%>
<% for( EbAccount account : accounts ) { 
	if( (account.allowMsgFromPlayer() || Auth.isUserAdmin(request, response)) && account.haveEmail() )
	{
		out.print("&nbsp;A : <img src='"+account.getAvatarUrl() +"' height='32px' /> "+ account.getPseudo()+"<br/>" );
		out.println("<input type='hidden' name='toid' value='"+ account.getId() +"'/>");
	} else if( !account.haveEmail() && Auth.isUserAdmin(request, response) ) {
		out.print("&nbsp;A : <img src='"+account.getAvatarUrl() +"' height='32px' /> "+ account.getPseudo()+" have no email<br/>" );
	}
}%>
Objet :
<input type="text" name="subject" value="<%=subject%>"/><br/>
<textarea cols="50" rows="10" name="msg"><%=msg%>
</textarea><br/>

<input type="submit" name="Submit" value="Envoyer"/>
<input type="reset" value="Annuler">
</form>

<%@include file="include/footer.jsp"%>
</body>
</html>
