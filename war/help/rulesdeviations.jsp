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

<p>
FMG tente de coller au plus près des règles originales de full metal planete tout en proposant 
des modes de jeu adapté au web. En particulier, il est possible (mais pas obligatoire) de jouer 
en mode parallèle (cf les modes de jeu & variantes) qui élimine la notion de tour de jeu. Ceci 
entraine quelques interprétations des règles :</p>

<h3>Les neutralisations</h3>
<p>Elles sont automatiques et n'empêchent pas de déplacer les neutralisants. En revanche, si 
l'un des neutralisants se déplace, la neutralisation est immédiatement rompue (cf les zones de 
feu). Les différences tactiques par rapport aux règles originales sont minimes.</p>

<h3>Deux chars cote à cote sur une montagne</h3>
<p>Ce n’est pas interdit, mais inutile en défense. Si deux chars, du même propriétaire, sont 
côte à côte sur des montagnes, l'un des deux ne produit pas de zone de feu (il est toujours 
possible de tirer).</p>

<h3>Les marées</h3>
<p>
Il n’est pas possible de prédire la marée à plus de deux tours, même avec 3 pondeuses météo.<br/>
Les marées sont aléatoires et non plus moyenné sur l’ensemble de la partie.
</p>

<h3>Le déploiement</h3>
<p>Le déploiement est possible à 4 cases du centre de l’astronef et non plus 3 à partir des 
podes. (cela ajoute 3 cases)
</p>

<h3>Le décollage</h3>
<p>Le décollage d'un astronef est gratuit (0PA) pour éviter les frustrations inutiles des nouveaux
 joueurs ignorants de cette règle.
</p>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>
