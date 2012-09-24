<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<%@taglib prefix="fmg" uri="/WEB-INF/classes/fmg.tld"%>
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
	<h2><fmg:resource key="account_createnewaccount"/></h2>
	<a href="<%= Auth.getGoogleLoginURL(request,response) %>" >
	<fmg:resource key="account_canusegoogleaccount"/></a>
<%} else if(account.isIsforumIdConfirmed() && account.getForumId() != null){ %>
	<img src='<%= account.getAvatarUrl() %>' border=0 alt='Avatar' style="float:right;">
	<a href="http://fullmetalplanete.forum2jeux.com/profile?mode=editprofile">Editer le profil du forum</a><br/>
	Voir mon profil public sur: 
	<a href="/profile.jsp?id=<%=account.getId()%>">FMG</a> 
	ou sur le 
	<a href="http://<%=FmpConstant.getForumHost()%>/u<%=account.getForumId()%>">Forum</a><br/>
<%} else if( account.getForumId() != null ){ 
		if( account.getForumKey() == null ) {%>
	Un message privé vous sera envoyé prochainement pour lier les comptes Forum et FMG<br/>
		<%} else {%>
	Un message privé vous a été envoyé pour lier les comptes Forum et FMG<br/>
		<% } %>	
	Si ce n'est pas le cas, merci de contacter l'administrateur.<br/>
	<a href="/profile.jsp?id=<%=account.getId()%>">Voir mon profil public.</a> 
<%} else {%>
	<a href="http://fullmetalplanete.forum2jeux.com/register">
	<img src="/images/icons/canceled32.png" border=0 />
	Nous vous conseillons de créer un compte sur le forum
	pour avoir un avatar et quelques autres options.</a><br/>
	<a href="/profile.jsp?id=<%=account.getId()%>">Voir mon profil public.</a> 
<%} %>

<% if( account.getFinshedGameCount() > 0 ) { %>
	<p>
	level: <%= account.getCurrentLevel() %>  <img src='<%= account.getGradUrl() %>'/>
	</p>
<%} %>

<form name="myform" action="/AccountServlet" method="post" enctype="multipart/form-data" accept-charset="utf-8">

<input type="hidden" name="accountid" value="<%= account.getId() %>"/>

<% if(Auth.isUserAdmin(request, response)) { %>
	update login : <input type="checkbox" name="credential"  value="1"  ><br/>
	Auth provider :
	<input type="text" name="authprovider" value="<%= account.getAuthProvider() %>"/>  (Google or Fmg)<br/>
	<fmg:resource key="account_login"/> :
	<input type="text" name="login" value="<%= account.getLogin() %>"/>
	<%= account.getAuthIconHtml() %><br/>
	<fmg:resource key="account_password"/> :
	<input type="text" name="password1" value="<%= account.getPassword() %>"/><br/>
	<fmg:resource key="account_confirm"/> :
	<input type="text" name="password2" value="<%= account.getPassword() %>"/><br/>
	<fmg:resource key="account_pseudo"/> :
	<input type="text" name="pseudo" value="<%= account.getPseudo() %>"/><br/>
	<fmg:resource key="account_avatarurl"/> :
	<input type="text" name="avatarurl" value="<%= account.getForumAvatarUrl()!=null ? account.getForumAvatarUrl() : "" %>"/><br/>  
	<% if( account.isIsforumIdConfirmed() && account.getForumId() != null )
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
	  } %>
	<hr/>
<% } else { %>
	<input type="hidden" name="authprovider" value="<%= account.getAuthProvider() %>"/>
	<fmg:resource key="account_login"/> :
	<input type="text" <%= (id == 0) ? "" : "readonly" %> name="login" value="<%= account.getLogin() %>"/>
	<%= account.getAuthIconHtml() %><br/>
	<% if( account.getAuthProvider() == AuthProvider.Fmg ) {%>
		<fmg:resource key="account_password"/> :
		<input type="password" name="password1" value=""/><br/>
		<fmg:resource key="account_confirm"/> :
		<input type="password" name="password2" value=""/><br/>
	<% } %>
	<% if( account.canChangePseudo() ) {%>
		<fmg:resource key="account_pseudo"/> :
		<input type="text" name="pseudo" value="<%= account.getPseudo() %>"/> ! Vous ne pourrez le modifier qu'une seule fois !<br/>
	<% } else if( !account.isTrancient() ) {%>
		<fmg:resource key="account_pseudo"/> :
		<input type="text" readonly name="pseudo" value="<%= account.getPseudo() %>"/><br/>
	<% } %>
<% } %>


<br/>
<fmg:resource key="account_email"/> :
<input type="text" name="email" value="<%= account.getEmail() %>"/><br/>
<fmg:resource key="account_jabberid"/> :
<input type="text" name="jabberId" value="<%= account.getJabberId() %>"/><br/>
<fmg:resource key="account_allowgamemessages"/> :
	<SELECT name="NotificationQty">
	<% for( EbAccount.NotificationQty notif : EbAccount.NotificationQty.values()) { %>
		<OPTION VALUE="<%=notif%>" <%= account.getNotificationQty()==notif ? "SELECTED" : "" %> ><%=notif%></OPTION>
	<% } %>
	</SELECT><br/>
<fmg:resource key="account_allowplayermessages"/> : <input type="checkbox" name="AllowMsgFromPlayer"  value="1" <%= account.allowMsgFromPlayer() ? "checked" : "" %> ><br/>
<% if( id == 0 ) { %>
<fmg:resource key="account_createforumaccount"/> <input type="checkbox" name="createforumaccount" value="1" checked /> 
<br/>
<% } %>
<br/>


<input type="submit" name="Submit" value="<fmg:resource key="account_save"/>"/>
<input type="reset" value="<fmg:resource key="account_cancel"/>">
<% if(Auth.isUserAdmin(request, response)) {
	out.println("<a href=\"/admin/Servlet?deleteaccount="+account.getId()+"\">delete</a>" );
} %>
</form>

<%@include file="include/footer.jsp"%>
</body>
</html>
