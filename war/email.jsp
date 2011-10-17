<%@page import="com.fullmetalgalaxy.server.EbAccount.AllowMessage"%>
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
EbAccount account = ServerUtil.findRequestedAccount(request);
if( account == null ) 
{ 
	out.println("<h2>Le profil " + request.getParameter( "id" ) + " n'a pas été trouvé.</h2>" );
	return;
}
if( !Auth.isUserLogged( request, response ) )
{
	out.println("<h2>Vous devez être connecté pour envoyer un email a un autre joueur.</h2>" );
	return;
}
if( account.getAllowMsgFromPlayer() == AllowMessage.No || !account.haveEmail() )
{
	out.println("<h2>" + account.getPseudo() + " ne souhaite pas être contacté par email.</h2>" );
	return;
}
String subject = request.getParameter("subject");
if( subject == null ) subject = "";
%>

&nbsp;A : <%= account.getPseudo() %><br/>

<form name="myform" action="/PMServlet" method="post" enctype="multipart/form-data" accept-charset="utf-8">
<input type="hidden" name="fromid" value="<%= Auth.getUserAccount( request, response ).getId() %>"/>
<input type="hidden" name="toid" value="<%= account.getId() %>"/>
Objet :
<input type="text" name="subject" value="<%=subject%>"/><br/>
<textarea cols="50" rows="10" name="msg">
</textarea><br/>

<input type="submit" name="Submit" value="Envoyer"/>
<input type="reset" value="Annuler">
</form>

<%@include file="include/footer.jsp"%>
</body>
</html>
