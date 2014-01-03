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

<h2>Statistiques globals</h2>
<p>Cette page est amenée a évoluer en fonction des demandes et de mes prioritées...</p>

<h3>Les corporations en <%= GregorianCalendar.getInstance().get( Calendar.YEAR ) -1 %></h3>
<div>
<%
    com.googlecode.objectify.Query<CompanyStatistics> companyList = FmgDataStore.dao().query(CompanyStatistics.class)
        .filter( "m_year", GregorianCalendar.getInstance().get( Calendar.YEAR ) -1 )
        .order("-m_profit");
    out.println( "<table width='100%'>");
    for( CompanyStatistics companyStat : companyList )
    {
      out.println( "<tr>");
      out.println( "<td><a href='/oldgameprofile.jsp?corpo="+companyStat.getCompany()+"'>"
          +"<IMG SRC='/images/avatar/" + companyStat.getCompany()
              + ".jpg' WIDTH=60 HEIGHT=60 BORDER=0/></a></td>" );
      out.println( "<td><b>"+companyStat.getCompany().getFullName()+"</b><br/>");
      out.println( "Bénéfices: "+companyStat.getProfit()+"<br/>");
      out.println( "Rentabilité: "+companyStat.getProfitabilityInPercent()+" %<br/>");
      out.println( "Nb exploitation: "+companyStat.getMiningCount());
      out.println( "</td>" );
      out.println( "</tr>");
    }
    out.println( "</table>");
%>
</div>    
    
<h3>Les corporations en <%= GregorianCalendar.getInstance().get( Calendar.YEAR ) %></h3>
<div>
<%
    companyList = FmgDataStore.dao().query(CompanyStatistics.class)
        .filter( "m_year", GregorianCalendar.getInstance().get( Calendar.YEAR ) )
        .order("-m_profit");
    out.println( "<table width='100%'>");
    for( CompanyStatistics companyStat : companyList )
    {
      out.println( "<tr>");
      out.println( "<td><a href='/oldgameprofile.jsp?corpo="+companyStat.getCompany()+"'>"
          +"<IMG SRC='/images/avatar/" + companyStat.getCompany()
              + ".jpg' WIDTH=60 HEIGHT=60 BORDER=0/></a></td>" );
      out.println( "<td><b>"+companyStat.getCompany().getFullName()+"</b><br/>");
      out.println( "Bénéfices: "+companyStat.getProfit()+"<br/>");
      out.println( "Rentabilité: "+companyStat.getProfitabilityInPercent()+" %<br/>");
      out.println( "Nb exploitation: "+companyStat.getMiningCount());
      out.println( "</td>" );
      out.println( "</tr>");
    }
    out.println( "</table>");
%>
</div>    
    
<h3>Les joueurs</h3>
<pre>
Nombre de compte classé : <%= GlobalVars.getActiveAccount() %>
Nombre d'inscrit : <%= GlobalVars.getAccountCount() %>
<!-- Niveau TS maximum : <%= GlobalVars.getMaxLevel() %>  -->
</pre>


<h3>Les parties</h3>
<pre>
Nombre de partie en cours : <%= GlobalVars.getCurrentGameCount() %>
Nombre de partie terminée : <%= GlobalVars.getFinishedGameCount() %>
</pre>
<h3>Les parties terminées</h3>
<pre>
Nombre de partie time Standard : <%= GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.Standard) %>
Nombre de partie time StandardAsynch : <%= GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.StandardAsynch) %>
Nombre de partie time QuickTurnBased : <%= GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.QuickTurnBased) %>
Nombre de partie time QuickAsynch : <%= GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.QuickAsynch) %>
<!-- Nombre de partie time Custom : <%= GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.Custom) %> -->
Nombre de partie d'initiation : <%= GlobalVars.getFGameInitiationCount() %>

Nombre d'hexagon  : <%= GlobalVars.getFGameNbOfHexagon() %>
Nombre de joueurs  : <%= GlobalVars.getFGameNbPlayer() %>
Somme des scores  : <%= GlobalVars.getFGameFmpScore() %>
</pre>

        

<%@include file="include/footer.jsp"%>
</body>
</html>
