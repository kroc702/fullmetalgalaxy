<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<head>
<title>Full Metal Planete/Galaxy Online</title>

<style type="text/css">@import url( /style.css );</style>


</head>
<body>

<div style="display:none">
Full Metal Planete ou Full Metal Galaxy est un jeu web gratuit de strategie et tactique, 
ou wargame online par navigateur.
Il ce joue tour par tour ou en mode asynchrone.
Vous déplacez vos pions sur une carte pavée d'hexagones.
</div>


<%@include file="include/mytopmenu.jsp"%>

<div style="width:999px; margin: 0 auto; text-align: left;">
<div style="position:absolute;">

<div class="bloc" style="width:400px; position:absolute; top:130px; left:220px;">   
    <p>Aux confins de la galaxie, plus aucune loi ne r&eacute;gule les conflits
    entre m&eacute;gacorporations mini&egrave;res. Vous &ecirc;tes un full
    metal pilote vendant vos comp&eacute;tences militaires au plus
    offrant.</p>
    <table><tr><td>
    <a href="/images/screenshots/realmap1.jpg" >
    <img src="/images/screenshots/min/realmap1.jpg" /> 
	</a>
    </td><td>
    <div>
    FMG est un wargame enti&egrave;rement gratuit, jouable dans un navigateur où chaque partie est ind&eacute;pendante.<BR/>
    </div>
    </td></tr></table>
    <br/>
    <center>
    <A HREF="/game.jsp?id=/puzzles/tutorial/model.bin" style="font-size:12pt; line-height:25px;">Tutorial (15 min)</A><br/>
    <br/>
    <A HREF="/account.jsp" style="font-size:12pt; line-height:25px;">Créer un compte</A>
    </center>
</div>


<div class="bloc" style="width:955px; height:30px; position:absolute; left:0px; top:450px; font-size: 8pt;" >   
Full Metal Galaxy est une adaptation jeu web de Full Metal Plan&egrave;te, un jeu
de strat&eacute;gie sur table de G&eacute;rard Mathieu, G&eacute;rard Delfanti et Pascal
Trigaux, &eacute;dit&eacute; par Ludod&eacute;lire entre 1989 et
1996. Full Metal Galaxy est sous ma seule responsabilit&eacute;, les
auteurs n'ont pas pris part &agrave; son d&eacute;veloppement. 
<A HREF="/apropos.jsp">A propos...</A>
</div>

<div id="menu" class="bloc" style="width:180px; height:280px; position:absolute; top:130px; left:0px;" >
    <%@include file="include/menu.html"%>
</div>

</div>
</div>

<%@include file="include/analiticscript.html"%>

</body>
</HTML>