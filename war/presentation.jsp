<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<HTML>
<head>
<title>Présentation de Full Metal Galaxy</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body >
<%@include file="include/header.jsp"%>


<h2>Copies d'écrans</h2>

<a href="/images/screenshots/tutorial1.jpg" >
    <img src="/images/screenshots/min/tutorial1.jpg" /> 
</a>
<a href="/images/screenshots/tutorial2.jpg" >
    <img src="/images/screenshots/min/tutorial2.jpg" />
</a>


<h2>Pour démarrer</h2>
<p>
Le moyen le plus simple pour avoir un bon aperçu et s'initier au jeu est de faire le 
<a href="/game.jsp?id=/puzzles/tutorial/model.bin" >tutorial</a>.
Il ne dure qu'une dizaine de minutes et vous n'avez pas besoin de créer de compte.
</p>
<p>
Par la suite, vous pouvez tenter les quelques problèmes proposés ou vous inscrire &agrave; une
partie regroupant des débutants. (Pour s'inscrire cliquez sur 
<img src="/help/images/Register32.png" alt="Register" />)
</p>



<%@include file="include/footer.jsp"%>
</body>
</HTML>