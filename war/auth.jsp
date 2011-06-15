<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Connexion</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<center>
<h2>Full Metal Connexion</h2>


<h1><%= (request.getParameter("msg")==null) ? "" : request.getParameter("msg") %></h1>

<form name="myform" action="/AccountServlet" method="post" enctype="multipart/form-data">

login :
<input type="text" name="login" value=""/><br/>
mot de passe :
<input type="password" name="password" value=""/><br/>

<input type="hidden" name="connexion" value="1"/>
<input type="hidden" name="continue" value="<%= (request.getParameter("continue")==null) ? "/" : request.getParameter("continue") %>"/>

<input type="submit" name="Submit" value="Connexion"/>
</form>
</center>


<p>
Si vous n'avez pas de compte, vous pouvez 
<a href="<%= Auth.getGoogleLoginURL(request,response) %>" >utiliser votre compte google</a>
 ou bien 
<A HREF="/account.jsp">cr&eacute;er un nouveau compte</A>. 
</p>

<p>
Si vous avez oublié votre mot de passe, <a href="/password.jsp">vous pouvez en demandez un autre</a> ou <a href="mailto:admin@fullmetalgalaxy.com">me contacter par mail</a>.
</p>


<%@include file="include/footer.jsp"%>
</body>
</html>
