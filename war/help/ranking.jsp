<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
<%@include file="/include/meta.jsp"%>
</head>
<body >
<%@include file="/include/header.jsp"%>

<div class="inline-ul" >
<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />
</div>

<h2>Le Classement</h2>
<p>
Cette page explique les méthodes de calcul liées aux classements des joueurs.
Ces calculs pourront évoluer après quelques mois de tests.
</p>

<h3>Sur une partie</h3>
<p>
FMP propose une méthode simple pour désigner le vainqueur d'une partie: chaque 
unité rapporte un point et chaque minerai en rapporte deux. Celui qui a la fin 
de la partie, a le plus de points de victoire remporte.
</p>
<p>
Sur FMG, on retire les 14 points correspondants aux unités fournies en début de partie.
</p>

<h3>Sur le long terme</h3>
<p>
Le niveau d'un joueur (d'où est directement tiré le classement), est la somme des 
points de victoire de chaque partie majorés ou minorés en fonction du niveau des adversaires.
</p>
<pre>
La formule est la suivante :
finalScore = fmgScore *(sum(otherLevel)/(myLevel*otherPlayerCount))^sign(fmgScore) 
</pre>
<p>
En gros, plus nos adversaires sont forts et plus nos points de victoires sont majorés.
</p><p>
En plus, le vainqueur d'une partie gagne un bonus de point correspondant au niveau des autres joueurs.
</p>

<h3>L'érosion</h3>
<p>
Pour permettre aux nouveaux joueurs de se faire une place au panthéon, on réduit le 
niveau des joueurs les plus forts s'ils dépassent les 2000 points.
<br/>
La formule suit une parabole : à 2000pts on perd 150pts par mois alors qu'a 100pts on en perd 0.
</p>

<h3>Les grades</h3>
<p>
Les grades sont les petites icônes ajoutées sous les avatars du forum. On y trouve 
un insigne indiquant sont niveau par rapport au plus fort et un smiley indiquant 
sont comportement moyen des parties précédentes.
</p>
<p>Il y a 10 icônes de niveau calculé en fonction du niveau du mieux classé :<br/>
    <img src="/images/icons/level0.png" alt="0" border="0">
    <img src="/images/icons/level1.png" alt="1" border="0">
    <img src="/images/icons/level2.png" alt="2" border="0">
    <img src="/images/icons/level3.png" alt="3" border="0">
    <img src="/images/icons/level4.png" alt="4" border="0">
    <img src="/images/icons/level5.png" alt="5" border="0">
    <img src="/images/icons/level6.png" alt="6" border="0">
    <img src="/images/icons/level7.png" alt="7" border="0">
    <img src="/images/icons/level8.png" alt="8" border="0">
    <img src="/images/icons/level9.png" alt="9" border="0">
</p><p>    
    Il y a 4 icônes d'attitudes !<br/>
    <img src="/images/icons/aggressive.png" alt="" border="0"> agressif<br/>
    <img src="/images/icons/balanced.png" alt="" border="0"> normal<br/>
    <img src="/images/icons/pacific.png" alt="" border="0"> pacifique<br/>
    <img src="/images/icons/sheep.png" alt="" border="0"> c'est fait prendre son astro 1 fois sur 2 (ou joué aucune
    partie)<br/>
</p><p>
    Enfin FMG peux ajouter des icônes sur la fiabilité du joueur:<br/>
    <img src="/images/icons/ban.png" alt="" border="0"> c'est fait banir 1 fois sur 2<br/>
    [rien] donc joueur normal<br/>
    <img src="/images/icons/vip.png" alt="" border="0"> termine toutes ses parties et icon sur demande<br/>
</p>


<%@include file="/include/footer.jsp"%>
</body>
</HTML>
