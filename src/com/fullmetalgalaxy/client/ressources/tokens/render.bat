blender -b ore3.blend -P script.py
blender -b ore2.blend -P script.py
blender -b ore5.blend -P script.py
blender -b ore0.blend -P script.py
blender -b freighter.blend -P script.py
blender -b turret.blend -P script.py
blender -b barge.blend -P script.py
blender -b speedboat.blend -P script.py
blender -b tank.blend -P script.py
blender -b heap.blend -P script.py
blender -b crab.blend -P script.py
blender -b weatherhen.blend -P script.py
blender -b pontoon.blend -P script.py
blender -b sluice.blend -P script.py
blender -b walkertank.blend -P script.py
blender -b tarask.blend -P script.py
blender -b hovertank.blend -P script.py
blender -b crayfish.blend -P script.py

rem find . -path ./render/*.png -exec convert \{\} -sharpen 0x.7 \{\} \;

