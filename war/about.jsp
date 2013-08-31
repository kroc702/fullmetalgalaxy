<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<%@taglib prefix="fmg" uri="/WEB-INF/classes/fmg.tld"%>
<!DOCTYPE html>
<HTML>
<head>
<title>Full Metal Galaxy - A propos</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body>
<%@include file="/include/header.jsp"%>


<h3>Disclaimer</h3>
<p><fmg:resource key="index_disclaimer"/></p> 

<p>
Je remercie G.Matthieu pour avoir officieusement autorisé ce site (08/11/07).
</p>           

<h3>Contact</h3>
<p>admin@fullmetalgalaxy.com</p>

<h3>Version actuel :</h3>
<ul>
<li>Dernière mise à jour : <%@include file="/include/version.htm"%> </li>
<li>Techno : HTML / Java / GWT / AppEngine</li>
<li>Licence : <a href="http://www.gnu.org/licenses/agpl.html"><img border="none" src="/images/agplv3-88x31.cache.png" alt="AGPL v3"/></a> 
			<a href="http://www.rodage.org/gpl-3.0.fr.html">(fr)</a>.<br/>
<small>Copyright 2010 - 2013 Vincent Legendre</small></li>			
<li>Sources dispo ici : <a href="http://dev.fullmetalgalaxy.com">http://dev.fullmetalgalaxy.com</a></li>
<li>Commit : <a href="http://code.google.com/p/fullmetalgalaxy/source/detail?r=<%@include file="/include/commitid.html"%>">
       <%@include file="/include/commitid.html"%></a> </li>
<li>Hébergé par  <img src="http://code.google.com/appengine/images/appengine-silver-120x30.gif" alt="Google App Engine" /></li>
</ul>

<h3>Précédentes versions :</h3>
<pre>
<%@include file="/include/history.html"%>           
</pre>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>