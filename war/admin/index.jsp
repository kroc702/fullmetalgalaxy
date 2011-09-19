<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<HTML>
<head>

</head>
<body >

<a target="_top" href="http://www.fullmetalgalaxy.com/">Retour Accueil</a><br/>
<br/>
<a target="_blank" href="https://www.ovh.com/managerv3/">ManagerV3 OVH</a> <br/>
<br/>
<a target="_blank" href="https://www.google.com/analytics/reporting/?reset=1&id=13711373">Analytics</a><br/>
<br/>
<a target="_blank" href="https://www.google.com/webmasters/tools/">Web Tools</a><br/>
<br/>
<a target="_blank" href="http://code.google.com/p/fullmetalgalaxy/">Google code</a><br/>
<br/>
<a target="_blank" href="https://www.google.com/a/fullmetalgalaxy.com">Apps FMG</a><br/>
<a target="_blank" href="http://mail.fullmetalgalaxy.com">Mail FMG</a><br/>
<br/>
<a target="_blank" href="http://latest.fullmetalgalaxy2.appspot.com/">Autre version</a><br/>
<br/>

<form name="myform" action="/admin/Servlet" method="post" enctype="multipart/form-data">
upload game file: <input type="file" name="gamefile"><br/>
<input type="submit" name="Submit" value="Submit your game"/>
</form>

<a href="/admin/Servlet?deletecache=1" >delete cache</a><br/>
<br/>
<a href="/admin/Servlet?deletesession=1" >delete session from datastore</a><br/>
<br/>
<a href="/admin/Servlet?forumpostgame=1" >post a new game on forum (test)</a><br/>
<br/>
<a href="/admin/accounts.jsp" >accounts list</a><br/>
<br/>

</body>
</HTML>
