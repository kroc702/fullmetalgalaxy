<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.*,com.fullmetalgalaxy.model.constant.*,com.googlecode.objectify.Query" %>
<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Liste des joueurs</title>
        

</head>
<body>

<%
final int COUNT_PER_PAGE = 100;
int offset = 0;
try
{
  offset = Integer.parseInt( request.getParameter( "offset" ) );
} catch( NumberFormatException e )
{
}

String pseudo = request.getParameter( "pseudo" );

Query<EbAccount> accountQuery = FmgDataStore.dao().query(EbAccount.class);

if( pseudo != null && !pseudo.isEmpty() )
{
  accountQuery.filter("m_pseudo >=",pseudo).filter("m_pseudo <", pseudo + "\uFFFD"); ;
}
else
{
  pseudo = "";
}

int accountListCount = accountQuery.count();
out.println("<p>FMG compte actuellement " + accountListCount + " inscrits</p>");

%>

<form name="myform" action="/admin/accounts.jsp" method="get">
search pseudo: <input type="text" name="pseudo" value="<%= pseudo %>">
<input type="submit" name="Submit" value="search"/>
</form>

	<table class="fmp-array" style="width:100%;">
	<tr><td>Inscription</td><td>Connexion</td><td>Auth</td><td>Mail</td><td>Pseudo</td></tr>
	<%
		SimpleDateFormat  simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
	    for( EbAccount account : accountQuery.offset(offset).limit(COUNT_PER_PAGE) )
	    {
	      out.println("<tr>" );
	      // subscribtion date
	      out.println("<td style='width:110px;''>"+ simpleFormat.format(account.getSubscriptionDate()) + "</td>" );
	      // connection date
	      if( account.getLastConnexion() != null ) {
	      	out.println("<td style='width:110px;'>"+ simpleFormat.format(account.getLastConnexion()) + "</td>" );
	      } else {
	        out.println("<td style='width:110px;'>???</td>" );
	      }
	    	
	      // AuthProvider
	      out.println("<td style='width:30px;'>"+account.getAuthIconHtml()+"</td>" );
	   
	      // account email
	      out.println("<td style='width:30px;'><a href='mailto:"+ account.getEmail() + "'><img src='/images/css/icon_pm.gif' border=0 alt='PM'></a></td>" );
	      
	      // Pseudo 
	      out.println("<td><a href='"+account.getProfileUrl()+"'>"+ account.getPseudo() + "</a></td>" );
	      
	      // Edit
	      out.println("<td><a href=\"/account.jsp?id="+account.getId()+"\"><img style='border=none' border=0 src='/images/css/icon_edit.gif' alt='edit' /></a></td>" );
	
	      // Avatar
	      out.println("<td><img src='"+account.getAvatarUrl()+"' border=0 alt='Avatar' width='32' height='32'></td>" );
	      
	      out.println("</tr>" );
	    }
	%>
	</table>
	
	<p>Pages :
	<%
		int p = 0;
		while(accountListCount > 0)
		{
		  out.println( "<a href='"+ request.getRequestURL() +"?offset="+(p*COUNT_PER_PAGE)+"'>"+(p+1)+"</a> " );
		  accountListCount -= COUNT_PER_PAGE;
		  p++;
		}
	%>
	</p>
	

</body>
</html>
