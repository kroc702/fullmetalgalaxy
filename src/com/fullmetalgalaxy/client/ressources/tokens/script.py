import Blender
from Blender import *
from Blender.Scene import Render

from Blender.Mathutils import *

# this script work with blender 2.49

g_coloredtokens = ('tank','weatherhen','freighter','barge','crab','turret','heap','speedboat','walkerheap','crayfish','hovertank','tarask','destroyer')
g_noshadowtokens = ('turret')
g_colors = { 'brown':'metal', 'pink':'metal', 'blue':'metal', 'white':'metal', 'cyan':'metal', 'grey':'metal', 'olive':'metal', 'orange':'metal', 'purple':'metal', 'yellow':'metal', 'green':'metal', 'red':'metal', 'camouflage':'camouflage', 'lightning':'lightning', 'pantera':'pantera', 'zebra':'zebra' }
g_angles = ( 0, 60, 120, 180, 240, 300 )


g_strategySize = {'freighter':3.4, 'barge':3.2, 'weatherhen':2.2, 'tank':1.1, 'crab':1.1, 'heap':1.3, 'speedboat':1.1, 'ore1':1.2, 'ore3':1.2, 'ore2':1.2, 'ore5':1.2, 'walkerheap':1.3, 'crayfish':1.2,'hovertank':1.2,'tarask':1.4, 'destroyer':3.4, 'ore2generator':2.9, 'ore3generator':3.4 }
g_tacticSize = {'freighter':3.4, 'barge':3, 'weatherhen':2.4, 'tank':1.1, 'crab':1.2, 'turret':1.3, 'heap':1.5, 'speedboat':1.1, 'ore1':1.2, 'ore3':1.2, 'ore2':1.2, 'ore5':1.3, 'walkerheap':1.5, 'crayfish':1.3,'hovertank':1.2,'tarask':1.6,'sluice':1.1, 'destroyer':3.5, 'ore2generator':2.9, 'ore3generator':3.5 }


g_defaultTacticWidth = 76
g_defaultTacticHeight = 40
g_defaultTacticScale = 24
g_defaultStrategyWidth = 34
g_defaultStrategyHeight = 29
g_defaultStrategyScale = 24.5
g_cameraTactic = 'tactic'
g_cameraStrategy = 'strategy'

pi = 3.14


def rotate(p_angle):
	for obj in Blender.Scene.GetCurrent().objects:
		if obj.type == 'Armature' or obj.type == 'Lattice' or obj.type == 'Mball' or obj.type == 'Mesh' or obj.type == 'Surf':
			obj.setMatrix( obj.getMatrix() * RotationMatrix(p_angle, 4, 'z') )
	


def renderColor(p_context, p_texture, p_color, p_angle, p_path):
	if( g_name in g_coloredtokens ):
		footex = Texture.Get('color')             # get texture named 'color'
		footex.setType('Image')                 # make foo be an image texture
		img = Image.Load('textures/'+p_color+'.jpg')            # load an image
		footex.image = img                      # link the image to the texture
		footex = Texture.Get('Tex')             # get texture named 'Tex'
		footex.setType('Image')                 # make foo be an image texture
		img = Image.Load('textures/'+p_texture+'.jpg')            # load an image
		footex.image = img                      # link the image to the texture
	p_context.renderPath = p_path % (p_color, p_angle)
	rotate(p_angle)
	p_context.render()
	rotate(-1*p_angle)

	
def renderAll(p_context):
	for angle in g_angles:
		renderColor( p_context, 'metal', 'colorless', angle, '//render/%s/'+g_currentCamera+'/'+g_name+'%d-' )
			
	if( g_name in g_coloredtokens ):
		for color in g_colors:
			for angle in g_angles:
				renderColor( p_context, g_colors[color], color, angle, '//render/%s/'+g_currentCamera+'/'+g_name+'%d-' )


scn = Blender.Scene.GetCurrent()
context = scn.getRenderingContext()

#get the root directory that the current file is in
#we'll write the muray files there.  
path = Blender.Get('filename')
tokens = path.split('\\')
g_name = tokens.pop();
file = g_name.split('.')
g_name = file[0]
# this isn't working for me
#g_name = Blender.Get('filename').replace('.blend','')

context.extensions = True
context.imageType = Render.PNG
context.enableRGBAColor() 
context.enablePremultiply() 
context.enableOversampling(1) 
context.OSALevel = 8
context.enableRayTracing(1)
context.enableShadow(1)


# destroy all lamps
for obj in Blender.Scene.GetCurrent().objects:
	if obj.type == 'Lamp':
		scn.objects.unlink(obj)

context.enableRayTracing(1)
context.enableShadow(1)
# create new lamp
lamp = Lamp.New('Sun','sun')
lamp.energy = 2.5
lamp.R = 1
lamp.G = 0.9
lamp.B = 0.7
#lamp.setMode('Square', 'Shadow')
#lamp.setMode('RayShadow','NoSpecular')
#lamp.mode &= ~Lamp.Modes["RayShadow"] # Disable RayShadow.
#lamp.mode |= Lamp.Modes["Shadows"]    # Enable Shadowbuffer shadows
lamp.mode |= Lamp.Modes["RayShadow"]
lamp.mode |= Lamp.Modes["NoSpecular"]
objLamp = Object.New('Lamp')
objLamp.link(lamp)
scn.objects.link(objLamp)
# objLamp.LocX = 50
# objLamp.LocY = -30
# objLamp.LocZ = 24
objLamp.RotX = 54*pi/180  #62*pi/180
objLamp.RotY = 40*pi/180  #44*pi/180
objLamp.RotZ = 42*pi/180

# create new lamp
lamp = Lamp.New('Hemi','moon')
lamp.energy = 0.8
lamp.R = 0.6
lamp.G = 0.7
lamp.B = 1
lamp.mode |= Lamp.Modes["RayShadow"]
lamp.mode |= Lamp.Modes["NoSpecular"]
objLamp = Object.New('Lamp')
objLamp.link(lamp)
scn.objects.link(objLamp)
objLamp.RotX = 62*pi/180
objLamp.RotY = 44*pi/180
objLamp.RotZ = 222*pi/180

# create new lamp spot for shadow 
lamp = Lamp.New('Spot','shadow')
lamp.energy = 10
lamp.R = 1
lamp.G = 1
lamp.B = 1
lamp.mode |= Lamp.Modes["RayShadow"]
lamp.mode |= Lamp.Modes["OnlyShadow"]
objLamp = Object.New('Lamp')
objLamp.link(lamp)
scn.objects.link(objLamp)
objLamp.RotX = 0
objLamp.RotY = 0
objLamp.RotZ = 222*pi/180

if( g_name not in g_noshadowtokens ):
	#create ground material
	mat = Material.New('ground')          # create a new Material called 'newMat'
	#mat.rgbCol = [0.8, 0.2, 0.2]          # change its color
	#mat.setAlpha(0.2)                     # mat.alpha = 0.2 -- almost transparent
	#mat.emit = 0.7                        # equivalent to mat.setEmit(0.8)
	#mat.mode |= Material.Modes.ZTRANSP    # turn on Z-Buffer transparency
	#mat.setAdd(0.8)                       # make it glow
	mat.setMode('OnlyShadow')  

	# create ground to receive shadow
	plane = Mesh.Primitives.Plane(200.0)   # create a newplane of size 200
	ob = Object.New('Mesh','Ground')          # create a new mesh-type object
	ob.link(plane)                      # link mesh datablock with object
	scn.link(ob)                      # add object to the scene
	plane.materials += [mat]


# create new ortho camera data
cam = Camera.New('ortho')   
objCam = scn.objects.new(cam)   # add a new camera object from the data
scn.objects.camera = objCam


# rendu tactic
# ==========
g_currentCamera = "tactic"
objCam.LocX = 0
objCam.LocY = -23.5
objCam.LocZ = 18.5
objCam.RotX = 53.5*pi/180
objCam.RotY = 0
objCam.RotZ = 0
#cam.scale = 24

context.imageSizeX(g_defaultTacticWidth)
context.imageSizeY(g_defaultTacticHeight)
cam.scale = g_defaultTacticScale
if( g_name in g_tacticSize ):
	context.imageSizeX(g_defaultTacticWidth * g_tacticSize[g_name])
	context.imageSizeY(g_defaultTacticHeight * g_tacticSize[g_name])
	cam.scale = g_defaultTacticScale * g_tacticSize[g_name]

renderAll(context)


# rendu strategy
# ============
g_currentCamera = "strategy"
objCam.LocX = 0
objCam.LocY = 0
objCam.LocZ = 100
objCam.RotX = 0
objCam.RotY = 0
objCam.RotZ = 0

context.imageSizeX(g_defaultStrategyWidth)
context.imageSizeY(g_defaultStrategyHeight)
cam.scale = g_defaultStrategyScale
if( g_name in g_strategySize ):
#if( g_tacticSize[name] is not null ):
	context.imageSizeX(g_defaultStrategyWidth * g_strategySize[g_name])
	context.imageSizeY(g_defaultStrategyHeight * g_strategySize[g_name])
	cam.scale = g_defaultStrategyScale * g_strategySize[g_name]

renderAll(context)

