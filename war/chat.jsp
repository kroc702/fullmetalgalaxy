<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!doctype html>
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
	%>
	<meta name='gwt:property' id='fmp_channelToken' content='<%= channelToken %>' />
	<meta name='gwt:property' id='fmp_pageid' content='<%= presence.getPageId() %>' />

    
    <%
    String conversation = Serializer.escape(Serializer.toClient( ChannelManager.getRoom(id) ));
    %>
    <!-- meta name='gwt:property' id='fmp_conversation' content='' /-->
    <script type="text/javascript" language="javascript">
    	var conversation='<%= conversation %>';
	</script>
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="chat/chat.nocache.js"></script>
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

    <h3>Full Metal Chat</h3>

	<div id="wgtmessages" style="height:100%; width:100%;"></div>
	
	<%@include file="include/footer.jsp"%>
  </body>
</html>
