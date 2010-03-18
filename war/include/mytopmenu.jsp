<%@ page import="com.fullmetalgalaxy.server.*" %>

<div id="mymenu" style="margin:5px; float:right; color:white;">
	<% if(Auth.isUserLogged(request,response)) { %>
	    <%= Auth.getUserPseudo(request,response) %> :
	    <% if(Auth.isUserAdmin(request, response)) { %>
	    	<a href="https://appengine.google.com/custompage?name=Index&app_id=fullmetalgalaxy2">Admin</a> |
	    <% } %>
	    <a HREF="/account.jsp" >
	    	<img style="border=none" border=0 src="/images/css/user.gif" alt="" />&nbsp;Mon&nbsp;profil
	    </a> |
	    <a href="<%= Auth.getLogoutURL(request,response) %>" >
	        <img style="border=none" border=0 src="/images/css/power.gif" alt="" />&nbsp;D&eacute;connexion
	    </a>
	<% } else { %>
		<a href="<%= Auth.getFmgLoginURL(request,response) %>" >
	        <img style="border=none" border=0 src="/favicon.ico" alt="FMG" />&nbsp;Connexion
	    </a> |
		<a href="<%= Auth.getGoogleLoginURL(request,response) %>" >
	        <img style="border=none" border=0 src="http://www.google.com/favicon.ico" alt="Google" />&nbsp;Connexion 
	    </a>
	<% } %>
</div>