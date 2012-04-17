<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<%
String title = request.getParameter("title");
if( title == null ) title = "";
String text = request.getParameter("text");
if( text == null ) text = "";
%>

<title>Full Metal Galaxy - <%= title %></title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<center>

<h1><%= title %></h1>


</center>

<p><%=text%></p>

<%@include file="include/footer.jsp"%>
</body>
</html>
