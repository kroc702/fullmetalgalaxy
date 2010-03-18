rem 1er param: texte
rem 2nd param: filename

rem convert -size 40x40 xc:white -font Courier-New-Bold -pointsize 50 -annotate +0+30 %1 -annotate +4+30 %1 -bordercolor white -border 2x2 -blur 0x2  -negate -draw "point 0,0 #FFFFFE" -draw "point 0,39 #FFFFFE"  -trim +repage -draw "point 0,0 #FFFFFF" -draw "point 0,39 #FFFFFF"  mask.png

convert -size 60x40 xc:white -font Courier-New-Bold -pointsize 40 -annotate +1+30 %1 -annotate +5+30 %1 -blur 0x1 -fill #FF0000 -draw "point 0,0" -draw "point 0,39" mask1.png
convert mask1.png -trim +repage mask2.png
convert mask2.png -fill #FFFFFF -draw "point 0,0" -draw "point 0,39" mask3.png
convert mask3.png -negate  mask.png


convert -size 40x40 xc:white -font Courier-New-Bold -pointsize 40 -annotate +1+30 %1 -annotate +5+30 %1 -bordercolor white -border 2x2  -shade  320x21 -draw "point 0,0 #FFFFFE" -draw "point 0,39 #FFFFFE" -trim +repage -draw "point 0,0 #FFFFFF" -draw "point 0,39 #FFFFFF" shadow.png

convert shadow.png  -sigmoidal-contrast 5x50%  shadow.png
convert shadow.png  mask.png  +matte -compose CopyOpacity -composite   shadow.png

rem convert -font Courier-New-Bold -pointsize 100 label:W  -trim +repage -bordercolor black -border 2x2  font.png

composite texture.jpg  -compose multiply shadow.png  mask.png %2.png

