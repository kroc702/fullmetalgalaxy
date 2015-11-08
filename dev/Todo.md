---
layout: default
title: todo
published: true
lang: en
categories: [dev]
---
#summary Big subject to work on

aim of this page isn't to replace Issues tab, but to define main areas where improvement are needed.
Some functions discuss here won't be implemented at all, but we should think about them...

<wiki:toc max_depth="1" />

=Some big function=
==on model and client side==
 * Allow player messages to be located on board
 * Allow map to be cylinder (link borders together)
 * Implement fog of war to discover map and opponents with our units

==on server==
 * Create a robot player (or AI) for practice or to replace players

==very independant from existing code==
 * Build a solution to merge several rss feed
 * Generate image to be included in forum signature
 * Use an fmg email address that forward messages to real address
 * Add/Improve social functions (comment fairplay on profiles, corporation...)


=A cleaner HMI=
 * grid/background color associated to each planet type
 * game located on a galaxy map ?
 * rotate board while playing
 * improve all menu and dialogs

=Graphics=
 * redraw some land (and new lands styles)
 * get a carton style (over drawing) for units ?
 * allow to choose different units styles ?
  
=Add community functions=
 * twiter/facebook pages for main events ?
 * cleaner messaging -> edit messages
 * in game message ? flare or any drawing on board ?
 * add user flag/avatar over freither or in unit detail/selection
 * create corporation
 * note on player profile

=Game function=
 * many variant (cf Issues tab)
 * allow users to create scenario
 * add an AI
 * link with fmp.exe (common file format)

=Technical improvement=
 * allow bigger game (unlimited map)
 * create an action EvtMove with several hexagon
 * use UIBinder ?
 * rewrite board widget to use canvas, playn or webgl...
 
=Define production procedure=
 * automatic tests on model
 * automatic tests on server
 * real prodecure before launching new version

=Misc=
 * support for OpenID
 * How should we manage international community (chat, forum, game,...)