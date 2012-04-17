<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<HTML>
<head>
<title>Full Metal Galaxy - Liens</title>

<%@include file="include/meta.jsp"%>
</head>
<body >
<%@include file="include/header.jsp"%>
<!-- img src='/images/robot_instructor.jpg' style='float:right'/-->
<div style="width: 100%;min-height: 600px;background-image: url('/images/robot_instructor.jpg');background-repeat:no-repeat;background-position: 100% 100%;">	

<p>
Une petite explication est souvent préférable a un long manuel utilisateur...<br/>
Voici donc quelques personnes à contacter.
</p>

<a href='/halloffames.jsp?orderby=-m_lastConnexion'>Voir l'annuaires des joueurs</a>

<h2>Les instructeurs</h2>
<p>Une petite question ou une partie d'initiation, ils sont volontaires pour vous initier à Full Metal Galaxy !</p>

<h3>grzon</h3>
<p style='width:60%'>
<img src='/ImageServlet?avatar=grzon' style="height:64px;margin: 10px;float:left"/>
Grand fan et promoteur de FMG grzon jouaient déjà à fmp au berceau, c'est dire s'il en connaît un rayon !<br/>
<a href='/email.jsp?pseudo=grzon&subject=je suis nouveau&msg=Connaissez-vous le jeu de plateau "full metal planete" ? Y avez vous déjà joué ? Une présentation serait sympas...'>
Contacter</a>
</p>
<br/>
<h3>ludomaniak</h3>
<p style='width:60%'>
<img src='/ImageServlet?avatar=ludomaniak' style="height:64px;margin: 10px;float:left"/>
Que ce soit en tant qu'instructeur ou grand admin du forum, il donne son temps à la communauté.<br/>
<a href='/email.jsp?pseudo=ludomaniak&subject=je suis nouveau&msg=Connaissez-vous le jeu de plateau "full metal planete" ? Y avez vous déjà joué ? Une présentation serait sympas...'>
Contacter</a>
</p>
<br/>
<h2>Les admins</h2>
<p>Ils ont les pouvoirs d'admin et corrigent les bugs plus souvent qu'ils ne jouent...<br/>
Contactez les instructeurs en priorité.</p>

<h3>Kroc</h3>
<p style='width:60%'>
<img src='/ImageServlet?avatar=Kroc' style="height:64px;margin: 10px;float:left"/>
Je suis le développeur principal du projet.
Mon pseudo viens de Kroc le bô, le gobelin allumeur de gros bill qui s'en prend plein la tête ! Vous voulez suivre mes traces ?<br/>
<a href='/email.jsp?pseudo=Kroc&subject=je suis nouveau&msg=Connaissez-vous le jeu de plateau "full metal planete" ? Y avez vous déjà joué ? Une présentation serait sympas...'>
Contacter</a>
</p>





</div>
<%@include file="include/footer.jsp"%>
</body>
</HTML>