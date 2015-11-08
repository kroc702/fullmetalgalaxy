---
layout: default
title: Image generation
published: true
lang: en
categories: [dev]
---
#summary How units picture are created

= Render unit =
Units are modelized in 3 dimensions (files src/com/fullmetalgalaxy/client/ressouces/tokens/*.blend).

That blender files must provide :
 * two camera called 'tactic' and 'strategy' which correspond to two different zoom level.
 * One texture called ‘color’. This texture will be replaced  by different color.

To generate all images (all angles and all colors) for one unit, you must launch blender and giving it a file name and the provided script written in python (script.py) :
blender -b freighter.blend -P script.py

render.bat, generate all angle and all colors for all units.
To add color, you must edit script.py


After this "manual" generation, ant script can do the two following sequence.

= Concat images =
To avoid the load of a huge amount of picture, during the compilation process GWT concat all pictures into a single one for each color. (cf gwt ImageBundle or ResourceBundle in GWT documentation)

= Reduce color number =
After GWT compilation, image are 16Millions colors.
To save bandwidth, we use imagemagick to reduce theses images to 256 colors.