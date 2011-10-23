<%@ page import="com.fullmetalgalaxy.server.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>

<div id="login">
	<% if(Auth.isUserLogged(request,response)) { %> 
	    <% if(Auth.isUserAdmin(request, response)) { %>
		<span id="linkPseudo" style="position: relative;">
	    	<a href="http://fullmetalplanete.forum2jeux.com/profile?mode=editprofile">
	    	<img style="border:none" border=0 src="/images/css/icon_user.cache.png" alt="" />&nbsp;<%= Auth.getUserPseudo(request,response) %>
	    	</a> |
		  <div id="menuAdmin" class="bloc" style="position: absolute; text-align:left; top:15px; left:-10px; width:150px; z-index:9999999999;">
			<a target="_blank" href="https://appengine.google.com/dashboard?&app_id=fullmetalgalaxy2">App Engine</a><br/>
		    <a target="_blank" href="https://www.google.com/analytics/reporting/?reset=1&id=13711373">Analytics</a><br/>
			<a target="_blank" href="http://code.google.com/p/fullmetalgalaxy/issues/list">Google code</a><br/>
			<hr/>
		    <a target="_blank" href="https://www.ovh.com/managerv3/">ManagerV3 OVH</a> <br/>
		    <a target="_blank" href="https://www.google.com/webmasters/tools/">Web Tools</a><br/>
			<a target="_blank" href="https://www.google.com/a/fullmetalgalaxy.com">Apps FMG</a><br/>
			<a target="_blank" href="http://latest.fullmetalgalaxy2.appspot.com/">Autre version</a><br/>
			<hr/>
		    <a href="/auth.jsp">log to other user</a> <br/>
		    <a href="/AccountServlet?logout=fmgonly">log back to admin</a> <br/>
		  </div>
		</span>	    	
	    <% } else { %>
	    <a HREF="/account.jsp" >
	    	<img style="border=none" border=0 src="/images/css/icon_user.cache.png" alt="" />&nbsp;<%= Auth.getUserPseudo(request,response) %>
	    </a> |
	    <% } %>
	    <a href="<%= Auth.getLogoutURL(request,response) %>" >
	        <img style="border=none" border=0 src="/images/css/icon_power.cache.png" alt="" />&nbsp;D&eacute;connexion
	    </a>
	<% } else { %>
	<form id="login" method="POST" action="/AccountServlet" enctype="multipart/form-data">
		<div>
			<span><label>Username</label><input type="text" name="login" size="15" /></span>
            <span><label>Password</label><input type="password" name="password" size="15" /></span>
			<input type="hidden" name="connexion" value="1"/>
			<input type="hidden" name="continue" value="<%= (request.getParameter("continue")==null) ? "/" : request.getParameter("continue") %>"/>
			<span><input id="login-submit" type="submit" name="Submit" value="Go!" /></span>
		</div>
	</form>
	<div id="loginlinks">
	<a href="<%= Auth.getGoogleLoginURL(request,response) %>" >
        <img style="border=none" border=0 src="/images/icon_google.cache.ico" alt="Google" />&nbsp;Connexion avec google 
    </a>
    <a href="/account.jsp"><img style="border=none" border=0 src="/images/logo16.png" alt="FMG" />&nbsp;Inscription</a>
    <a href="/password.jsp"><img style="border=none" border=0 src="/images/ask16.png" alt="Ask" />&nbsp;Identifiants oubliés</a>
    </div>
	<% } %> 	
</div>