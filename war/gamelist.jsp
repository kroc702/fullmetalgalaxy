<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.server.datastore.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Parties en cours</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

	<table class="fmp-array" style="width:100%;">
	<%  List<PersistGame> gameList = FmgDataStore.getPersistGameList();
	    for( PersistGame game : gameList )
	    {
	      out.println("<tr>" );
	      // minimap
	      out.println("<td style=\"width:100px;\"><a href=\"/game.jsp?id="+game.getId()+"\"><img src=\"/ImageServlet?minimap="+game.getId()+"\" height=\"50\"></a></td>" );
	      // game name
	      out.println("<td><a href=\"/game.jsp?id="+game.getId()+"\"><h2>"+ game.getName() + "</h2></a></td>" );
	      // player nb
	      out.println("<td>"+game.getCurrentNumberOfRegiteredPlayer()+"/"+ game.getMaxNumberOfPlayer() + "</td>" );
	      
	      // admin option
	      if(Auth.isUserAdmin(request, response))
	      {
	      	out.println("<td><a href=\"/admin/Servlet?deletegame="+game.getId()+"\">effacer</a></td>" );
	      }
	      out.println("</tr>" );
	    }
	%>
	</table>
	<% if(Auth.isUserLogged(request, response)) { %>
		<a href="editgame.jsp">Cr&eacute;er une nouvelle partie</a>
	<% } %>
<%@include file="include/footer.jsp"%>
</body>
</html>
