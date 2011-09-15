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
		<meta name='gwt:property' id='fmp_gameid' content='<%= id %>' />
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
        
		<meta name='gwt:property' id='ChatEngine' />
		<meta name='gwt:property' id='GameEngine' />
		
    </head>

    <body style="width:100%; height:100%; background:url(images/css/bloc-bkg.png) repeat;">
    
    <div style="position:fixed; left:0px; width:100%; min-height:50px; background-image:url('images/css/navbar-bkg.png');">
	    <div id="status" style="width:100%;"></div>
		<div id="mymenu" style="width:30px; margin:5px; float:right; color:white;">
		<%@include file="/include/mytopmenu.jsp"%>
		</div>
    </div>

    <table width="100%" height="100%" border="0" rules="none" cellspacing="0" cellpadding="0">
    <tr><td>
    	<div style="min-height:50px;"></div>
    </td></tr>
    <tr style="height:100%;"><td>
    	<div id="board" style="width:100%; height:100%; background-color:#d09750;"></div>
    </td></tr>
    </table>
    
    <div id="tabmenu" class="bloc" style="height:376px; position:fixed; top:10%; left:0px; z-index:9999999999;" ></div>
    <div id="context" style="position:fixed; bottom:0px; right:0px; z-index:9999999998;"></div>
    <div id="LittlePresences" style="position:fixed; bottom:0px; right:0px; z-index:9999999999;"></div>
    <div id="MessagesStack" style="position:fixed; bottom:205px; right:0px; z-index:9999999999;"></div>
 
        
        <script type="text/javascript" language="javascript" src="game/game.nocache.js"></script>
<%@include file="include/analiticscript.html"%>
 
    </body>
</html>

