<%@ page import="com.fullmetalgalaxy.server.*" %>

<div id="mymenu" style="margin:5px; float:right; color:white;">
	<% if(Auth.isUserLogged(request,response)) { %>
	    <%= Auth.getUserPseudo(request,response) %> :
	    <% if(Auth.isUserAdmin(request, response)) { %>
	    
<SCRIPT language="Javascript">
  <!--
var timeout = 500;
var closetimer  = 0;
var ddmenuitem  = 0;

// open hidden layer
function mopen(id)
{ 
  mcancelclosetime();

  // close old layer
  if(ddmenuitem) ddmenuitem.style.visibility = 'hidden';

  // get new layer and show it
  ddmenuitem = document.getElementById(id);
  ddmenuitem.style.visibility = 'visible';

}
// close showed layer
function mclose()
{
  if(ddmenuitem) ddmenuitem.style.visibility = 'hidden';
}

// go close timer
function mclosetime()
{
  closetimer = window.setTimeout(mclose, timeout);
}

// cancel close timer
function mcancelclosetime()
{
  if(closetimer)
  {
    window.clearTimeout(closetimer);
    closetimer = null;
  }
}

// close layer when click-out
document.onclick = mclose; 
  // -->
</SCRIPT>
		<span style="position: relative;">
	    
	    	<a href="https://appengine.google.com/dashboard?&app_id=fullmetalgalaxy2"
	    	onmouseover="mopen('menuAdmin')" onmouseout="mclosetime()">Admin</a> |
	    	<div id="menuAdmin" class="bloc" style="visibility: hidden; position: absolute; text-align:left; top:10px; left:-10px; width:150px;"
		      onmouseover="mcancelclosetime()" 
		      onmouseout="mclosetime()">
			<a target="_blank" href="https://appengine.google.com/dashboard?&app_id=fullmetalgalaxy2">App Engine</a><br/>
		    <a target="_blank" href="https://www.google.com/analytics/reporting/?reset=1&id=13711373">Analytics</a><br/>
			<a target="_blank" href="http://code.google.com/p/fullmetalgalaxy/">Google code</a><br/>
			<hr/>
		    <a target="_blank" href="https://www.ovh.com/managerv3/">ManagerV3 OVH</a> <br/>
		    <a target="_blank" href="https://www.google.com/webmasters/tools/">Web Tools</a><br/>
			<a target="_blank" href="https://www.google.com/a/fullmetalgalaxy.com">Apps FMG</a><br/>
			<a target="_blank" href="http://latest.fullmetalgalaxy2.appspot.com/">Autre version</a><br/>
		  </div>
		</span>	    	
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