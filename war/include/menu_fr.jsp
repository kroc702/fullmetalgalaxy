<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>

<div id="nav"><nav>
	<ul>
		<li><a href="/" >Accueil</a></li>
		<li><a href="/gamelist.jsp" >Parties</a>
			<ul>
				<li><a href="/gamelist.jsp?tab=0" >Nouvelles Parties</a></li>
				<li><a href="/gamelist.jsp?tab=1" >Mes Parties</a></li>
				<li><a href="/gamelist.jsp?tab=2" >Jeux Solo</a></li>
				<li><a href="/gamelist.jsp?tab=3" >Autres Parties</a></li>
			</ul>
		</li>
		<li><a href="http://guide.fullmetalgalaxy.com/instructors?language=fr" >Joueurs</a>
			<ul>
				<li><a href="http://guide.fullmetalgalaxy.com/instructors?language=fr" >Instructeurs</a></li>
				<li><a href="/halloffames.jsp" >Classement</a></li>
				<li><a href="/halloffames.jsp?orderby=-m_lastConnexion" >Annuaire</a></li>
				<li><a href="/stats.jsp" >Stats global</a></li>
			</ul>
		</li>																
		<li><a href="http://fullmetalplanete.forum2jeux.com/f33-full-metal-galaxy" >Communauté</a>
			<ul>
				<li><a href="http://fullmetalplanete.forum2jeux.com/f33-full-metal-galaxy" >Forum FMG</a></li>
				<li><a href="http://fullmetalplanete.forum2jeux.com/" >Forum Général</a></li>
				<li><a href="/chat.jsp" >Chat</a></li>
			</ul>
		</li>
      <li><a href="http://guide.fullmetalgalaxy.com/?language=fr" >Guide</a>
			<ul>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/highlights?language=fr">Bon à savoir</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/create-game?language=fr">Créer une partie</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/rules-deviations?language=fr">Ecarts de règle</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/game-modes?language=fr">Modes de jeu &amp; variantes</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/manual?language=fr">Manuel d utilisation</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/ranking?language=fr">Classement</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/fire-covers?language=fr">Les zones de feu</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/advices?language=fr">Conseils</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/faq?language=fr">FAQ</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/original-rules?language=fr">Règles original</a></li>
                 <li><a href="http://guide.fullmetalgalaxy.com/guide/standard-units?language=fr">Unités standards</a></li>
   			</ul>
		</li>																
        <li><a href="http://guide.fullmetalgalaxy.com/resources/external-links?language=fr" >Ressources</a>
			<ul>
				                                    <li><a href="http://guide.fullmetalgalaxy.com/resources/external-links?language=fr">Liens externes</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/resources/banners?language=fr">Bannières</a></li>
                			</ul>
		</li>
        <li><a href="http://guide.fullmetalgalaxy.com/development/contribute?language=fr" >Développement</a>
			<ul>
                    <li><a href="http://guide.fullmetalgalaxy.com/development/contribute?language=fr">Contribuer</a></li>
                    <li><a href="http://guide.fullmetalgalaxy.com/development/team?language=fr">L&rsquo;équipe</a></li>
            		<li><a href="/about.jsp">About</a></li>
			</ul>
		</li>			
        <li>
        <%-- addthis button was causing issue with ClientUtils.jsNewCssRules method ! --%>
        <span class='st_sharethis' displayText=''></span>
        <script type="text/javascript">var switchTo5x=false;</script>
		<script type="text/javascript" src="http://w.sharethis.com/button/buttons.js"></script>
		<script type="text/javascript">stLight.options({publisher: "ur-eb8265ad-beed-67c5-b458-486fe5f7d419"}); </script>
        </li>		
		<li><a href="" ><img src="/images/icons/flag16_fr.png" /></a>
			<ul>
				<li><a href="?locale=fr" ><img src="/images/icons/flag16_fr.png" /> Français</a></li>
				<li><a href="?locale=en" ><img src="/images/icons/flag16_en.png" /> English</a></li>
			</ul>
		</li>
																		
	</ul>
</nav></div>
