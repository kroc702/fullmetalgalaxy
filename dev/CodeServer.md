---
layout: default
title: Code server
published: true
lang: en
categories: [dev]
---
#summary Code on server

todo

<wiki:toc max_depth="1" />

=Game service implementation=
Basically it receive a game event, load corresponding game, run new event, save game, return nothing
and finally broadcast update through channel API to all connected client.

=I18n=
Internationalisation on client use GWT mechanism. On server it use a specific code that look user locale to serve 
corresponding jsp or messages.
In model package, we had to find an i18n mechanism that work on client and server side. That mechanism is a 
re-implementation of GWT.create() on server side: class SharedI18n have a different implementation for server.
This means that during compilation process (cf build.xml) SharedI18n.class is overridden with code from
com/fullmetalgalaxy/server/override/com/fullmetalgalaxy/model/ressources/Shared.java

=Presence management=
cf ChannelManager

=Cron tasks=
to update:
 * rooms presence
 * games
 * account and forum synchro

=Forum connection=
FMG authenticate itself as an administrator to see all users profile. To keep a unique IP address during
synchro task, we have to use a proxy as GAE infrastructure expose a diferent IP address for every http request.

=Web site=
Nothing special indeed...
i18n is done by including relevant jsp files.
