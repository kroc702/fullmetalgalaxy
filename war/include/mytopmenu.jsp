<%@ page import="com.fullmetalgalaxy.server.*" %>

<div id="mymenu" style="margin:5px; float:right; color:white;">
	<% if(Auth.isUserLogged(request,response)) { %>
	    <%= Auth.getUserPseudo(request,response) %> :
	    <% if(Auth.isUserAdmin(request, response)) { %>
	    	<a href="https://appengine.google.com/custompage?url=/admin/index.jsp&app_id=fullmetalgalaxy2">Admin</a> |
	    <% } %>
	    <a HREF="/account.jsp" >
	    	<img style="border=none" border=0 src="/images/css/icon_user.cache.png" alt="" />&nbsp;Mon&nbsp;profil
	    </a> |
	    <a href="<%= Auth.getLogoutURL(request,response) %>" >
	        <img style="border=none" border=0 src="/images/css/icon_power.cache.png" alt="" />&nbsp;D&eacute;connexion
	    </a>
	<% } else { %>
		<a href="<%= Auth.getFmgLoginURL(request,response) %>" >
	        <img style="border=none" border=0 src="/favicon.ico" alt="FMG" />&nbsp;Connexion
	    </a> |
		<a href="<%= Auth.getGoogleLoginURL(request,response) %>" >
	        <img style="border=none" border=0 src="/images/icon_google.cache.ico" alt="Google" />&nbsp;Connexion 
	    </a>
	<% } %>
</div>