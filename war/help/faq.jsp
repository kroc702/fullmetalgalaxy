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

    <h1>Foire aux questions</h1>

<p>
<b>Q: Le panneau d'information général sur l'exploitation m'annonce 'Prévision non disponible'.</b><br/>
R: Effectivement, pour avoir les prévisions météorologiques, vous devez contrôler une Pondeuse 
Météo &agrave; l'extérieur et en état de marche : pas déactivée par une marrée 
ou une zone de feu adverse.
</p>
<p>
<b>Q: Bon j'ai sorti ma pondeuse mais je ne vois toujours pas la marrée futur.</b><br/>
R: Vous ne verrez la marrée futur qu'au prochain changement de tour ou incréments de temps.
</p>
<p>
<b>Q: Je n'arrive pas &agrave; charger mon Crabe dans ma Barge.</b><br/>
R: L'action de chargement coûte un point d'action et nécessite que le véhicule chargé 
ait suffisament de place. Un Crabe chargé de 2 chars occupe 4 places.
</p>
<p>
<b>Q: Je n'arrive pas à visualiser l'ensemble du plateau de jeu.</b><br/>
R: La zone que vous pouvez visuliser d'un seul coup d'&oelig;il est variable selon la résolution
de votre écran. Vous pouvez cependant utiliser les deux icons loupes [+/-] pour changer de vue.
Si cela ne suffit pas, vous pouvez aussi utiliser le zoom du navigateur: [CTRL] + Molette 
ou bien [CTRL] + [+/-]. <br/>
Pour les utilisateurs de mac: [Pomme] + [+]
</p>
<p>
<b>Q: comment orienter la barge comme on veut quand elle sort de l''astronef ?</b><br/>
R: La meilleure solution :<br/>
clic sur le pod<br/>
clic sur la barge<br/>
clic une case adjacente<br/>
clic la deuxième case (& vérifier que ça nous convient)<br/>
valider le mouvement (clic même 2nd case ou bouton OK)
</p>
<p>
<b>Q: Comment choisir la couleur d'une unité controlée par deux destructeurs de couleur différente ?</b><br/>
R: Le premier destructeur sélectionné détermine la future couleur de l'unité contrôlée.
</p>
<p>
Q: En mode tour par tour, je ne comprend pas comment sont calculés les dates limites de jeu.<br/>
R: 
</p>
<p>
<b>Q: .</b><br/>
R: .
</p>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>
