---
layout: default
title: troubleshooting
published: true
lang: en
categories: [dev]
---
# durring project compilation with ant
```
compilserver:
    [javac] C:\Users\J8S\Softwares\EclipseIndigoGWT\workspace\FullMetalGalaxy\build.xml:58: warning: 'includeantruntime' was not set, defaulting to build.sysclasspath=last; set to false for repeatable builds 

BUILD FAILED
C:\Users\J8S\Softwares\EclipseIndigoGWT\workspace\FullMetalGalaxy\build.xml:58: java.lang.UnsupportedClassVersionError: com/sun/tools/javac/Main : Unsupported major.minor version 51.0
```

You probably have several jre installed in your environement. JRE used by ant shall have the same version as the project (ie 1.6).

in eclipse:
Window > Preferences > Ant > Runtime > Global entries > check tools.jar path

# on your local server
After launching developpement server, the following error is displayed in navigator:
```
HTTP ERROR 500
Problem accessing /. Reason:
    ERROR: GWT.create() is only usable in client code! [...]
```
This error mean you're using client only code on server side.
Two class in model package, are compiled differently weather for client or server execution.
To solve this error, you need to compile server with the provided ant script (target compiltarget)

# while launching server
```
java.lang.RuntimeException: Unable to restore the previous TimeZone 
at com.google.appengine.tools.development.DevAppServerImpl.restoreLocalTimeZone(DevAppServerImpl.java:228) 
at com.google.appengine.tools.development.DevAppServerImpl.start(DevAppServerImpl.java:164) 
at com.google.appengine.tools.development.DevAppServerMain$StartAction.apply(DevAppServerMain.java:164) 
at
...
```
You probably have JDK 1.6.0.31 where a known GAE bug is described here http://code.google.com/p/googleappengine/issues/detail?id=6928.
you must add arguments in virtual machine: "-Dappengine.user.timezone=UTC"