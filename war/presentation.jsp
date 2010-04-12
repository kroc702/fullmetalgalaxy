<%@ page import="com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<HTML>
<head>
<title>Présentation de Full Metal Galaxy</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body >
<%@include file="include/header.jsp"%>


<h2>Mécanismes</h2>
<p>
Il s'agit d'un wargame (ou jeu de strategie) gratuit jouable dans un navigateur sans aucun
plugin. La dur&eacute;e et la vitesse des parties sont configurables : 3 minutes 
par coup pour une partie de 1h30 ou un coup par jour pour une partie
d'un mois. <br/>
Il est possible de jouer chacun à son tour
ou tous en m&ecirc;me temps (mode asynchrone) à la fa&ccedil;on
des RTS.<BR/>
Chaque partie est ind&eacute;pendante, il n'y a donc pas de monde
persistant. La communaut&eacute; et le classement donnent une
consistante &agrave; l'univers, mais il vous est tout &agrave; fait possible de ne
jouer qu'une partie de temps &agrave; autre sans aucun d&eacute;savantages en
jeu.
</p>

<h2>Résumé d'une partie</h2>

Vous posez votre astronef sur une planète pour une campagne d'extraction. 
Vous recevrez &agrave; intervale régulier (en mode asynchrone) ou au début de votre tour
(en mode tour par tour) un certain nombre de points d'action que vous pouvez dépenser 
à tout instant pour :
<ul>
<li>Ramasser des minerais à l'aide de vos transporteurs et les ramener dans votre Astronef</li>
<li>Créer des pièces supplémentaires à l'aide du minerai et de votre Pondeuse Météo,</li>
<li>Affaiblir l'adversaire avec vos destructeurs en lui détruisant ou en lui capturant 
des pièces</li>
<li>Menacer ou gêner ses mouvements en occupant les zones appropriées</li>
<li>Capturer un ou plusieurs Astronefs, pour augmenter le nombre de pièces sous votre contrôle, 
bénéficier de points d'action supplémentaires et décoller en fin de partie avec plusieurs 
astronefs et leur contenu (minerais et pièces).</li>
</ul>
<p>
Les marées changeront, en cours de partie, la topologie de la carte rendant 
certaines zones inacessibles par voie terrestre.
</p>
<p>
Pendant toute la partie, vous pourrez aussi échanger avec vos adversaires (via mail, 
messages privés ou chat intégré) pour 
lancer des actions communes.
</p>
<p>
Si tout se passe bien pour vous, vous décollerez à la fin de la partie avec des véhicules 
(1 point chacun) et des minerais (2 points chacun). Le joueur totalisant le plus grand nombre 
de points aura gagné.
</p>

<h2>Copies d'écrans</h2>

<a href="/images/screenshots/tutorial1.jpg" >
    <img src="/images/screenshots/min/tutorial1.jpg" /> 
</a>
<a href="/images/screenshots/tutorial2.jpg" >
    <img src="/images/screenshots/min/tutorial2.jpg" />
</a>

<h2>Pour démarrer</h2>
<p>
Le moyen le plus simple pour avoir un bon aperçu et s'initier au jeu est de faire le 
<a href="/game.jsp?id=/puzzles/tutorial/model.bin" >tutorial</a>.
Il ne dure qu'une dizaine de minutes et vous n'avez pas besoin de créer de compte.
</p>
<p>
Par la suite, vous pouvez tenter les quelques problèmes proposés ou vous inscrire &agrave; une
partie regroupant des débutants. (Pour s'inscrire cliquez sur 
<img src="/puzzles/tutorial/images/Register32.png" alt="Register" />)
</p>

<h2>Pour initier une partie</h2>
<p>
Il est possible qu'une petite explication vous soit utile pour la création d'une nouvelle partie.
Pour cela jetez un coup d'&oelig;il <a href="help/newgame.jsp">ici</a>.
</p>

<%@include file="include/footer.jsp"%>
</body>
</HTML>