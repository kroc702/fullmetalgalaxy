<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<HTML>
<head>
<title>Full Metal Galaxy - A propos</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body >

<%@include file="include/header.jsp"%>

            <center>
	        <p>Pour me contacter : <a href="mailto:admin@fullmetalgalaxy.com">admin@fullmetalgalaxy.com</a></p>
	        
            <p><small>
            Full Metal Galaxy est une adaptation pour le web de Full Metal Planète.
            Il s'agit d'un jeu de stratégie sur table de Gérard Mathieu, Gérard Delfanti et Pascal Trigaux, 
            édité par Ludodélire entre 1989 et 1996. 
            Full Metal Galaxy est sous ma seule responsabilité, les auteurs n'ont pas pris 
            part &agrave; son développement. 
            </small></p>            

            <h3>Version actuellement en production :</h3>
            <P> <%@include file="include/version.html"%> <br/>
            commit 
            <a href="http://code.google.com/p/fullmetalgalaxy/source/detail?r=<%@include file="include/commitid.html"%>">
            <%@include file="include/commitid.html"%></a>
            </P>
            
			<p>
			Logiciel libre publié sous 
			<a href="http://www.gnu.org/licenses/agpl.html"><img border="none" src="/images/agplv3-88x31.cache.png" alt="AGPL v3"/></a> 
			<a href="http://www.rodage.org/gpl-3.0.fr.html">(fr)</a>.<br/>
			Hébergé par  <img src="http://code.google.com/appengine/images/appengine-silver-120x30.gif" alt="Google App Engine" /><br/>
			Source disponible <a href="http://code.google.com/p/fullmetalgalaxy/">ici</a>.<br/>
			<small>Copyright 2010 Vincent Legendre</small>
			</p>
            
            
            <%-- p>
            <a href="/conditions.jsp">Conditions d'utilisations</a>
            </p> --%>
            
            <h3>Remerciements :</h3>
            <p>
            - A ma copine chérie qui en a entendu parler quelques heures...<br/>
            - A Eric Alber pour m'avoir fourni les modèles blender des pions<br/>
            - A Brice Vandemoortele pour m'avoir fourni la superbe illustration de pondeuse météo<br/>
            - A Jean-Marc Leroy, l'auteur de full metal program, pour m'avoir fourni son algorithme 
            de g&eacute;n&eacute;ration
            de carte ainsi que les images des pions pour mes premières versions.</br>
            - A isi.nc qui a hébergé gratuitement la version alpha en Nouvelle-Calédonie.<br/>
            - A l'équipe des Alpha testeurs (et particulièrement &agrave; Quentin, Manu et Sergio) qui 
            m'ont remonté la première série de bugs bloquants.<br/>
            - A tous ceux qui m'ont encouragés de près ou de loin.<br/>
            </P>
            
            </center>

<%@include file="include/footer.jsp"%>
</body>
</HTML>