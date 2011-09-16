<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.*,com.fullmetalgalaxy.model.constant.*,com.googlecode.objectify.Query" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Classement des joueurs</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<h2>Classement des joueurs</h2>
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
accountQuery.order("-m_currentLevel"); ;

for( EbAccount account : accountQuery.offset(offset).limit(COUNT_PER_PAGE) )
{
  
  out.println(account.buildHtmlFragment() );
}
%>
	
	<p>Pages :
	<%
		int p = 0;
		int accountListCount = GlobalVars.getAccountCount();
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
