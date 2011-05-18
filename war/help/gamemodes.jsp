<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body >

<%@include file="/include/header.jsp"%>

<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />

<h2>Mode "tour par tour" & "parallèle"</h2>
<p>Full Metal Planete, le jeu de plateau qui inspire FMG, se joue chacun à tour de rôle. 
Afin de profiter de certaine spécificité des jeux par navigateur, FMG propose deux modes :</p>
<ul>
<li><img src="/images/css/icon_tbt.gif" /> le mode tour par tour où chaque joueur joue à tour de rôle.<br/>
Ce mode est le plus fidèle au jeu de plateau.
Les parties peuvent être bloquées par un joueur ne jouant pas.
La durée d’une partie dépend du nombre de joueurs.
</li>
<li><img src="/images/css/icon_parallele.gif" /> le mode parallèle où tous les joueurs jouent en même temps.<br/>
 Les PA s’incrémentent au rythme de 3 par 5h50 et durent un mois.
Les joueurs ne bloquent pas la partie.
La durée des parties ne dépend pas du nombre de joueurs.
Les autres joueurs peuvent réagir pendant une attaque, augmentant ainsi la dose de stress...
</li>
</ul>

<h2>Les vitesses de jeu.</h2>
<p>FMG propose des parties lentes (compter un mois) et des parties rapides (compter deux heures).</p>
<ul>
<li><img src='/images/css/icon_slow.cache.png'/> partie lente : pas de limite de temps en tour par tour ou un mois en parallèle.</li>
<li><img src='/images/css/icon_fast.cache.png'/> partie rapide : 3min maxi par tour et par joueur ou 1h30 en parallèle.</li>
</ul>
<h2>Variantes</h2>
Pas de variante pour l’instant mais certaines sont prévu.<br/>
cf <a href="http://fullmetalplanete.forum2jeux.com/t284-variantes">http://fullmetalplanete.forum2jeux.com/t284-variantes</a>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>
