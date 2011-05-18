<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<style type="text/css">@import url( /style.css );</style>

</head>
<body >

<%@include file="/include/header.jsp"%>

<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />

<p>
Le mécanisme des zones de feu est la clef de voute du jeu. Bien comprises,
 elles vous permettront de défendre une position ou percer les lignes ennemies.
</p>

<p>
Un tir réalisé par deux destructeurs sur une cible à portée (2PA) détruit 
systématiquement celle-ci.<br/>
Mais il n’est pas possible de pénétrer dans une zone de feu adverse. C.-à-d.
 sur une case à portée de deux destructeurs du même propriétaire. Il vous est
  possible de visualiser ces zones de feu grâce au bouton <img src="images/FireCoverOn32.png"/>.<br/>
Un destructeur tout seul ne sert donc à rien : il ne peut ni défendre si attaquer.
</p>

<p>
Attention, un destructeur peut tout de même entrer dans une zone de feu adverse 
s’il effectue immédiatement un ou deux tirs permettant d’éliminer cette zone de feu.
</p>

<h2>Exemple 1 :</h2>
<p>
<table width="100%" >
  <tr>
    <td> <img src="images/fire01.jpg"/> </td>
	<td> <img src="images/fire02.jpg"/> </td>
  </tr>
</table>
Les chars ont tous une portée de 2 cases : les chars bleus ne peuvent pas pénétrer dans la zone de feu rouge.
</p>

 <br/>
<table width="100%" >
  <tr>
    <td> <img src="images/fire03.jpg"/> </td>
	<td> <img src="images/fire04.jpg"/> </td>
  </tr>
  <tr>
    <td> <img src="images/fire05.jpg"/> </td>
	<td> <img src="images/fire06.jpg"/> </td>
  </tr>
</table>

Ici la situation est différente, car un char bleu a été remplacé par un gros tas 
(un destructeur ayant une portée de 3 cases). 
Il est donc possible de faire pénétrer 
le char bleu dans la zone de feu rouge et tirer immédiatement.

<p>
A noter : <br/>
les chars sur une montagne ont une portée de 3 cases et les gros tas 
ne peuvent pas ce déplacer sur les montagnes.<br/>
Une unité neutralisé ne peut pas tirer, mais peut reculer d’une case pour sortir de la zone de feu.
</p>

<h2>Exemple 2 :</h2>
<table width="100%" >
  <tr>
    <td> <img src="images/fire10.jpg"/> </td>
	<td> <img src="images/fire11.jpg"/> </td>
  </tr>
</table>
<p>
Les trois destructeurs bleu aimeraient capturer le char orange, mais malgré sa neutralisation,
il est toujours dans une zone de feu adverse.
</p>

<img src="images/fire12.jpg"/>
<p>
La neutralisation du char orange a permis au troisième destructeur bleu de s'approcher suffisamment
pour neutraliser un second char orange. Le contrôle du premier char orange est maintenant possible.
</p>
<img src="images/fire14.jpg"/>
<p>
Attention tout de même, si le gros tas bleu neutralisant s'éloigne alors les chars oranges peuvent
neutraliser le char bleu avant que celui-ci ai le temps de remplacer le gros tas dans la neutralisation. 
</p>
<img src="images/fire13.jpg"/>
<p>
De même, malgré la zone de feu qui semble neutralisé, le char bleu ne peut pas avancer plus, car 
il effectue lui même la neutralisation. En fait, les destructeurs ne peuvent pas neutraliser pendant leurs 
déplacements.
</p>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>
