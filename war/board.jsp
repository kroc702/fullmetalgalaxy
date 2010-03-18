<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%
//ServerUtil.reinitConfig( getServletContext() );
%>

<html>
    <head>
        <title>Full Metal Galaxy - Planetes</title>

        <meta http-equiv="cache-control" content="no-cache">
        <meta http-equiv="pragma" content="no-cache">

        <%@include file="include/meta.jsp"%>
        <style type="text/css">@import url( /appMain.css );</style>
        
        <meta name='gwt:property' id='app_history' content='loginbtn__newlogin__status__list__'>
        
    </head>

    <body style="width:100%; height:100%; background:url(images/css/bloc-bkg.png) repeat;">
    
    <table width="100%" height="100%" border="0" rules="none" cellspacing="0" cellpadding="0" style="position:absolute; top:0px; right:0px; z-index:1;" >
    <tr height="50px"><td height="50px" style="height:50px; background-image:url('images/css/navbar-bkg.png'); background-repeat:repeat;">
        <table width="100%" height="40px"  border="0" rules="none" cellspacing="0" cellpadding="0">
        <tr style="height:40px;" >
            <td width="100%">
                <div id="status" style="display:none; width:100%;"></div>
            </td>
            <td>
				<div id="mymenu" style="width:200px; margin:5px; float:right; color:white;">
				
				<% if(Auth.isUserLogged(request,response)) { 
				%>
				    <%= Auth.getUserLogin(request,response) %> :
				    <a href="<%= Auth.getLogoutURL(request,response) %>" >
				        D&eacute;connexion
				    </a>
				<% } else { %>
					<a href="<%= Auth.getGoogleLoginURL(request,response) %>" >
				        Login
				    </a>
				<% } %>
				</div>
            </td>
        </tr>   
        </table>
    </td></tr>
    <tr><td>
    
        <table border="0" width="100%" height="100%" rules="none" cellspacing="0" cellpadding="0" style="position:absolute; top:60px; left:0px; z-index:0;" >
            <tr >
                <td>
                    <div style="width:220px;"></div>
                </td>
                <td width="100%" height="100%" >
                    <div id="list" style="display:none;" ></div>
                    <div id="new" style="display:none; width:100%; height:100%; "></div>
                </td>
            </tr>
        </table>

        <div id="board" style="display:none; width:100%; height:100%; background-color:#d09750;"></div>
        <div id="advises" style="display:none; position:absolute; top:50px; right:10px; z-index:9999999999;"></div>

        <div class="bloc" style="height:376px; position:absolute; top:130px; left:0px; z-index:9999999999;" >
            <div id="menu" style="float:left; width:180px;">
                <%@include file="include/menu.html"%>
            </div>
            <div style="height:100%; background-color:black; cursor: pointer; cursor: hand; float:left;" >
                <div id="switch" style="display:none; width:7px; height:100%;"></div>
            </div>
        </div>

    </td></tr>
    </table>
    
        <div id="context" style="display:none; position:absolute; bottom:0px; right:0px; z-index:9999999998;"></div>
    
        <div id="messages" style="display:none; position:absolute; bottom:205px; right:0px; z-index:9999999999;"></div>
        
        <div id="debug" style="display:block; position:absolute; top:50px; left:50px; z-index:9999999999;"></div>
        
        <script type="text/javascript" language="javascript" src="fullmetalgalaxy/fullmetalgalaxy.nocache.js"></script>

        <!-- OPTIONAL: include this if you want history support -->
        <iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>
        
<!-- 
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-6780889-1");
pageTracker._trackPageview();
} catch(err) {}</script>
 -->
    </body>
</html>

