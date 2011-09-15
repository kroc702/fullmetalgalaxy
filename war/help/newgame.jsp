<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body >

<%@include file="/include/header.jsp"%>

<div class="inline-ul" >
<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />
</div>

    <h1>Initier une nouvelle partie</h1>
    <p>
    La création d'une partie est relativement simple. Cependant, cette partie du site
    étant la moins aboutie, certain bug ou problème de mise en page peuvent surprendrent...
    </p>
    <h2>Création rapide</h2>
    <p>
    Sur la page "Tableau des missions" cliquez sur "Nouvelle exploitation".
    </p>
    Ici, seul 6 champs sont indispenssables:
    <ul>
        <li>Nom : C'est le nom de la partie.</li>
        <li>Nombre max de joueur : Le maximum d'inscription.</li>
        <li>Description : Ajoutez ici toute les info utiles aux futurs joueurs (ex: niveau des joueurs).</li>
        <li>Taille de la carte : Elle est calculé en fonction du nombre de joueur.</li>
        <li>Mode de jeu : tour par tour ou parallèle</li>
        <li>Vitesse du jeu : lente ou rapide</li>
    </ul>
    Les autres champs sont de simples indications sur vos choix.<br/>
    Note: Comme pour le jeu de plateau, pour être intéressant les parties de 
    Full Metal Galaxy doivent comporter entre 3 et 9 joueurs.
    <p>
    C'est tout, cliquez sur "Sauver/céer la partie" (utilisez l'ascenseur).
    </p>
    
    <h2>Personalisation</h2>
	    <h3>Onglet "Carte"</h3>
	    <p>
	    Cet onglet vous permet d'éditer la carte case par case. Attention, dès que vous
	    visité cet onglet, la génération automatique est déactivé.<br/>
	    Vous pouvez :</p>
	    <ul>
           <li>Modifier le thème de la planète.</li>
           <li>Modifier la taille de la carte sans tenir compte du nombre de joueur.</li>
           <li>Modifier la forme de la carte. (utilisez le bouton "effacer")</li>
           <li>Editer chaque case comme un dessin.</li>
           <li>Charger une carte toute faite. (pour l'instant la carte de base uniquement)</li>
	    </ul>
	    <h3>Onglet "Pions"</h3>
	    <p>
	    Attention, dès que vous visité cet onglet, la pose automatique des minerais
	    est déactivée.<br/>
	    Cet onglet vous permet de positionner les minerais où bon vous semble.
	    Vous pouvez aussi positioner n'importe quelle autre pièce avant le début de la partie.<br/>
	    Pour faire tourner un pion avant de le poser, cliquez dessus. (en haut a gauche)<br/>
	    Pour effacer un pion, utilisez le clic droit de la souris.<br/>
	    </p>
	    <h3>Les autres onglets</h3>
	    <p>
	    Les autres onglets ne sont pour l'instant pas ouvert au public, ils m'ont, entre autre,
	    servis à la saisie du tutorial.
	    </p>
    
    <h2>Démarrage</h2>
    <p>
	    La partie est créé, mais personne n'est inscrit et elle n'est pas encore démaré.
    </p>
    <h3>Inscrivez-vous</h3>
    <p>
	    A priori, si vous avez créé une partie, c'est pour y jouer. Inscrivez vous en cliquant
	    sur l'icone d'action <img src="/help/images/Register32.png" alt="Register" />. Choisissez
	    ensuite la couleur qui vous représentera durant toute la partie. 
	    Votre Astronef apparait en bas avec tous les Astronefs en orbite.
    </p>
    <h3>Lancez la partie</h3>
    <p>
	    Comme la partie est créé en pause. Vous pouvez la lancer quand vous voulez, mais il est préférable
	    d'attendre que le nombre de joueur maximum soit atteint. En effet, il vous est parfaitement 
	    possible de jouer une partie à 3 joueurs sur une carte prévu pour 4, mais la partie
	    risque d'être moins intéressante.
    </p>
    
    <h2>Administration de la partie</h2>
	<p>
		En tant que créateur de la partie, vous avez certains droits et devoirs sur celle-ci.
		<br/>
		Pour le respect des autres joueurs, vous vous engagez moralement à mener cette partie à terme. 
		A défaut, contactez l'administrateur qui fera son possible pour terminer la partie.
	</p>
    <h3>la Pause</h3>
    <p>
	    Dans certains cas, il peut être intéressant de mettre la partie en pause. 
	    Icon <img src="/puzzles/tutorial/images/Info32.png" alt="Info" /> puis bouton pause/play.
		<br/>
		Assurez-vous que tous les joueurs soient au courant.
    </p>
    <h3>Banir un joueur</h3>
    <p>
	    Si un joueur ne donne plus signe de vie après plusieurs relances ou s'il désire abandonner, 
	    vous pouvez le bannir de la partie. Ses pièces restent sur le plateau de jeu et pour ne pas 
	    déséquilibrer la partie, il est largement préférable de trouver un remplaçant. 
		<br/>
		Pour qu'un nouveau joueur prenne ça place, la partie doit être en pause: il est alors possible 
		de la rejoindre comme en début de partie.
    </p>
    <h3>Autre</h3>
    <p>
	    Il existe d'autre possibilité comme l'édition de case 
	    ou de pions. A priori vous n'aurez pas besoin de cette fonctionalité.
    </p>
    <p>
    Cet aspect du jeu n'étant pas encore très développé, n'hésitez pas à faire vos demandent
    ou remarques sur le forum.
    </p>
    
<%@include file="/include/footer.jsp"%>
</body>
</HTML>
