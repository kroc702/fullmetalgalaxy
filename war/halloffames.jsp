<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.*,com.fullmetalgalaxy.model.constant.*,com.googlecode.objectify.Query,com.fullmetalgalaxy.model.ressources.SharedI18n" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Classement des joueurs</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
final int COUNT_PER_PAGE = 20;
int offset = 0;
try
{
  offset = Integer.parseInt( request.getParameter( "offset" ) );
} catch( NumberFormatException e )
{
}

String title = "Classement des joueurs";
String orderby = request.getParameter( "orderby" );
if( orderby == null )
{
  orderby = "-m_currentLevel";
}
if( orderby.equals("-m_lastConnexion") )
{
  title = "Les derniers joueurs connectés";
}

out.println( "<h2>"+title+"</h2>" );

Query<EbAccount> accountQuery = FmgDataStore.dao().query(EbAccount.class);
accountQuery.order(orderby);
DateFormat dateFormat = new SimpleDateFormat( SharedI18n.getMisc( Auth.getUserId(request,response) ).dateFormat() );
DecimalFormat df = new DecimalFormat("#.#");

out.println("<table width='100%'>");
out.println("<tr><td>Avatar</td>" );
out.println("<td><a href='"+ request.getRequestURL() +"?orderby=m_pseudo'>Pseudo</a></td>");
out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_currentLevel'>Niveau</a></td>");
out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_totalScoreSum'>Gain total</a></td>");
out.println("<td>Nb <a href='"+ request.getRequestURL() +"?orderby=-m_victoryCount'>victoire</a>/<a href='"+ request.getRequestURL() +"?orderby=-m_finshedGameCount'>partie</a></td>");
out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_styleRatio'>Agressivité</a></td>");
out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_lastConnexion'>Dernière connexion</a></td><td></td></tr>");

for( EbAccount account : accountQuery.offset(offset).limit(COUNT_PER_PAGE) )
{
  out.println("<tr>");
  // avatar
  out.println("<td><a href='"+ account.getProfileUrl() + "'><img src='" + account.getAvatarUrl() + "' height='40px' /></a></td>");
  // pseudo
  out.println("<td><a href='"+ account.getProfileUrl() + "'>" + account.getPseudo() + "</a></td>");
  // level
  out.println("<td>"+df.format(account.getCurrentLevel())+" <img src='"+ account.getLevelUrl() +"'/></td>");
  // total score sum
  out.println("<td>"+account.getTotalScoreSum()+"</td>");
  // game victory / played count
  out.println("<td>"+account.getVictoryCount()+"/"+account.getFinshedGameCount()+"<a href='/profile.jsp?id="+account.getId()+"'> <img src='/images/icon_new_window.gif' border=0/></a></td>");
  // agressivity
  out.println("<td>"+df.format(account.getStyleRatio())+" <img src='"+ account.getPlayerStyle().getIconUrl() +"'/>");
  // fairplay
  if( account.getFairplay() > 0 )
  {
    out.println(" <img src='/images/icons/thumbup.gif' title='"+account.getFairplay()+"'/>");
  }
  else if( account.getFairplay() < 0 )
  {
    out.println(" <img src='/images/icons/thumbdown.gif' title='"+account.getFairplay()+"'/>");
  }
  out.println("</td>");
  
  // last connexion
  out.println("<td><span class='date'>"+ dateFormat.format( account.getLastConnexion() ) +"</span></td>");
  // private message
  if( (account.allowMsgFromPlayer() && account.haveEmail()) )
  {
    out.println("<td><a href='"+ account.getEMailUrl() + "'><img src='/images/css/icon_pm.gif' border=0 alt='PM'></a></td>" );
  } else {
    out.println("<td></td>" );
  }

  out.println("</tr>");
}
out.println("</table>");
%>
	
	<p>Pages :
	<%
		int p = 0;
		int accountListCount = GlobalVars.getAccountCount();
		while(accountListCount > 0)
		{
		  out.println( "<a href='"+ request.getRequestURL() +"?orderby="+orderby+"&offset="+(p*COUNT_PER_PAGE)+"'>"+(p+1)+"</a> " );
		  accountListCount -= COUNT_PER_PAGE;
		  p++;
		}
	%>
	</p>
	
<%@include file="include/footer.jsp"%>
</body>
</html>
