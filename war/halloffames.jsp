<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.server.datastore.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Liste des joueurs</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

	<table class="fmp-array" style="width:100%;">
	<%
		SimpleDateFormat  simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
	    for( EbAccount account : FmgDataStore.getAccountList() )
	    {
	      out.println("<tr>" );
	      // subscribtion date
	      out.println("<td>"+ simpleFormat.format(account.getSubscriptionDate()) + "</td>" );
	      
	      // account name
	      out.println("<td><a href=\"/profile.jsp?id="+account.getId()+"\">"+ account.getPseudo() + "</a></td>" );
	      
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
<%@include file="include/footer.jsp"%>
</body>
</html>
