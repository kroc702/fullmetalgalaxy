<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

<meta name='gwt:property' id='app_history' content='loginbtn__newlogin__'>

</head>
<body >

<%@include file="/include/header.jsp"%>

<%@include file="menu.html"%>

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
        <li>Description : Ajoutez ici toute les info utiles aux futurs joueurs (ex: niveau des joueurs).</li>
        <li>Nombre max de joueur : Le maximum d'inscription.</li>
        <li>Taille de la carte : Elle est calculé en fonction du nombre de joueur.</li>
        <li>Vitesse du jeu : </li>
        <ul>
        <li>Standard : tour par tour</li>
        <li>StandardAsynch : Aynchrone, 3pt d'action toute les 4.8 heures (ie un tour par jour)</li>
        <li>QuickTurnBased : un tour toutes les 3 minutes</li>
        <li>QuickAsynch : Aynchrone, 8pt d'action toute les 100 secondes (ie un tour toutes les 3 minutes)</li>
        </ul>
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
    La partie est créé, mais personne n'est inscrit et elle n'est pas encore démaré. En fait
    la partie est créé en pause.
    </p>
    <h3>Inscrivez-vous</h3>
    <p>
    A priori, si vous avez créé une partie, c'est pour y jouer. Inscrivez vous en cliquant
    sur l'icone d'action <img src="/puzzles/tutorial/images/Register32.png" alt="Register" />. Choisissez
    ensuite la couleur qui vous représentera durant toute la partie. 
    Votre Astronef apparait en bas avec tous les Astronefs en orbite.
    </p>
    <p>
    Attendez que le nombre de joueur maximum soit atteint. En effet, il vous est parfaitement 
    possible de jouer une partie à 3 joueurs sur une carte prévu pour 4, mais la partie
    risque d'être moins intéressante.<br/>
    Note : Il n'est possible de s'inscrire que si la partie est en pause.
    </p>
    <h3>Lancez la partie</h3>
    <p>
    Comme la partie est créé en pause il vous reste encore à retirer la pause pour pouvoir
    jouer. Pour cela cliquez sur l'icon détail de la partie 
    <img src="/puzzles/tutorial/images/Info32.png" alt="Info" />, puis cliquez sur le bouton "Play".
    </p>
    
    <h2>Administration de la partie</h2>
    <p>
    Tous les joueurs inscrit à une partie, peuvent activer/déactiver la pause. 
    Cependant, et afin de détecter la triche, toutes les actions des joueurs sont enregistrées.
    Vous pouvez avoir un apperçu en cliquant l'icon 
    <img src="/puzzles/tutorial/images/Info32.png" alt="Info" />
    puis l'onglet "log" et "admin log".
    </p>
    <p>
    En tant que créateur de la partie, vous avez d'autres droits comme l'édition de case 
    ou de pions. A priori vous n'aurez pas besoin de cette fonctionalité.<br/>
    Si vous désirez banir un joueur (qui ne joue plus par exemple) contactez l'administrateur.
    </p>
    <p>
    Cet aspect du jeu n'étant pas encore très développé, n'hésitez pas à faire vos demandent
    ou remarques sur le forum.
    </p>
    
<%@include file="/include/footer.jsp"%>
</body>
</HTML>
