<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>

	<div id="motifHeader">
	<div id="bordure">
	<div id="corps">
	<div id="content">
		<div id="header"><header>
			<div id="banniere">
				<div id="logo"></div>
			</div>
		<jsp:include page="/include/mytopmenu.jsp" />
  				<div id="chart"></div>
		<div id="sousMarin"></div>
		</header></div>
		<div id="nav"><nav>
			<ul>
				<li><a href="/" >Accueil</a></li>
				<li><a href="/gamelist.jsp" >Partie en cours</a>
					<ul>
						<li><a href="/gamelist.jsp?tab=0" >Nouvelles Parties</a></li>
						<li><a href="/gamelist.jsp?tab=1" >Mes Parties</a></li>
						<li><a href="/gamelist.jsp?tab=2" >Jeux Solo</a></li>
						<li><a href="/gamelist.jsp?tab=3" >Autres Parties</a></li>
					</ul>
				</li>
				<li><a href="http://fullmetalplanete.forum2jeux.com/f33-full-metal-galaxy" >Forum</a></li>
				<li><a href="/chat.jsp" >Chat</a></li>
				<li><a href="/help/" >Aides de jeu</a>
					<jsp:include page="/i18n/fr/help/menu.html" />
				</li>																
				<li><a href="/halloffames.jsp" >Joueurs</a></li>																
				<li><a href="/liens.jsp" >Liens</a></li>																
				<li><a href="/apropos.jsp" >Développement</a></li>			
				
				<%--
				<li><a href="" ><img src="/i18n/fr/images/icon_locale.png" /></a></li>
				 --%>																
			</ul>
		</nav></div>
