<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.*,com.fullmetalgalaxy.model.constant.*,com.googlecode.objectify.Query" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Liste des joueurs</title>
        
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

Query<EbAccount> accountQuery = FmgDataStore.dao().query(EbAccount.class);
int accountListCount = accountQuery.count();
out.println("<p>FMG compte actuellement " + accountListCount + " inscrits</p>");

%>

	<table class="fmp-array" style="width:100%;">
	<tr><td>Date d'inscription</td><td>Pseudo</td><td>Message</td></tr>
	<%
		SimpleDateFormat  simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
	    for( EbAccount account : accountQuery.offset(offset).limit(COUNT_PER_PAGE) )
	    {
	      out.println("<tr>" );
	      // subscribtion date
	      out.println("<td style='width:150px;''>"+ simpleFormat.format(account.getSubscriptionDate()) + "</td>" );
	      
	      // Pseudo 
	      out.println("<td><a href='"+account.getProfileUrl()+"'>"+ account.getPseudo() + "</a></td>" );
	      
	      // Message
	      if( account.isAllowPrivateMsg() && account.haveEmail() )
	      {
	      	out.println("<td><a href='"+account.getPMUrl()+"'><img src='" + "/images/css/icon_pm.gif' border=0 alt='PM'></a></td>" );
	      }
	      else
	      {
	        out.println("<td></td>" );
	      }
	      
	      // admin option
	      if(Auth.isUserAdmin(request, response))
	      {
	      	out.println("<td><a href=\"/account.jsp?id="+account.getId()+"\"><img style='border=none' border=0 src='/images/css/icon_edit.gif' alt='edit' /></a></td>" );

	      	// AuthProvider
	      	out.println("<td>"+account.getAuthIconHtml()+"</td>" );
	      	
	      	// account email
	      	out.println("<td><a href='mailto:"+ account.getEmail() + "'>"+ account.getEmail()+"</a></td>" );
	      }
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
	
<%@include file="include/footer.jsp"%>
</body>
</html>
