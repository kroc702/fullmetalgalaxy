---
layout: default
title: Code client
published: true
lang: en
categories: [dev]
---
#summary FMG code client

Explain you very generals concept in FMG client part. 

<wiki:toc max_depth="1" />

=Prerequisite=
FMG is written in Java and GWT/GAE, so you’ll need to know this language and frameworks.
We also use some tools like eclipse (IDE) and mercurial (version control)
You should also have a look on Overview wiki page that give you a first introduction to package architecture.

=Client architecture=
FMG is a web application, therefore client refer to the web page (eg game.jsp) and associated javascript code. This javascript code is compiled from com.fullmetalgalaxy.client and com.fullmetalgalaxy.model java package.
When browser ask for /game.jsp page, web server inject data useful for client like: user name or game data.

Once loaded, client deal with server through http request: get data, special action with servlet, GWT RPC mechanism (cf GameServices interface) and GAE channel.

=Module & Event bus=
FMG entry point is located in class AppMain which provide several services (channel connection, bus events and other) and create required module (or MiniApp). 
Basically, AppMain instantiate all required module which are also EntryPoint and call their onModuleLoad() method. Modules do what they want to, like inserting graphic component into html document inside a dedicated div (cf MAppTabMenu) or build a model in memory (cf GameEngine).

All Modules shouldn’t know each other so they can be removed or replaced quite easily. To send event to other modules, they must use main event bus provided by AppMain singleton. Possible event are located in com.fullmetalgalaxy.client.event package.
Note that ModelUpdateEvent is historical and is sent every time GameEngine.model() changed.

=Channel mechanism=
http protocol don’t support connected mode, more specifically, server can send any message or update to client if it didn’t ask for it. The general solution is client polling: clients ask for update, server response no update… Then client ask again… Server can wait a bit before sending response, it’s long polling. To manage all these connections on server side, FMG is using a GAE dedicated API called Channel API.

Complexity is hidden and client (AppMain) can receive any message from server. Module can register themselves to AppMain to receive a specific message.

=ChatEngine=
ChatEngine is a not graphical module that manages PresenceRoom. It keep a list of connected user to a given room (ie a given game or main room 0).
This Module and some other are loaded in some other page than /game.jsp to allow users to chat.

=Board display=
MAppBoard module display game map, units and other information in a scrollable area. It sends user actions to EventPlayBuilder to construct action that will be sent to server.
In future, this part may be replaced to display map in a canvas (html5) or webgl to have fancy animation or camera angle... It’s currently organized into layer (map, fire cover, current action, units ...). Each layer is a composition of div with correct z-index and css rules.