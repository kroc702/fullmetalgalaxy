---
layout: default
title: écarts de règles
published: true
lang: fr
---

* TOC
{:toc}

FMG tente de coller au plus près des règles originales de full metal planete tout en proposant des modes de jeu adapté au web. En particulier, il est possible (mais pas obligatoire) de jouer en mode parallèle (cf les modes de jeu & variantes) qui élimine la notion de tour de jeu. Ceci entraine quelques interprétations des règles.

## Les neutralisations

Elles sont automatiques et n'empêchent pas de déplacer les neutralisants. En revanche, si l'un des neutralisants se déplace, la neutralisation est immédiatement rompue (cf les zones de feu). Les différences tactiques par rapport aux règles originales sont minimes.

## Deux chars cote à cote sur une montagne

Ce n’est pas interdit, mais inutile en défense. Si deux chars, du même propriétaire, sont côte à côte sur des montagnes, l'un des deux ne produit pas de zone de feu (il est toujours possible de tirer).
En mode tour par tour, il n'est cependant pas possible de valider la fin de tour dans cette configuration.

## Les marées

Il n’est pas possible de prédire la marée à plus de deux tours, même avec 3 pondeuses météo.
Les marées ne sont pas tirées avec des cartes ce qui peut changer les probabilités d'aparitions.

Voici l'algorithme utilisé:
<pre>
A chaque nouvelle marée, on ajoute -1, 0 ou +1 au niveau moyen de la marée.
La proba de la marée suivante est 1/3 haute, 1/3 normal et 1/3 basse.
Si ce niveau dépasse 2 (ou -2) la proba passe a 1/2 normal et 1/2 basse (ou 1/2 haute et 1/2 normal).
au pire on peu donc avoir 6 marées hautes !
</pre>

## Le déploiement

Le déploiement est possible à 4 cases du centre de l’astronef et non plus 3 à partir des podes. (cela ajoute 3 cases)
Le déploiement est réellement caché ce qui est difficilement réalisable sur plateau.

## Le décollage

Le décollage d'un astronef est gratuit (0PA) pour éviter les frustrations inutiles des nouveaux joueurs ignorants de cette règle.

## Le décollage anticipé

Le tour 21 est spécale: aucun PA n'est distribué mais il est possible de dépenser les PA économisé pour des actions non agressive. Il est aussi possible de décoller en avance.

La partie aurra 26 tours.

## Bugs...

FMG comporte encore des bugs qui ce matérialisent parfois par des écarts des règles.
