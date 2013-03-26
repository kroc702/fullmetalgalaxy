<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>

<div id="nav"><nav>
	<ul>
		<li><a href="/" >Home</a></li>
		<li><a href="/gamelist.jsp" >Games</a>
			<ul>
				<li><a href="/gamelist.jsp?tab=0" >New Games</a></li>
				<li><a href="/gamelist.jsp?tab=1" >My Games</a></li>
				<li><a href="/gamelist.jsp?tab=2" >Puzzle Games</a></li>
				<li><a href="/gamelist.jsp?tab=3" >Other Games</a></li>
			</ul>
		</li>
		<li><a href="http://guide.fullmetalgalaxy.com/instructors?language=en" >Players</a>
			<ul>
				<li><a href="http://guide.fullmetalgalaxy.com/instructors?language=en" >Instructors</a></li>
				<li><a href="/halloffames.jsp" >Ranking</a></li>
				<li><a href="/halloffames.jsp?orderby=-m_lastConnexion" >Directory</a></li>
				<li><a href="/stats.jsp" >Global stats</a></li>
			</ul>
		</li>																
		<li><a href="http://fullmetalplanete.forum2jeux.com/f33-full-metal-galaxy" >Community</a>
			<ul>
				<li><a href="http://fullmetalplanete.forum2jeux.com/f33-full-metal-galaxy" >FMG Forum</a></li>
				<li><a href="http://fullmetalplanete.forum2jeux.com/" >General Forum</a></li>
				<li><a href="/chat.jsp" >Chat</a></li>
			</ul>
		</li>
      <li><a href="http://guide.fullmetalgalaxy.com/?language=en" >Guide</a>
			<ul>
				                    <li><a href="http://guide.fullmetalgalaxy.com/guide/highlights?language=en">Highlights</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/create-game?language=en">Create game</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/rules-deviations?language=en">Rules deviations</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/game-modes?language=en">Game modes</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/manual?language=en">Manual</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/ranking?language=en">Ranking</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/fire-covers?language=en">Fire covers</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/advices?language=en">Advices</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/faq?language=en">FAQ</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/original-rules?language=en">Original rules</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/guide/standard-units?language=en">Standard units</a></li>
				                 	<li><a href="http://guide.fullmetalgalaxy.com/guide/additional-units?language=en">Extra units</a></li>
                			</ul>
		</li>																
        <li><a href="http://guide.fullmetalgalaxy.com/resources/external-links?language=en" >Resources</a>
			<ul>
				                                    <li><a href="http://guide.fullmetalgalaxy.com/resources/external-links?language=en">External links</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/resources/banners?language=en">Banners</a></li>
                			</ul>
		</li>
        <li><a href="http://guide.fullmetalgalaxy.com/development/contribute?language=en" >Development</a>
			<ul>
				                                    <li><a href="http://guide.fullmetalgalaxy.com/development/contribute?language=en">Contribute</a></li>
                                    <li><a href="http://guide.fullmetalgalaxy.com/development/team?language=en">Team</a></li>
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
		<li><a href="" ><img src="/images/icons/flag16_en.png" /></a>
			<ul>
				<li><a href="?locale=fr" ><img src="/images/icons/flag16_fr.png" /> Français</a></li>
				<li><a href="?locale=en" ><img src="/images/icons/flag16_en.png" /> English</a></li>
			</ul>
		</li>
																		
	</ul>
</nav></div>
