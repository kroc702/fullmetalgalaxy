<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<HTML>
<head>
<title>Full Metal Galaxy - A propos</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body>
<%@include file="/include/header.jsp"%>

<div class="inline-ul" >
<jsp:include page="<%= I18n.localize(request,response,\"/development/menu.html\") %>" />
</div>


<p>
Comparé à certains MMO, FMG est certes un projet modeste... Mais il est déjà trop gros pour être porté 
par une seule ame.<br/>
<br/>
En effet, pour s'établir dans la durée, un jeu en ligne doit bien sûr fédérer une communauté de joueur, 
mais aussi de contributeur. 
Comme beaucoup de projet amateur et non-commercial, Full Metal Galaxy a choisi une licence open source 
(AGPL) pour permettre à qui le souhaite, de contribuer sans risquer de voir son travail anéanti par une 
mésentente au sein de l'équipe.<br/>
FMG a donc besoin de vous pour continuez à progresser !
</p>

<a name="contribute" ></a>
<h2>Comment contribuer ?</h2>
<p>
Il n'y a pas que le code dans la vie (d'un logiciel). Quelles que soient vos compétences, vous pouvez sans 
doute aider au développement du jeu. Voici quelques exemples:
</p>

<h3>Trouver des bugs et améliorations possibles</h3>
<p>C'est tout bête, mais les développeurs ne voient pas tout et n'ont pas forcément les meilleures idées.
</p>

<h3>Écrire de la documentation</h3>
<p>Il y a déjà quelques articles expliquant les bases du jeu et l'utilisation du logiciel. Ces textes 
peuvent être obsolètes ou à clarifier. Vous pouvez aussi participer à l'écriture d'un background ou 
traduire FMG dans une autre langue.
</p>

<h3>Gérer la communauté</h3>
<p>C'est peut être un bien grand mot vu la taille actuelle de la communauté, il n'empêche que vous pouvez 
répondre a certaines questions des joueurs et montrer que l'équipe est active. Vous pouvez aussi promouvoir 
FMG sur d'autres forums et initier des débutants.
</p>

<h3>Améliorer les graphismes</h3>
<p>Si vous savez manier les logiciels de dessin et/ou de 3D, vous pourrez sans doute rendre certaines images 
plus agréables et lisibles.
</p>

<h3>Financer le serveur</h3>
<p>FMG est hebergé sur des infrastructures Google App Engine. C'est un service qui devient payant au-delà 
d'une certaine utilisation. Pour l'instant, je paie la note qui reste modeste, mais à l'avenir on peut imaginer 
des besoins de financement.<br/>
On peut aussi imaginer de créer une association permettant de gérer les futurs problèmes.
</p>

<h3>Coder les améliorations possibles</h3>
<P>On a bien sûr besoin de développeur informatique :)<br/>
Pour info le code est écrit en java et utilise GWT & GAE. Il n'est vraiment pas indispensable d'avoir des 
connaissances web particulières. Allez voir la doc et le code pour vous faire une idée.
</p>

<h2>Les raisons qui vous pousseraient à participer au projet ?</h2> 
<p>
Hum, pas l'argent en tout cas : c'est un loisir de passionné. 
Vous apprendrez sans doute plein de choses et cela donnera une petite visibilité à votre travail : Full Metal Planete 
étant connu, il attire toujours un peu de monde.
Initier une communauté autour d'un jeu qui perdure sera aussi une source de satisfaction...
</p>
<img src="/images/logoGoogleCode.png" style="float:left;"/>
<p>
Si le projet vous intéresse ou êtes simplement curieux, n'hésitez pas à visiter le site dédié au developpement 
(<a href="http://dev.fullmetalgalaxy.com">http://dev.fullmetalgalaxy.com</a>) et me contacter (admin@fullmetalgalaxy.com)
pour obtenir des infos concrètes. Et de toute façon, le moral est la clef de voute de tout projet amateur.
</p>
            
<h3>Les atouts du projet :</h3>
<ul>
<li>Les mécanismes du jeu sont éprouvés et ont déjà remporté un certain succès au travers de Full Metal Planete.</li>
<li>Pour la même raison, une petite communauté d'ancien joueur se créer, assurant le démarrage du projet.</li>
<li>Il n'existe que très peu (pas ?) de jeu similaire : wargame asynchrone jouable à plus de 2 au travers d'un navigateur.</li>
<li>Le jeu est déjà jouable et joué.</li>
<li>La licence open source devrait assurer une certaine pérennité.</li>
</ul>

          
<h2>Fork</h2>
<p>
Il vous est tout a fait possible de créer une nouvelle instance du serveur avec ou sans modification de votre cru ! 
N'oubliez pas cependant que vous êtes tenu de publier toutes les améliorations que vous pourrez effectuer.<br/>
Et me prévenir serait sympa ;)
</p>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>