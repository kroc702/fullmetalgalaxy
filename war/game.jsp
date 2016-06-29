<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
   
<html style="width:100%; height:100%;">
    <head>
        <title>Full Metal Galaxy - Planetes</title>

        <meta http-equiv="cache-control" content="no-cache">
        <meta http-equiv="pragma" content="no-cache">

        <%@include file="include/meta.jsp"%>
	    <%
	      Presence presence = new Presence();
	      String channelToken = null;
	      String room = null;
	      if( id != 0 && !withoutChannel) {
	      		// if id != 0, then it's a multiplayer game
	    		channelToken = ChannelManager.connect(pseudo,id,Presence.ClientType.GAME,presence);
	    	    room = Serializer.escape(Serializer.toClient( ChannelManager.getRoom(id) ));
	      }
	      ModelFmpInit modelFmpInit = GameServicesImpl.sgetModelFmpInit(request,response,request.getParameter("id"));
	      
	      if( Auth.isUserLogged( request, response ) && modelFmpInit != null && modelFmpInit.getGame() != null && !modelFmpInit.getGame().getCurrentPlayerIds().isEmpty() ) {
	        Game game = modelFmpInit.getGame();
	        EbRegistration currentRegistration = game.getRegistration( game.getCurrentPlayerIds().get( 0 ) );
	        if( game.getStatus() == GameStatus.Running && currentRegistration.haveAccount() && currentRegistration.getAccount().isAI()) {
	          WebHook webhook = new WebHook( game.getId(), currentRegistration.getAccount().getId() );
	          webhook.staiExtraStatements = "retry, manual by user "+Auth.getUserPseudo( request, response ) +" from game page";
	          webhook.setStartDelayMillis(4000);
	          webhook.start();
	        }
	      }
	      
	      String model = Serializer.escape(Serializer.toClient( modelFmpInit ));
	    %>
		<meta name='gwt:property' id='fmp_channelToken' content='<%= channelToken %>' />
		<meta name='gwt:property' id='fmp_pageid' content='<%= presence.getPageId() %>' />
		<meta name='gwt:property' id='fmp_gameid' content='<%= id %>' />
		<script type="text/javascript" language="javascript">
			var fmp_room='<%= room %>';
			var fmp_model='<%= model %>';
		</script>

        <style type="text/css">@import url( /appMain.css );</style>
        
        <% if( id != 0 ) { %>
		<meta name='gwt:property' id='ChatEngine' />
		<% } %>
		<meta name='gwt:property' id='GameEngine' />
		
    </head>

    <body style="width:100%; height:100%; top: 0px; position: fixed; background:url(images/css/bloc-bkg.png) repeat;" scroll=no>
    
    <div style="position:fixed; left:0px; top:0px; width:100%; height:60px; background-image:url('images/css/navbar-bkg.png');">
	    <div id="status" style="width:100%;"></div>
		<div id="mymenu" style="width: 100%; margin:5px; float:right; color:white;">
		<%@include file="/include/mytopmenu.jsp"%>
		</div>
    </div>

<div id="board" style="width:100%; height:100%; height:calc(100% - 60px); margin-top: 60px; background-color:hsl(32, 12%, 44%);"></div>
    
    <%-- _position:... and expression(... are for IE only --%>
    <div id="tabmenu" class="bloc" style="height:376px; position:fixed; top:10%; left:0px; _position: absolute; top:expression(body.scrollTop + 100 +'px'); left:expression(body.scrollLeft +'px'); z-index:9999999999;" ></div>
    <div id="context" style="position:fixed; bottom:0px; right:0px; _position: absolute; bottom:expression(0 - body.scrollTop +'px'); right:expression(0 - body.scrollLeft +'px'); z-index:9999999998;"></div>
    <div id="MessagesStack" style="position:fixed; bottom:205px; right:0px; _position: absolute; bottom:expression(205 - body.scrollTop +'px'); right:expression(0 - body.scrollLeft +'px'); z-index:9999999999;"></div>

	<% if( id != 0 ) { %>
    <div id="LittlePresences" style="position:fixed; bottom:0px; right:0px; _position: absolute; bottom:expression(0 - body.scrollTop +'px'); right:expression(0 - body.scrollLeft +'px'); z-index:9999999999;"></div>
 	<% } %>
        
        <script type="text/javascript" language="javascript" src="game/game.nocache.js"></script>
<%@include file="include/analiticscript.html"%>
 
    </body>
</html>

