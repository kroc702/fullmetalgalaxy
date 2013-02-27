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
<p>Cette page est amené a évoluer en fonction des demandes et de mes prioritées...</p>

<h3>Les joueurs</h3>
<pre>
Nombre d'inscrit : <%= GlobalVars.getAccountCount() %>
Nombre de compte actif : <%= GlobalVars.getActiveAccount() %>
Niveau maximum : <%= GlobalVars.getMaxLevel() %>
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
Nombre de partie time Custom : <%= GlobalVars.getFGameNbConfigGameTime(ConfigGameTime.Custom) %>
Nombre de partie variant Standard : <%= GlobalVars.getFGameNbConfigGameVariant(ConfigGameVariant.Standard) %>
Nombre de partie d'initiation : <%= GlobalVars.getFGameInitiationCount() %>

Nombre d'hexagon  : <%= GlobalVars.getFGameNbOfHexagon() %>
Nombre de joueurs  : <%= GlobalVars.getFGameNbPlayer() %>
Somme des scores  : <%= GlobalVars.getFGameFmpScore() %>

Nombre de minerai récupéré : <%= GlobalVars.getFGameOreCount() %>
Nombre d'unité ramené : <%= GlobalVars.getFGameTokenCount() %>
Nombre de construction : <%= GlobalVars.getFGameConstructionCount() %>
Nombre de tir : <%= GlobalVars.getFGameFireCount() %>
Nombre de control : <%= GlobalVars.getFGameUnitControlCount() %>
Nombre de control d'astronef : <%= GlobalVars.getFGameFreighterControlCount() %>
</pre>

        


    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Agressivité');
        data.addColumn('number', 'Nombre de joueur');
        data.addRows([
        <%
        float currentAgressivity = GlobalVars.STYLE_RATIO_MIN;
        float incrementAgressivity = (GlobalVars.STYLE_RATIO_MAX - GlobalVars.STYLE_RATIO_MIN) / GlobalVars.STYLE_RATIO_COUNT;
        for(int nbPlayer : GlobalVars.getStyleRatioRepartition())
        {
          out.println("['"+currentAgressivity+"', "+nbPlayer+"],");
          currentAgressivity += incrementAgressivity;
        }
        %>
          ['et plus...', 0]
        ]);

        var options = {
          width: 400, height: 240,
          title: 'répartition de l\'agressivité des joueurs',
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
    </script>
  </head>
  <body>
    <div id="chart_div"></div>

<%@include file="include/footer.jsp"%>
</body>
</html>
