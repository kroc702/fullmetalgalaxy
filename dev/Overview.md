---
layout: default
title: Overview
published: true
lang: en
categories: [dev]
---
#summary général presentation
#labels Featured

<wiki:toc max_depth="1" />

This introduction should help you understand the overall architecture of FMG.
This first draft asks only get better: do not hesitate to contact me if you are interested in the project.

=Packages=
FMG is written entirely in Java without using applet. Client side use GWT,
a Java to JavaScript compiler, and runs on the browser as javascript. The server part is
compiled to standard Java bytecode and runs on the application server AppEngine.

The software is divided into three main packages:
http://wiki.fullmetalgalaxy.googlecode.com/hg/images/main-packages.png

==client==
includes all classes that run solely on client side of the game
They use the 'model' package.

==model==
includes all classes that run on both the client and server part.
The package com.fullmetalgalaxy.model.persist represents the bulk of business classes
which modeled the game and implement its rules: these are classes are stored in database.

==server==
includes all classes that run only on the server side.
They use the 'model' package.

For the rest, it is better to know a minimum on AppEngine framework and GWT.

=HMI=
HMI is organized around client.ModelFmpMain a singleton that contains an EBGames, a list of EbAccount
and some other information.

Each widget can register themself to ModelFmpMain as event listener to be notified
of any update of the model.
Once a widget is notified, he is in charge of deciding whether or not they should be updated.

Class model.persist.gamelog.EventsPlayBuilder (ModelFmpMain content) is the major class involved
in events construction: each widget sends single action performed by
the user and it builds game events to send to the server.


=Model=
EbAccount represents a user account.

EBGames is a game and shall include:
  * A units list   (EbToken)
  * A players list (EbRegistration)
  * An events list (model.persist.gamelog package)

A player can interact with the game only through event that represents all possible actions.
Each event implements the rules (and restrictions) and the game can be played in both way of time.

Make a move boils down to creating an event, then play it ( exec() method ) on the game.
Similarly, to go back you 'only' need to unstack the events and playing it backwards ( unexec() method ).


=Make a move=
The following diagram shows a game loading and execution of a movement by player.
<wiki:comment>J'ai utilisé mscgen pour la génération du diagramme de séquence (http://www.mcternan.me.uk/mscgen/)
</wiki:comment>
http://wiki.fullmetalgalaxy.googlecode.com/hg/images/game-event.png


=Tools repository=
The tools repository brings useful tools to FMG but not part of the game or site.

It mainly contains a game file converter:
  *.bin java serialization standard, the only format read by the server.
  *.xml a serialization through xstream, human-readable.
  *.fmp format used by fmp.exe (to play by email)