<%@page import="com.fullmetalgalaxy.model.ressources.SharedI18n"%>
<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Profil du joueur</title>
        
</head>
<body>

<%
EbAccount account = ServerUtil.findRequestedAccount(request);
if( account == null ) 
{ 
	out.println("<h1>Ce profil n'a pas été trouvé sur <a href='http://www.fullmetalgalaxy.com'>www.fullmetalgalaxy.com</a></h1>" );
	return;
}
%>

<h1>Partie</h1>
<table class="fmp-array" style="width:100%;">
<!--tr><td>Date</td><td>score</td><td>score</td><td>Locale</td><td>Mail</td><td>Pseudo</td></tr-->
<%
Iterable<EbGamePreview> gameList = new ArrayList<EbGamePreview>();
String pseudo = account.getPseudo();
gameList = FmgDataStore.dao().query(EbGamePreview.class).filter( "m_setRegistration.m_account.m_pseudo", pseudo ).order("-m_creationDate");
for( EbGamePreview game : gameList )
{
  out.println( "<tr>" );
  out.println( game.getDescriptionAsHtml() );
  out.println("</tr>" );
}
%>
</table>


</body>
</html>
