<!-- DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"-->
<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>

<html>
    <head>
        <title>Full Metal Galaxy - Planetes</title>

        <meta http-equiv="cache-control" content="no-cache">
        <meta http-equiv="pragma" content="no-cache">

        <%@include file="include/meta.jsp"%>
        <style type="text/css">@import url( /appMain.css );</style>
        
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
				<%@include file="/include/mytopmenu.jsp"%>
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

        <div class="bloc" style="height:376px; position:absolute; top:130px; left:0px; z-index:9999999999;" >
            <div id="menu" style="float:left; width:180px;">
                <jsp:include page="<%= I18n.localize(request,response,\"/menu.html\") %>" />
            </div>
        </div>

    </td></tr>
    </table>
    
        
        <script type="text/javascript" language="javascript" src="game/game.nocache.js"></script>

<%@include file="include/analiticscript.html"%>
 
    </body>
</html>

