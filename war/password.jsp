<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<HTML>
<head>
<title>Full Metal Galaxy - Password</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body >

<%@include file="include/header.jsp"%>

    <h2>Mot de passe perdu</h2>

<h1><%= (request.getParameter("msg")==null) ? "" : request.getParameter("msg") %></h1>

<form name="myform" action="/AccountServlet" method="post" enctype="multipart/form-data">

email :
<input type="text" name="email" value=""/><br/>
<input type="hidden" name="password" value="1"/>

<input type="submit" name="Submit" value="Password"/>
</form>

<%@include file="include/footer.jsp"%>
</body>
</HTML>