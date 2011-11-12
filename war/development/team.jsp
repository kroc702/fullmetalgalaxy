<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<HTML>
<head>
<title>Full Metal Galaxy - A propos</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body>
<%@include file="/include/header.jsp"%>

<div class="inline-ul" >
<jsp:include page="<%= I18n.localize(request,response,\"/development/menu.html\") %>" />
</div>

<p>
Pour l'instant, le terme équipe est sans doute galvaudé, car hormis le développeur principal,
il y a eu bien peut de contribution...<br/>
Néanmoins, FMG est open source et a déjà reçu quelques contributions exterieur. Le projet a aussi
l'objectif de trouver des contributeurs réguliers.
</p>

<p>
<h3>L'auteur :</h3>
Bonjour,<br/>
Je m'appelle Vincent, j'ai 31 ans, j'habite à Brest et je suis développeur dans la journée.<br/> 
FMG a été initié en 2008 pour tester une techno récente: GWT. Depuis 2010, je l'ai fait 
migrer vers les infrastructures google et je tente de faire partager ce projet avec le plus grand nombre.<br/>
Mon pseudo ? Kroc, en référence à "Kroc le bô".
</p>

<h3>Remerciements :</h3>
<p>
<ul>
<li>A ma copine chérie qui en a entendu parler quelques heures...</li>
<li>A Quentin Castel pour m'avoir proposé un design plus moderne du site.</li>
<li>A Eric Alber pour m'avoir fourni les modèles blender des pions.</li>
<li>A Brice Vandemoortele pour m'avoir fourni les superbes illustrations de pondeuse météo.</li>
<li>A Jean-Marc Leroy, l'auteur de full metal program, pour m'avoir fourni son algorithme 
de g&eacute;n&eacute;ration de carte.</li>
<li>Aux premiers testeurs, particulièrement Quentin, Manu, Sergio et Bekymy.</li>
<li>A tous ceux qui m'ont encouragés de près ou de loin.</li>
</ul>
</P>
            
           
           

<%@include file="/include/footer.jsp"%>
</body>
</HTML>