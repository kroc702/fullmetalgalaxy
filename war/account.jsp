<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.server.datastore.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Détail du compte</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
if( Auth.isUserAdmin( request, response ) )
{
	try
	{
		id = Long.parseLong( request.getParameter( "id" ) );
	} catch( NumberFormatException e )
	{
		id = Auth.getUserAccount( request, response ).getId();
	}
}
else if( Auth.isUserLogged( request, response ) )
{
  id = Auth.getUserAccount( request, response ).getId();
}
PersistAccount pAccount = FmgDataStore.sgetPersistAccount( id );
EbAccount account = new EbAccount();
account.reinit();
if(pAccount != null) {
	account = pAccount.getAccount();
} else {
    pAccount = new PersistAccount();
    account.setAllowMailFromGame( true );
    account.setAllowMailFromNewsLetter( true );
    account.setAllowPrivateMsg( true );
} 
%>

<% if( id == 0 ) { %>
<h2>Création d'un nouveau compte</h2>
Vous pouvez aussi utiliser votre compte google pour vous 
<a href="<%= Auth.getGoogleLoginURL(request,response) %>" >connecter</a> a Full Metal Galaxy.
<%}%>


<h1><%= (request.getParameter("msg")==null) ? "" : request.getParameter("msg") %></h1>



<form name="myform" action="/AccountServlet" method="post" enctype="multipart/form-data" accept-charset="utf-8">

<input type="hidden" name="accountid" value="<%= pAccount.getId() %>"/>
<input type="hidden" name="authprovider" value="<%= pAccount.getAuthProvider() %>"/>
login :
<input type="text" <%= (id == 0) ? "" : "readonly" %> name="login" value="<%= account.getLogin() %>"/>
<%= account.getAuthIconHtml() %><br/>

<% if( pAccount.getAuthProvider() == AuthProvider.Fmg ) {%>
	<% if(Auth.isUserAdmin(request, response)) { %>
		mot de passe :
		<input type="text" name="password1" value="<%= pAccount.getPassword() %>"/><br/>
		confirmation :
		<input type="text" name="password2" value="<%= pAccount.getPassword() %>"/><br/>
		pseudo :
		<input type="text" name="pseudo" value="<%= account.getPseudo() %>"/><br/>
	<% } else { %>
		mot de passe :
		<input type="password" name="password1" value=""/><br/>
		confirmation :
		<input type="password" name="password2" value=""/><br/>
	<% } %>
<%} else if( account.canChangePseudo() || Auth.isUserAdmin(request, response) ) {%>
	pseudo :
	<input type="text" name="pseudo" value="<%= account.getPseudo() %>"/> ! Vous ne pourrez le modifier qu'une seule fois !<br/>
<%} else {%>
	pseudo :
	<input type="text" readonly name="pseudo" value="<%= account.getPseudo() %>"/><br/>
<%}%>

<br/>
email :
<input type="text" name="email" value="<%= account.getEmail() %>"/><br/>
Autoriser FMG a envoyer un mail pour signaler votre tour de jeu
<input type="checkbox" <%= account.isAllowMailFromGame() ? "checked" : "" %> name="AllowMailFromGame" value="1"><br/>
Autoriser les autres joueurs a vous contacter par messages privés
<input type="checkbox" <%= account.isAllowPrivateMsg() ? "checked" : "" %> name="AllowPrivateMsg" value="1"><br/>
Autoriser FMG a vous informer des évolutions majeurs
<input type="checkbox" <%= account.isAllowMailFromNewsLetter() ? "checked" : "" %> name="AllowMailFromNewsLetter" value="1"><br/>
<br/>
Jabber ID :
<input type="text" name="jabberid" value="<%= account.getJabberId() %>"/><br/>
<br/>
Description publique :<br/>
<textarea cols="50" rows="10" name="description">
<%= account.getDescription() %>
</textarea><br/>



<input type="submit" name="Submit" value="Enregistrer"/>
<input type="reset" value="Annuler">
<% if(Auth.isUserAdmin(request, response)) {
	out.println("<a href=\"/admin/Servlet?deleteaccount="+account.getId()+"\">effacer</a>" );
} %>
</form>

<%@include file="include/footer.jsp"%>
</body>
</html>
