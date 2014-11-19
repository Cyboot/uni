#pragma warning(disable:4244)
#pragma warning(disable:4018)

#include <Inventor/SbBasic.h>
#include <Inventor/actions/SoGLRenderAction.h>
#include <Inventor/sensors/SoIdleSensor.h>
#include <Inventor/nodes/SoNodes.h>


#include <Inventor/Win/SoWin.h>
#include <Inventor/Win/viewers/SoWinExaminerViewer.h>

#include "particle.h"
#include "textoutput.h"

/////////////////////////////////////////////////
// Viewer constants
/////////////////////////////////////////////////

const DclFloat  WORLD_WIDTH = 10.0;
const DclFloat  WORLD_DEPTH = 10.0;
const DclFloat  WORLD_HEIGHT = 10.0;

const DclFloat  WORLD_RED = 1.0;
const DclFloat  WORLD_GREEN = 0.9;
const DclFloat  WORLD_BLUE = 0.9;

const DclFloat LIGHT_X = 1.0;
const DclFloat LIGHT_Y = 2.0;
const DclFloat LIGHT_Z = 1.0;

const DclFloat CAMERA_X = 0.0;
const DclFloat CAMERA_Y = 0.0;
const DclFloat CAMERA_Z = 14.0; 

const int WINDOW_WIDTH = 1024;
const int WINDOW_HEIGHT = 768;

const int MAX_NUM_PARTICLES = 10000;

////////////////////////////////////////////////////


SoSeparator * root;
SoMaterial  *particleMaterials;
SoCoordinate3 * particleCoordinates;
int * particleFaceIndices;
SoIndexedFaceSet * particleFaces;
SoMaterial *particleMaterial;
bool  running = false;

void  initializeSimulation();
void  simulationStep();

///////////////////////////////////////////////////



const int NUM_FACE_INDICES_PER_PARTICLE = 4 * 8;
const int NUM_FACES_PER_PARTICLE = 8;
const int NUM_VERTICES_PER_PARTICLE = 6;

///////////////////////////////////////////////////

// returns the extent of the world
DclVector getWorld()
{
  return DclVector(WORLD_WIDTH, WORLD_HEIGHT, WORLD_DEPTH);
}

//------------------------------------------------------------------------------------------------------------------------------------

// construct the world cube
SoSeparator *  constructWorld(int w, int h, int d)
{
  SbVec3f * vertices;
  SbVec3f * normals;
  int32_t * numvertices;

  normals = new SbVec3f[6];
  vertices = new SbVec3f[24];
  numvertices = new int32_t[6];

  normals[0].setValue(  0.0,  0.0,  1.0);
  normals[1].setValue(  0.0,  -1.0,  0.0);
  normals[2].setValue(  -1.0,  0.0, 0.0);
  normals[3].setValue(  1.0,  0.0, 0.0);
  normals[4].setValue(  0.0,  1.0, 0.0);
  normals[5].setValue(  0.0,  0.0, -1.0);

  // back wall
  vertices[3].setValue( -w/2.0,  h/2.0,  -d/2.0);
  vertices[2].setValue(  w/2.0,  h/2.0,  -d/2.0);
  vertices[1].setValue ( w/2.0, -h/2.0,  -d/2.0);
  vertices[0].setValue( -w/2.0, -h/2.0,  -d/2.0);

  // top wall
  vertices[4].setValue( -w/2.0,  h/2.0,  -d/2.0);
  vertices[5].setValue(  w/2.0,  h/2.0,  -d/2.0);
  vertices[6].setValue ( w/2.0,  h/2.0,  d/2.0);
  vertices[7].setValue( -w/2.0,  h/2.0,  d/2.0);


  // right wall
  vertices[8].setValue(  w/2.0,  h/2.0,  -d/2.0);
  vertices[9].setValue ( w/2.0, -h/2.0,  -d/2.0);
  vertices[10].setValue ( w/2.0, -h/2.0,  d/2.0);
  vertices[11].setValue(  w/2.0,  h/2.0,  d/2.0);

  // left wall
  vertices[15].setValue(  -w/2.0,  h/2.0,  -d/2.0);
  vertices[14].setValue ( -w/2.0, -h/2.0,  -d/2.0);
  vertices[13].setValue ( -w/2.0, -h/2.0,  d/2.0);
  vertices[12].setValue(  -w/2.0,  h/2.0,  d/2.0);

  // bottom wall
  vertices[19].setValue( -w/2.0,  -h/2.0,  -d/2.0);
  vertices[18].setValue(  w/2.0,  -h/2.0,  -d/2.0);
  vertices[17].setValue ( w/2.0,  -h/2.0,  d/2.0);
  vertices[16].setValue( -w/2.0,  -h/2.0,  d/2.0);

  // back wall
  vertices[20].setValue( -w/2.0,  h/2.0,  d/2.0);
  vertices[21].setValue(  w/2.0,  h/2.0,  d/2.0);
  vertices[22].setValue ( w/2.0, -h/2.0,  d/2.0);
  vertices[23].setValue( -w/2.0, -h/2.0,  d/2.0);

  int n;
  for (n=0; n<6; n++) {
    numvertices[n] = 4;
  }

  SoSeparator * sep = new SoSeparator;

  SoMaterial *mat = new SoMaterial;
  mat->diffuseColor.setValue(SbColor(WORLD_RED, WORLD_GREEN, WORLD_BLUE));
  sep->addChild(mat);
  
  // define normals (this is optional)
  SoNormal * norm = new SoNormal;
  norm->vector.setValues(0, 6, normals);
  sep->addChild(norm);

  // bind one normal per face
  SoNormalBinding * normb = new SoNormalBinding;
  normb->value = SoNormalBinding::PER_FACE;
  sep->addChild(normb);
  
  // define coords
  SoCoordinate3 * coord = new SoCoordinate3;
  coord->point.setValues(0, 24, vertices);
  sep->addChild(coord);
  
  // define face set
  SoFaceSet * fs = new SoFaceSet;
  fs->numVertices.setValues(0, 6, numvertices);
  sep->addChild(fs);


  return sep;
}

//------------------------------------------------------------------------------------------------------------------------------------

// construct the particle octahedrons
void  computeStaticParticleData(SbVec3f *particleNormalBuffer, int *particleFaceIndices)
{
  for (int i = 0; i < MAX_NUM_PARTICLES; i ++)
  {
    SbVec3f * currentParticle = particleNormalBuffer + NUM_FACES_PER_PARTICLE * i;

    currentParticle[0].setValue(0, -sqrt(2.0) / 2.0, -sqrt(2.0) / 2.0);
    currentParticle[1].setValue(sqrt(2.0) / 2.0, -sqrt(2.0) / 2.0, 0);
    currentParticle[2].setValue(0, -sqrt(2.0) / 2.0, sqrt(2.0) / 2.0);
    currentParticle[3].setValue(-sqrt(2.0) / 2.0, -sqrt(2.0) / 2.0, 0);

    currentParticle[4].setValue(0, sqrt(2.0) / 2.0, -sqrt(2.0) / 2.0);
    currentParticle[5].setValue(sqrt(2.0) / 2.0, sqrt(2.0) / 2.0, 0);
    currentParticle[6].setValue(0, sqrt(2.0) / 2.0, sqrt(2.0) / 2.0);
    currentParticle[7].setValue(-sqrt(2.0) / 2.0, sqrt(2.0) / 2.0, 0);

    int *faceStart = particleFaceIndices + NUM_FACE_INDICES_PER_PARTICLE * i;

    faceStart[0] = NUM_VERTICES_PER_PARTICLE * i + 0;
    faceStart[1] = NUM_VERTICES_PER_PARTICLE * i + 2;
    faceStart[2] = NUM_VERTICES_PER_PARTICLE * i + 1;
    faceStart[3] = SO_END_FACE_INDEX;
    faceStart += 4;

    faceStart[0] = NUM_VERTICES_PER_PARTICLE * i + 0;
    faceStart[1] = NUM_VERTICES_PER_PARTICLE * i + 3;
    faceStart[2] = NUM_VERTICES_PER_PARTICLE * i + 2;
    faceStart[3] = SO_END_FACE_INDEX;
    faceStart += 4;

    faceStart[0] = NUM_VERTICES_PER_PARTICLE * i + 0;
    faceStart[1] = NUM_VERTICES_PER_PARTICLE * i + 4;
    faceStart[2] = NUM_VERTICES_PER_PARTICLE * i + 3;
    faceStart[3] = SO_END_FACE_INDEX;
    faceStart += 4;

    faceStart[0] = NUM_VERTICES_PER_PARTICLE * i + 0;
    faceStart[1] = NUM_VERTICES_PER_PARTICLE * i + 1;
    faceStart[2] = NUM_VERTICES_PER_PARTICLE * i + 4;
    faceStart[3] = SO_END_FACE_INDEX;
    faceStart += 4;

    faceStart[0] = NUM_VERTICES_PER_PARTICLE * i + 1;
    faceStart[1] = NUM_VERTICES_PER_PARTICLE * i + 2;
    faceStart[2] = NUM_VERTICES_PER_PARTICLE * i + 5;
    faceStart[3] = SO_END_FACE_INDEX;
    faceStart += 4;

    faceStart[0] = NUM_VERTICES_PER_PARTICLE * i + 2;
    faceStart[1] = NUM_VERTICES_PER_PARTICLE * i + 3;
    faceStart[2] = NUM_VERTICES_PER_PARTICLE * i + 5;
    faceStart[3] = SO_END_FACE_INDEX;
    faceStart += 4;

    faceStart[0] = NUM_VERTICES_PER_PARTICLE * i + 3;
    faceStart[1] = NUM_VERTICES_PER_PARTICLE * i + 4;
    faceStart[2] = NUM_VERTICES_PER_PARTICLE * i + 5;
    faceStart[3] = SO_END_FACE_INDEX;
    faceStart += 4;

    faceStart[0] = NUM_VERTICES_PER_PARTICLE * i + 4;
    faceStart[1] = NUM_VERTICES_PER_PARTICLE * i + 1;
    faceStart[2] = NUM_VERTICES_PER_PARTICLE * i + 5;
    faceStart[3] = SO_END_FACE_INDEX;
    faceStart += 4;
  }
}

//------------------------------------------------------------------------------------------------------------------------------------

// update the positions of the particle-octahedrons
void  updateParticle(int i, double x, double y, double z, SbVec3f *coords, double radius)
{
  double  scale = radius;

  coords += i * NUM_VERTICES_PER_PARTICLE;

  coords[0].setValue(x + 0, y - 0.7 * scale, z + 0);
  coords[1].setValue(x - 0.5 * scale, y, z - 0.5 * scale);
  coords[2].setValue(x + 0.5 * scale, y, z - 0.5 * scale);
  coords[3].setValue(x + 0.5 * scale, y, z + 0.5 * scale);
  coords[4].setValue(x - 0.5 * scale, y, z + 0.5 * scale);
  coords[5].setValue(x + 0, y + 0.7 * scale, z + 0);
}


//------------------------------------------------------------------------------------------------------------------------------------

// update the positions and materials of the particles. Called in every simulation step
void  visualizeParticles()
{
  SbVec3f *coords  = particleCoordinates->point.startEditing();
  SbColor * color = particleMaterials->diffuseColor.startEditing();

  for (int i = 0; i < vectorParticles.size(); i ++)
  {
    updateParticle(i, vectorParticles[i].position.x, vectorParticles[i].position.y, vectorParticles[i].position.z, coords, vectorParticles[i].radius);
    for (int j = NUM_FACES_PER_PARTICLE * i; j < NUM_FACES_PER_PARTICLE * i + NUM_FACES_PER_PARTICLE; j ++)
    {
      color[j].setValue(vectorParticles[i].r, vectorParticles[i].g, vectorParticles[i].b);
    }
  }

  particleFaces->coordIndex.setValues(0, NUM_FACE_INDICES_PER_PARTICLE * (int)vectorParticles.size(), particleFaceIndices);

  particleMaterials->diffuseColor.finishEditing();
  particleCoordinates->point.finishEditing();
}

//------------------------------------------------------------------------------------------------------------------------------------

// create a pointlight source
SoGroup *pointlight(SbVec3f pos, SbVec3f col)
{
  SoGroup * group = new SoGroup;
  SoSeparator * lsep = new SoSeparator;

  SoLightModel * lm = new SoLightModel;
  SoPointLight * light = new SoPointLight;

  lm->model = SoLightModel::BASE_COLOR;

  light->location.setValue(pos);
  light->color.setValue(col);
  lsep->addChild(lm);

  group->addChild(light);
  group->addChild(lsep);
  return group;
}


//------------------------------------------------------------------------------------------------------------------------------------

// Keyboard-event callback
void keyPressed(void * userdata, SoEventCallback * node)
{
  const SoEvent * event = node->getEvent();

  if (SO_KEY_PRESS_EVENT(event, ENTER)) 
  {
    running = !running;
  }
  if (SO_KEY_PRESS_EVENT(event, Q)) 
  {
    SoWin::exitMainLoop();
  }
}


//------------------------------------------------------------------------------------------------------------------------------------

// idle callback
void  idleCallback(void* userData, SoSensor * s)
{
  if (running)
  {
    simulationStep();
  }
  s->schedule();
}


//------------------------------------------------------------------------------------------------------------------------------------


int main(int argc, char ** argv )
{
  Textout::init();
  Textout::print(0, 0, "--------------------- ParticleViewer ---------------------");
  Textout::print(1, 0, "Simulation in der Computergraphik, University of Freiburg");
  Textout::print(2, 0, "Press <ENTER> to start or pause, Q to exit");
  Textout::print(3, 0, "----------------------------------------------------------");
  SetWindowPos(GetForegroundWindow(), HWND_TOP, 0, WINDOW_HEIGHT, 0, 0, SWP_SHOWWINDOW | SWP_NOSIZE);

  HWND window = SoWin::init( argv[0] );
  if ( ! window )
    exit( 1 );


  root = new SoSeparator;
  root->ref();

  initializeSimulation();


  SoEventCallback * cb = new SoEventCallback;
  cb->addEventCallback(SoKeyboardEvent::getClassTypeId(), keyPressed, NULL);
  root->insertChild(cb, 0);

  SoSeparator *particleGroup = new SoSeparator;

  SbVec3f * particleNormalBuffer = new SbVec3f [NUM_FACES_PER_PARTICLE * MAX_NUM_PARTICLES]; 
  particleFaceIndices = new int [NUM_FACE_INDICES_PER_PARTICLE * MAX_NUM_PARTICLES];
  SbColor * colors = new SbColor [NUM_FACES_PER_PARTICLE * MAX_NUM_PARTICLES];
  computeStaticParticleData(particleNormalBuffer, particleFaceIndices); 

  particleMaterials = new SoMaterial;
  particleMaterials->diffuseColor.setValues(0, NUM_FACES_PER_PARTICLE * MAX_NUM_PARTICLES, colors);
  particleGroup->addChild(particleMaterials);

  SoMaterialBinding * matbind = new SoMaterialBinding;
  matbind->value.setValue( SoMaterialBinding::PER_FACE);
  particleGroup->addChild( matbind );

  SoNormalBinding *normbind = new SoNormalBinding;
  normbind->value.setValue(SoNormalBinding::PER_FACE);
  particleGroup->addChild(normbind);
  
  SoShapeHints *shint = new SoShapeHints;
  shint->vertexOrdering.setValue(SoShapeHints::CLOCKWISE);
  shint->shapeType = SoShapeHints::SOLID;
  particleGroup->addChild(shint);  

  SoNormal * particleNormals = new SoNormal;
  particleNormals->vector.setValues(0,NUM_FACES_PER_PARTICLE * MAX_NUM_PARTICLES,particleNormalBuffer);
  particleGroup->addChild(particleNormals);  

  SbVec3f *particleVertexBuffer = new SbVec3f [NUM_VERTICES_PER_PARTICLE * MAX_NUM_PARTICLES];
  particleCoordinates = new SoCoordinate3;
  particleCoordinates->point.setValues(0, NUM_VERTICES_PER_PARTICLE * MAX_NUM_PARTICLES, particleVertexBuffer);
  particleGroup->addChild(particleCoordinates);


  particleFaces = new SoIndexedFaceSet;
  particleGroup->addChild(particleFaces);

  visualizeParticles();

  SoIdleSensor  *sensor = new SoIdleSensor(idleCallback, 0L);
  sensor->schedule();

  SoShapeHints * hints = new SoShapeHints;

  // Enable backface culling
  hints->vertexOrdering = SoShapeHints::COUNTERCLOCKWISE;
  hints->shapeType = SoShapeHints::SOLID;

  root->addChild(hints);
  root->addChild(pointlight(SbVec3f(LIGHT_X, LIGHT_Y, LIGHT_Z), SbVec3f(1.0, 1.0, 1.0)));
  root->addChild(particleGroup);


  root->addChild(constructWorld(WORLD_WIDTH, WORLD_HEIGHT, WORLD_DEPTH));


  SoWinExaminerViewer * viewer = new SoWinExaminerViewer( window );
  viewer->setTransparencyType( SoGLRenderAction::BLEND );
  viewer->setSceneGraph( root );
  viewer->setTitle( "ParticleViewer" );
  viewer->setHeadlight(FALSE);
  viewer->viewAll();
  viewer->setViewing(false);
  SoCamera* camera = viewer->getCamera();
  camera->position.setValue(CAMERA_X, CAMERA_Y, CAMERA_Z);  
  camera->pointAt(SbVec3f(0, 0, 0));
  viewer->show();
  SetWindowPos(window, HWND_TOP, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, SWP_SHOWWINDOW);


  SoWin::show(window); // display the main window


  SoWin::mainLoop();   // main Coin event loop

  root->unref();      // decrements the root's reference counter
  return 0;
}

