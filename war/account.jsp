<%@page import="com.fullmetalgalaxy.server.EbAccount.AllowMessage"%>
<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Détail du compte</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
EbAccount account = null;
if( Auth.isUserAdmin( request, response ) )
{
	try
	{
		id = Long.parseLong( request.getParameter( "id" ) );
	} catch( NumberFormatException e )
	{
		id = Auth.getUserAccount( request, response ).getId();
	}
	if( id != 0 )
	{
		account = FmgDataStore.dao().get(EbAccount.class, id );
	}
}
else if( Auth.isUserLogged( request, response ) )
{
  account = Auth.getUserAccount( request, response );
  id = account.getId();
}
if(account == null) {
	account = new EbAccount();
	account.setPassword("");
	id = 0;
} 
%>

<h1><%= (request.getParameter("msg")==null) ? "" : request.getParameter("msg") %></h1>

<% if( id == 0 ) { %>
	<h2>Création d'un nouveau compte</h2>
	<a href="<%= Auth.getGoogleLoginURL(request,response) %>" >
	Vous pouvez aussi utiliser votre compte google pour vous connecter a Full Metal Galaxy.</a>
<%} else if(account.isIsforumIdConfirmed() && account.getForumId() != null){ %>
	<img src='<%= account.getAvatarUrl() %>' border=0 alt='Avatar' style="float:right;">
	<a href="http://fullmetalplanete.forum2jeux.com/profile?mode=editprofile">Editer le profil du forum</a><br/>
	Voir mon profil public sur: 
	<a href="/profile.jsp?id=<%=account.getId()%>">FMG</a> 
	ou sur le 
	<a href="http://<%=FmpConstant.getForumHost()%>/u<%=account.getForumId()%>">Forum</a><br/>
	<p>
	level: <%= account.getCurrentLevel() %>  <img src='<%= account.getGradUrl() %>'/>
	</p>
<%} else if( account.getForumId() != null){ %>
	Un message privé vous a été envoyé pour lier les comptes Forum et FMG<br/>
	Si ce n'est pas le cas, merci de contacter l'administrateur.<br/>
	<a href="/profile.jsp?id=<%=account.getId()%>">Voir mon profil public.</a> 
<%} else {%>
	<a href="http://fullmetalplanete.forum2jeux.com/register">
	<img src="/images/icons/canceled32.png" border=0 />
	Nous vous conseillons de créer un compte sur le forum
	pour facilier les contacts entre joueurs, avoir un avatar et quelques autres options.</a><br/>
	<a href="/profile.jsp?id=<%=account.getId()%>">Voir mon profil public.</a> 
<%} %>

<form name="myform" action="/AccountServlet" method="post" enctype="multipart/form-data" accept-charset="utf-8">

<input type="hidden" name="accountid" value="<%= account.getId() %>"/>
<input type="hidden" name="authprovider" value="<%= account.getAuthProvider() %>"/>

login :
<input type="text" <%= (id == 0) ? "" : "readonly" %> name="login" value="<%= account.getLogin() %>"/>
<%= account.getAuthIconHtml() %><br/>

<% if( account.getAuthProvider() == AuthProvider.Fmg ) {%>
	<% if(Auth.isUserAdmin(request, response)) { %>
		mot de passe :
		<input type="text" name="password1" value="<%= account.getPassword() %>"/><br/>
		confirmation :
		<input type="text" name="password2" value="<%= account.getPassword() %>"/><br/>
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
<% if( id == 0 ) { %>
<input type="checkbox" name="createforumaccount" value="1" checked /> Créer un compte sur le forum
<br/>
<% } else { %>
<br/>
AllowMsgFromGame : <%= account.getAllowMsgFromGame() %><br/>
AllowMsgFromPlayer : <%= account.getAllowMsgFromPlayer() %><br/>
NotificationQty : <%= account.getNotificationQty() %><br/>
<% } %>

<% if(Auth.isUserAdmin(request, response)) { %>
  Jabber ID :
  <input type="text" name="jabberId" value="<%= account.getJabberId() %>"/><br/>
  
<%
  if( account.isIsforumIdConfirmed() && account.getForumId() != null )
  {
	out.println("<a href=\"/admin/Servlet?pullaccount="+account.getId()+"\">pull data from forum</a><br/>" );
	out.println("<a href=\"/admin/Servlet?pushaccount="+account.getId()+"\">push data to forum</a><br/>" );
	out.println("<a href=\"/admin/Servlet?testpm="+account.getId()+"\">Send a test PM</a><br/>" );
  } else if( account.getForumId() != null ){
	out.println("<a href=\"/admin/Servlet?linkaccount="+account.getId()+"\">link existing forum account</a><br/>" );
	if( account.getForumKey() != null ){
		out.println("<a href=\"/admin/Servlet?linkpm="+account.getId()+"\">Send a link forum PM</a><br/>" );
	}
  } else {
    out.println("<a href=\"/admin/Servlet?linkaccount="+account.getId()+"\">pull account ID</a><br/>" );
    out.println("<a href=\"/admin/Servlet?createforumaccount="+account.getId()+"\">create forum account</a><br/>" );
  }
} %> <br/>

<input type="submit" name="Submit" value="Enregistrer"/>
<input type="reset" value="Annuler">
<% if(Auth.isUserAdmin(request, response)) {
	out.println("<a href=\"/admin/Servlet?deleteaccount="+account.getId()+"\">effacer</a>" );
} %>
</form>

<%@include file="include/footer.jsp"%>
</body>
</html>
