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
if( orderby == null || !orderby.equals("-m_lastConnexion"))
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

out.println("<table width='100%'>");
for( EbAccount account : accountQuery.offset(offset).limit(COUNT_PER_PAGE) )
{
  out.println("<tr>");
  // avatar
  out.println("<td><a href='"+ account.getProfileUrl() + "'><img src='" + account.getAvatarUrl() + "' height='40px' /></a></td>");
  // pseudo and grad
  out.println("<td><a href='"+ account.getProfileUrl() + "'>" + account.getPseudo() + "<br/><img src='" + account.getGradUrl() + "' /></a></td>");
  if( orderby.equals("-m_lastConnexion") )
  {
    // last connexion
    out.println("<td><span class='date'>"+ dateFormat.format( account.getLastConnexion() ) +"</span></td>");
  } else {
    // level
  	out.println("<td>"+account.getCurrentLevel()+" Pts</td>");
  }
  // private message
  if( account.isAllowPrivateMsg() )
  {
    out.println("<td><a href='"+ account.getPMUrl() + "'><img src='/images/css/icon_pm.gif' border=0 alt='PM'></a></td>" );
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
