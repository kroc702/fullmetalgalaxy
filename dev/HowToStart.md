---
layout: default
title: How to start
published: true
lang: en
categories: [dev]
---


You don't have to use tool proposed here, but in this case you must configure it by yourself.

# How to start
## Install a java SDK
You need a java SDK (>=1.6) also called JDK but NOT a JRE.

http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u31-download-1501634.html

## Install eclipse
choose a version for java developper

http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/indigosr2

### Install google plugin
in eclipse: Help > Install Software > Add...

location = http://dl.google.com/eclipse/plugin/3.6

### Install mercurial
http://mercurial.selenic.com/

### Install mercurial plugin
in eclipse: Help > Install Software > Add...

location = http://mercurialeclipse.eclipselabs.org.codespot.com/hg.wiki/update_site/stable

## Create FMG project
In eclipse, create FMG project from mercurial repository:
File > new > Project > Mercurial > Clone Existing Mercurial Repository

URL = https://code.google.com/p/fullmetalgalaxy

### Configure
Once you've download all code you must update file ant-config.xml according to your environement.

property 'eclipsedir' have to be updated.
other property like 'appenginesdkdir' or 'gwtsdkdir' may also have to be updated.

### Compile
Compile project with ant
in eclipse: Window > Show View > Ant

drag and drop build.xml to Ant view.

double clic on target FullMetalGalaxy/build

This step compile client code into javascript and server code with different sources files. (so standard eclipse compilation process isn't sufficient)

For now, you don't need imagemagick, even if it raise error durring ant building process.

## Launch local server
in eclipse: Run > Debug configuration > Web Application > New

(project 'FullMetalGalaxy' and Main class 'com.google.gwt.dev.DevMode')

with your favorite web browser go to http://localhost:8888
You should see main page of full metal galxy web site.

If you see 'ERROR: GWT.create() is only usable in client code!' then you must compile server side code with provided Ant task.

# Debug client
Install GWT browser plugin: http://gwt-dev-plugin-missing.appspot.com/

with this plugin and special parameters on web page (&gwt.codesvr=127.0.0.1:9997) you can debug client page as any java source code inside eclipse debuger.

# Launch tests
JUnit tests are located in tests folder.

right clic on tests folder > Run As > JUnit Test

# Misc
## Jsp editor
to edit jsp, I'm using '[...] editor\JST Web UI'

You can also use 'amateras editor'

## Blender
Blender is used to generate all units images. As they are located in mercurial repository you don't need to have it.

`*`.blend files must have two cameras 'tactic' and 'strategy' that correspond to two zoom level.
They must also have a texture called 'color'. no special size but keep in mind that an hexagon is 34\*29 or 77\*40 pixel

## Image magick
imagemagick is used for GWT image post processing.
To reduce color number from 16M to 256 (with color table that manage alpha channel).