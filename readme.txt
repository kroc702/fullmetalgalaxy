
You should have a look here: http://code.google.com/p/fullmetalgalaxy/wiki/Welcome

direct dependency:
=====================
- gwt
- appengine
- common fileupload
- objectify


my dev environement :
=====================
eclipse + ant (standard)
    html jsp [...] editor\JST Web UI (ou bien amateras editor)
	MercurialEclipse: http://cbes.javaforge.com/update
	Plugin Google: http://dl.google.com/eclipse/plugin/3.6
	Mylyn google code connector: http://knittig.de/googlecode-mylyn-connector/update/
	
eclipseCodeFormater.xml is used to format my java code

plugin browser: http://gwt-dev-plugin-missing.appspot.com/
WARNING: From GWT Website ,http://www.gwtproject.org/release-notes.html#Release_Notes_2_6_1
GWT Development Mode will no longer be available for Chrome sometime in 2014, so we improved alternate ways of debugging. There are improvements to Super Dev Mode, asserts, console logging, and error messages.
This is because of newer chrome version.Use Super Dev Mode
Other Solution is you can enable NPAPI which was disable in chrome 42 version follow the below steps
1.Open New Tab and Enter chrome://flags/#enable-npapi
2.Enable this Enable NPAPI Mac, Windows
3.Then bottom of the page click "Relunch" button.


Si vous n'utilisez pas eclipse il vous faut au minimum:
- le JDK 1.6
- le SDK de GWT et AppEngine
- un éditeur de code java/jsp
- un client mercurial
- Ant (pour lancer le mode debug, la compilation)

static images generation (in option as images are in repository):
=================================================================
- imagemagick (post processing)
- blender (pions)
-> les fichiers doivent avoir 2 cameras 'tactic' et 'strategy' correspondants aux deux 
niveaux de zoom et une texture nommée 'color'. pas de taille imposée, 
mais a titre de comparaison le sol a une taille de 34*29 et 77*40 pixel

