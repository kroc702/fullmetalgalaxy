---
layout: default
title: Dev environment
published: true
lang: en
categories: [dev]
---
# direct dependency:
  * gwt
  * appengine
  * common fileupload
  * objectify


# my developpement environement:
  * eclipse + ant (standard)
    * html jsp [...] editor\JST Web UI (ou bien amateras editor)
    * MercurialEclipse: http://cbes.javaforge.com/update ou http://mercurialeclipse.eclipselabs.org.codespot.com/hg.wiki/update_site/stable
    * Plugin Google: http://dl.google.com/eclipse/plugin/3.6
    * Mylyn google code connector: http://knittig.de/googlecode-mylyn-connector/update/

  * plugin browser: http://gwt-dev-plugin-missing.appspot.com/


## If you don't use eclipse you need at least:
  * le JDK 1.6
  * le SDK de GWT et AppEngine
  * un éditeur de code java/jsp
  * un client mercurial
  * Ant (pour lancer le mode debug, la compilation)

# generation des images statiques:
  * imagemagick (fonts + post processing)
  * blender (pions)
-> les fichiers doivent avoir 2 cameras 'tactic' et 'strategy' correspondants aux deux
niveaux de zoom et une texture nommée 'color'. pas de taille imposée,
mais a titre de comparaison le sol a une taille de 34\*29 et 77\*40 pixel