---
layout: default
title: Overview_fr
published: true
lang: fr
categories: [dev]
---
#summary présentation générale
#labels Featured

<wiki:toc max_depth="1" />

Cette introduction devrait vous faciliter la compréhension de l'architecture globale de FMG. 
Ce premier jet ne demande qu'à s'améliorer : n'hésitez pas à me contacter si vous êtes intéressé par le projet.

=Les packages=
FMG est entièrement écrit en java sans l'utilisation d'applet. La partie cliente utilise GWT, 
un compilateur Java vers javascript, et s'exécute sur le navigateur en javascript. La partie serveur est 
compilée de façon standard en java bytecode et s'exécute sur le serveur d'applications AppEngine.

Le logiciel se décompose en trois principaux packages:
http://wiki.fullmetalgalaxy.googlecode.com/hg/images/main-packages.png

==client==
regroupe toutes les classes s'exécutant uniquement sur la partie client du jeu. 
Elles utilisent le package model.

==model==
regroupe toutes les classes s'exécutant à la fois sur la partie client et serveur. 
Le package com.fullmetalgalaxy.model.persist représente l'essentiel des classes métiers qui 
modélisent le jeu et implémentent les règles: ce sont des classes qui seront stockée en base de données.

==server==
regroupe toutes les classes s'exécutant uniquement sur la partie serveur. 
Elles utilisent le package model.

Pour la suite, il est préférable de connaitre un minimum l'infrastructure AppEngine et le framework GWT, 
je vous renvoie donc à la présentation de ces outils.

=L'IHM=
L'IHM est architecturé autour de client.ModelFmpMain un singleton qui contient un EbGame, une liste de EbAccount 
et quelques autres informations.

Chaque élément graphique peut s'enregistrer auprès de ModelFmpMain comme écouteur d'évènement pour être prévenu 
de la moindre mise à jour du modèle.
Les éléments graphiques étant prévenus, charge à eux de décider, ou non, s'ils doivent se mettre à jour.


La classe model.persist.gamelog.EventsPlayBuilder (contenu par ModelFmpMain) est la principale classe impliquée 
dans la construction des événements: chaque élément graphique lui envoie des brides d'actions réalisées par 
l'utilisateur et elle en construit des événements de jeu à envoyer au serveur.

=Le model=
EbAccount représente un compte utilisateur.

EbGame représente une partie et contient entre autres :
 * Une liste de pion (EbToken)
 * Une liste de joueur (EbRegistration)
 * Une liste d'événement (package model.persist.gamelog)

Un joueur peut interagir avec le jeu uniquement au travers d'événement qui représente l'ensemble des actions possibles. 
Chaque événement implémente les règles (et restriction) du jeu et peut être joué dans les deux sens du temps.

Jouer un coup se résume donc à créer un événement, puis le jouer ( methode exec() ) sur la partie.
De la même façon, pour revenir en arrière il 'suffit' de dépiler les événements en les jouant à l'envers ( méthode unexec() ).


=Mécanique globale d'un coup=
Le diagramme suivant montre le chargement d'une partie et l'exécution d'un mouvement par un joueur.
<wiki:comment>J'ai utilisé mscgen pour la génération du diagramme de séquence (http://www.mcternan.me.uk/mscgen/)
</wiki:comment>
http://wiki.fullmetalgalaxy.googlecode.com/hg/images/game-event.png


=Le dépot tools=
Le dépôt tools rassemble les outils utiles à FMG mais ne faisant pas partie du jeu ou du site.

Il contient surtout un convertisseur de fichier de partie:
 * .bin une sérialisation java standard, le seul format lu par le serveur.
 * .xml une sérialisation via xstream, lisible par un humain
 * .fmp le format utilisé par fmp.exe (un jeu par mail)