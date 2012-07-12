<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.server.forum.*,com.fullmetalgalaxy.model.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<%@taglib prefix="fmg" uri="/WEB-INF/classes/fmg.tld"%>

<!DOCTYPE html>
<html lang="<%= I18n.getLocale(request,response) %>">
<head>
<title>Full Metal Planete/Galaxy Online</title>

<jsp:include page="include/meta.jsp" />

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
		<a href="img01_url" target="_blank" style="display:none;">
	        <img src='/images/slide/screen1.jpg' >
	    </a>
		<a href="img01_url" target="_blank" style="display:none;">
	        <img src='/images/slide/tank.jpg' >
	    </a>
		<a href="img01_url" target="_blank" style="display:none;">
	        <img src='/images/slide/pince.jpg' >
	    </a>
		<a href="img01_url" target="_blank" style="display:none;">
	        <img src='/images/slide/crabe.jpg' >
	    </a>
		</div>
		</div>
		<script type="text/javascript">
	    $(document).ready(function() {
	        $('#coin-slider').coinslider({width:375, height:245, effect:'rain', links:false, delay: 7000});
	    });
		</script>
		
			<h1><fmg:resource key="index_title"/></h1>
			<p><fmg:resource key="index_catchphrase"/></p>

<% if(Auth.isUserLogged( request, response )) { %>
	<table><tr>
		<td style="width:40px"></td>		
		<td><img src='/images/lost.png'/></td>		
		<td style="width:30px"></td>		
		<td style=""><fmg:resource key="index_lost"/></td>
	</tr></table>
<% } else { %>
	<p><fmg:resource key="index_fmgisfree"/></p>
	<table><tr>
		<td><a href="/game.jsp?id=/puzzles/tutorial/model.bin" class="bouton"><fmg:resource key="index_demo"/></a></td>		
		<td><a href="/account.jsp" class="bouton"><fmg:resource key="index_suscribe"/></a></td>
		<td style="width : 100%;"><div id="draw2"></div></td>
	</tr></table>
<% } %>
		</div>
		
		<div id="rssCollumn" class="collumn" >
			<h2><a href="http://fullmetalplanete.forum2jeux.com/f40-news"><fmg:resource key="index_lastnew"/></a>
				<a class="iconrss" href="http://fullmetalplanete.forum2jeux.com/feed?f=40" target="_blank"></a>
			</h2>
				<div id="newsrss">
					<%= News.getHtml() %>
				</div>

			<h2><a href="/gamelist.jsp"><fmg:resource key="index_lastgames"/></a>
			</h2>
				<div id="gamesrss">
					<%= Games.getHtml() %>
				</div>
		</div>


		<div id="keyPointsCollumn" class="collumn">
<% if(Auth.isUserLogged( request, response )) { 
  String myPseudo = Auth.getUserPseudo( request, response );
  com.googlecode.objectify.Query<EbGamePreview> gameList = FmgDataStore.dao().query(EbGamePreview.class)
	    						.filter( "m_status in", new GameStatus[] {GameStatus.Running, GameStatus.Pause, GameStatus.Open} )
	    						.filter( "m_setRegistration.m_account.m_pseudo", myPseudo )
	    						.filter( "m_configGameTime in", ConfigGameTime.values()  )
	    						.order("-m_creationDate");
  gameList.limit( 5 );
	%>
	<fmg:resource key="index_mygames"/>
	<%
	int gameCount = 0;
	for( EbGamePreview game : gameList )
	{
	  gameCount++;
	  out.println( "<a href='" + FmpConstant.getBaseUrl() + "/game.jsp?id=" + game.getId()
	      + "'><div class='article'><article>"
	      // <h4> tag cause graphic glich on IE7
	      + "<div class='h4'>" + game.getIconsAsHtml() + game.getName()
	      + "</div></article></div></a>" );
	}
	if( gameCount > 0 ) { %>
	<fmg:resource key="index_allmygames"/>
	<% } else { %>  
	<fmg:resource key="index_nogamesuscribe"/>
	<% } %>

<% } else { %>
	<fmg:resource key="index_keypoints"/>
<% } %>
		</div>
		
		<div id="statCollumn" class="collumn">
		<h2><a href="/stats.jsp"><fmg:resource key="index_statistiques"/></a></h2>
		<%= GlobalVars.getStatsHtml() %>
		</div>		
		

	<p id="nb"><fmg:resource key="index_disclaimer"/></p>
	

<jsp:include page="include/footer.jsp" />
</body>
</html>
