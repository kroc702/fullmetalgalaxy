<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <title>Full Metal Chat</title>
    
    <%@include file="include/meta.jsp" %>
    <%
	Presence presence = new Presence();
	String channelToken = ChannelManager.connect(pseudo,id,Presence.ClientType.CHAT,presence);
	String room = Serializer.escape(Serializer.toClient( ChannelManager.getRoom(id) ));
	EbGamePreview game = FmgDataStore.dao().find( EbGamePreview.class, id);
	String title = "Full Metal Chat";
	if( game != null )
	{
		title = game.getName();
	}
	%>
	<meta name='gwt:property' id='fmp_channelToken' content='<%= channelToken %>' />
	<meta name='gwt:property' id='fmp_pageid' content='<%= presence.getPageId() %>' />
	<meta name='gwt:property' id='fmp_gameid' content='<%= id %>' />
	<script type="text/javascript" language="javascript">
		var fmp_room='<%= room %>';
	</script>
    
    <style type="text/css">@import url( /appMain.css );</style>
	<meta name='gwt:property' id='ChatEngine' />
  </head>

  <body style="width:100%; height:100%;">
	<%@include file="include/header.jsp"%>

    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>

    <h3><%= title %></h3>

	<div id="Presences" style="float:right;"></div>
	<div id="Chat" style="height:100%; width:100%;"></div>
	
	<% if( id == 0 ) { %>
	<p>
	Tapez '/?' pour avoir la liste des commandes.<br/>
	Vous pouvez utiliser le client Jabber de votre choix comme gmail, <a href='http://www.pidgin.im'>pidgin</a> 
	ou <a href='http://www.google.fr/search?q=client+jabber'>autres</a>.<br/>
	Le contact (ou Jabber ID) est: <a href="xmpp:fullmetalgalaxy2@appspot.com">fullmetalgalaxy2@appspot.com</a>
	</p>
	<% } %>

        <script type="text/javascript" language="javascript" src="game/game.nocache.js"></script>

	<%@include file="include/footer.jsp"%>
  </body>
</html>
