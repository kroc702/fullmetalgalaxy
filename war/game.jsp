<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!--DOCTYPE html-->

<html>
    <head>
        <title>Full Metal Galaxy - Planetes</title>

        <meta http-equiv="cache-control" content="no-cache">
        <meta http-equiv="pragma" content="no-cache">

        <%@include file="include/meta.jsp"%>
	    <%
	      Presence presence = new Presence();
	    		String channelToken = ChannelManager.connect(pseudo,id,Presence.ClientType.GAME,presence);
	    	    String room = Serializer.escape(Serializer.toClient( ChannelManager.getRoom(id) ));
	    	    String model = Serializer.escape(Serializer.toClient( GameServicesImpl.sgetModelFmpInit(request.getParameter("id")) ));
	    %>
		<meta name='gwt:property' id='fmp_channelToken' content='<%= channelToken %>' />
		<meta name='gwt:property' id='fmp_pageid' content='<%= presence.getPageId() %>' />
		<script type="text/javascript" language="javascript">
			var fmp_room='<%= room %>';
			var fmp_model='<%= model %>';
		</script>

		<script type="text/javascript" language="javascript">
		    <%-- This is to prevent browser native scroll on some devices --%>
			document.body.addEventListener('touchmove', function(e){ e.preventDefault(); }); 
			document.body.addEventListener('touchstart', function(e){ e.preventDefault(); });
			document.ontouchmove = function(e){ e.preventDefault(); }
		</script>

        <style type="text/css">@import url( /appMain.css );</style>
        
        <meta name='gwt:property' id='app_history' content='idGame_<%= request.getParameter( "id" ) %>_status__messages__context__advises__tabmenu__board__'>
        
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
				<div id="mymenu" style="width:30px; margin:5px; float:right; color:white;">
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

        <div id="board" style="display:none; width:100%; height:100%; background-color:#d09750;"></div>

    </td></tr>
    </table>

        <div id="tabmenu" class="bloc" style="height:376px; position:absolute; top:130px; left:0px; z-index:9999999999;"></div>
    
        <div id="context" style="display:none; position:absolute; bottom:0px; right:0px; z-index:9999999998;"></div>
    
        <div id="messages" style="display:none; position:absolute; bottom:205px; right:0px; z-index:9999999999;"></div>
        
        <div id="debug" style="display:block; position:absolute; top:50px; left:50px; z-index:9999999999;"></div>
        
        <script type="text/javascript" language="javascript" src="game/game.nocache.js"></script>

<%@include file="include/analiticscript.html"%>
 
    </body>
</html>

