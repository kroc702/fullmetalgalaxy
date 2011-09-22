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
Nombre de partie ouverte : <%= GlobalVars.getOpenGameCount() %>
Nombre de partie en cour : <%= GlobalVars.getRunningGameCount() %>
Nombre de partie terminée : <%= GlobalVars.getFinishedGameCount() %>
Nombre de partie annulée : <%= GlobalVars.getAbortedGameCount() %>
Nombre de partie effacée : <%= GlobalVars.getDeletedGameCount() %>
</pre>

<%@include file="include/footer.jsp"%>
</body>
</html>
