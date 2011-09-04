<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.server.forum.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>

<!DOCTYPE html>
<html lang="<%= I18n.getLocale(request,response) %>">
<head>
<title>Full Metal Planete/Galaxy Online</title>

<jsp:include page="include/meta.jsp" />

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="jquery.js"></script>
<script type="text/javascript" src="slider.js"></script>	

</head>
<body>
<jsp:include page="include/header.jsp" />

		<div id="description">
		
		<div id="slideshow" >
		<div id="coin-slider" >
		<a href="img01_url" target="_blank">
	        <img src='/images/slide/pondeuse.jpg' >
	    </a>
		<a href="img01_url" target="_blank">
	        <img src='/images/slide/screen1.jpg' >
	    </a>
		<a href="img01_url" target="_blank">
	        <img src='/images/slide/tank.jpg' >
	    </a>
		<a href="img01_url" target="_blank">
	        <img src='/images/slide/pince.jpg' >
	    </a>
		<a href="img01_url" target="_blank">
	        <img src='/images/slide/crabe.jpg' >
	    </a>
		</div>
		</div>
		<script type="text/javascript">
	    $(document).ready(function() {
	        $('#coin-slider').coinslider({width:375, height:245, effect:'rain', links:false, delay: 7000});
	    });
		</script>
		
			<h1>Full Metal Galaxy, wargame en ligne</h1>
			<p>
				Avez-vous l'étoffe d'un full metal pilote ?<br/>
Une grande compagnie minière vous a recruté à prix d'or. Votre mission: poser votre astronef sur Full Metal Planète, ramasser un maximum de minerai, désintégrer ou capturer le coûteux matériel des compagnies adverses, et si possible vous emparer de leurs propres navettes, aux soutes pleines à craquer.
</p>
<p>
FMG est un wargame entièrement gratuit, jouable dans un navigateur où chaque partie est indépendante.
</p>
		
		<table><tr>
			<td><a href="/game.jsp?id=/puzzles/tutorial/model.bin" class="bouton">Démo</a></td>		
			<td><a href="/account.jsp" class="bouton">Inscrivez-vous!</a></td>
			<td style="width : 100%;"><div id="draw2"></div></td>
		</tr></table>
		</div>
		
		<div id="rssCollumn" class="collumn" >
			<h2><a href="http://fullmetalplanete.forum2jeux.com/f40-news">Les dernières nouvelles</a>
				<a id="iconrss" href="http://fullmetalplanete.forum2jeux.com/feed?f=40" target="_blank">
			        <img src='/images/icons/rss.jpg'/>
		    	</a>
			</h2>
				<div id="newsrss">
					<%= News.getHtml() %>
				</div>
		</div>


		<div id="keyPointsCollumn" class="collumn">
			<h2><a href="/help">Les points clefs</a></h2>		
<img style="float : right;" src="images/keyPoints.jpg" />
<ul>
<li>La carte interactive est l'unique interface</li>
<li>Aucun hasard, seul votre tactique fera la différence</li>
<li>Un mécanisme qui a fait ses preuves</li>
<li>Entre 2 et 8 joueurs par partie</li>
<li>Le terrain change en fonction des marées</li>
<li>Partie lente sur un mois ou en temps réel (1h30)</li>
<li>Mode tour par tour ou parallèle pour ne jamais être bloqué</li>
</ul>
<a href="/help" style="float: right;">En Savoir plus</a>
		</div>
		
		<div id="statCollumn" class="collumn">
		<h2>Stats en test:</h2>
		<%= GlobalVars.getStatsHtml() %>
		</div>		
		
	<div id="draw1"></div>	

	<p id="nb">
Full Metal Galaxy est une adaptation jeu web de Full Metal Planète, un jeu de stratégie sur table de Gérard Mathieu, Gérard Delfanti et Pascal Trigaux, édité par Ludodélire entre 1989 et 1996. Full Metal Galaxy est sous ma seule responsabilité, les auteurs n'ont pas pris part à son développement.
	<p>



<jsp:include page="include/footer.jsp" />
</body>
</html>
