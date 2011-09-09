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

<h2>les prévisions des marées</h2>
<p>
Les marées modifient complètement le visage d’une planète, il est donc important de savoir en tenir compte.
Pour connaitre la prochaine marée, il faut avoir au début du tour (de l’incrément de temps en mode parallèle) 
une pondeuse météo en état de fonctionnement sur la carte.<br/>
Avec deux pondeuses ou plus, il est possible de prévoir les deux prochaines marées.
</p>

<h2>le décollage au tour 21 ou 25</h2>
<p>Une partie se joue en 25 tours, mais il est possible de décoller au tour 21. C’est souvent 
préférable lorsqu’un voisin belliqueux souhaite s’emparer de votre astronef.
</p>

<h2>Deux chars sur une montagne.</h2>
<p>Les chars sur les montagnes ont une portée de 3 cases. Pour éviter de construire des positions
 imprenables, si deux chars, du même propriétaire, sont côte à côte sur des montagnes, l'un des 
 deux ne produit pas de zone de feu (il est toujours possible de tirer).
</p>

<h2>les pontons en chaine</h2>
<p>Les pontons doivent toujours être reliés à la terre ferme ou un autre ponton. Si suite à un 
changement de marée ou un tir, des pontons ce retrouvent isolés au milieu de la mer, ils sont 
détruits et leurs cargaisons avec...
</p>

<h2>La réserve de PA</h2>
<p>Chaque tour, les joueurs gagnent 15 PA plus 5 par astronef supplémentaire.<br/>
Le nombre maximum de PA qu’un joueur peut accumuler est de 25 plus 5 par astronef supplémentaire.<br/>

En tour par tour, on ne peut économiser qu’un nombre de PA multiple de 5.
</p>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>
