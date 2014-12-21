#pragma warning(disable:4244)
#pragma warning(disable:4018)

#include <list>
#include "definitions.h"
#include "textoutput.h"
#include "precisetimer.h"
#include "particle.h"




DclVector getWorld();

const int MAX_NUM_PARTICLES = 10000;
const int NUM_PARTICLES = 1000;

// Integration step h
const DclFloat  TIMESTEP = 0.01;

// Elasticity of collision with walls
const DclFloat  WORLD_ELASTICITY = 0.6;

const DclFloat  PARTICLE_FORCE_CONSTANT = 1000.0;

const DclFloat  INITIAL_VELOCITY = 10;

const DclFloat  CELL_SIZE = 2.6;

//////////// GLOBALS /////////////////
std::vector<Particle> vectorParticles;
int frame = 0;
std::vector<std::list<Particle*> > vectorSpatialGrid;
int numCellsX, numCellsY, numCellsZ;

//////////////////////////////////////

void  visualizeParticles();

//------------------------------------------------------------------------------------------------------------------------------------


int getCellID(int i, int j, int k)
{
  int id = k * numCellsX * numCellsY + j * numCellsX + i;
  return id;
}

//------------------------------------------------------------------------------------------------------------------------------------

void  clearSpatialGrid()
{
  for (int i = 0; i < vectorSpatialGrid.size(); i ++)
  {
    if (vectorSpatialGrid[i].size() > 0)
      vectorSpatialGrid[i].clear();
  }
}

//------------------------------------------------------------------------------------------------------------------------------------

void  getCell(Particle& particle, int& cell_i, int& cell_j, int& cell_k)
{
  DclVector world = getWorld();
  cell_i = floor(particle.position.x + world.x / CELL_SIZE);
  cell_j = floor(particle.position.y + world.y / CELL_SIZE);
  cell_k = floor(particle.position.z + world.z / CELL_SIZE);

  if(cell_i < 0) cell_i = 0; if(cell_i >= numCellsX) cell_i = numCellsX - 1;
  if(cell_j < 0) cell_j = 0; if(cell_j >= numCellsY) cell_j = numCellsY - 1;
  if(cell_k < 0) cell_k = 0; if(cell_k >= numCellsZ) cell_k = numCellsZ - 1;
}

//------------------------------------------------------------------------------------------------------------------------------------

void  insertParticle(Particle& particle)
{
  int cell_i, cell_j, cell_k;

  getCell(particle, cell_i, cell_j, cell_k);
  int cellID = getCellID(cell_i, cell_j, cell_k);

  vectorSpatialGrid[cellID].push_back(&particle);
}

//------------------------------------------------------------------------------------------------------------------------------------

// initialize the particle properties
void  initializeSimulation()
{
  DclVector&  world = getWorld();
  numCellsX = floor(2 * world.x / CELL_SIZE) + 1;
  numCellsY = floor(2 * world.y / CELL_SIZE) + 1;
  numCellsZ = floor(2 * world.z / CELL_SIZE) + 1;

  vectorSpatialGrid.reserve(numCellsX * numCellsY * numCellsZ);
  vectorSpatialGrid.resize(numCellsX * numCellsY * numCellsZ);

  vectorParticles.reserve(MAX_NUM_PARTICLES);
}

void  createParticle()
{
  Particle  particle;
  particle.position.set((rand() - RAND_MAX / 2) * 2.0 / RAND_MAX, getWorld().y, (rand() - RAND_MAX / 2) * 2.0 / RAND_MAX);
  particle.force.clear();

  particle.velocity.set(0, -20, 0);

  particle.mass = 1.0;
  particle.integration = EulerCromer;
  particle.radius = 0.15;
  particle.id = vectorParticles.size();
  
  vectorParticles.push_back(particle);
}

//------------------------------------------------------------------------------------------------------------------------------------

// add external forces to the sum of forces acting on the particle
void  addForces(Particle& particle)
{
  particle.force += DclVector(0, 1.0, 0) * particle.mass * -9.81; // gravitational force
}

void  addSingleParticleForce(Particle& part1, Particle& part2)
{
  DclVector distance = part2.position - part1.position;
  DclVector direction = distance; 
  DclFloat  magnitude = direction.normalize();
  DclFloat  radius = (part1.radius + part2.radius);

  if (magnitude < radius)
  {
    part2.force += direction * (radius - magnitude) * PARTICLE_FORCE_CONSTANT;
    part1.force -= direction * (radius - magnitude) * PARTICLE_FORCE_CONSTANT;
  }
}

void  addParticleForces(Particle& particle)
{
  DclVector world = getWorld();

  int cell_i, cell_j, cell_k;
  getCell(particle, cell_i, cell_j, cell_k);

  for (int i = MAX(cell_i - 1, 0); i < MIN(cell_i + 1, numCellsX); i ++)
  {
    for (int j = MAX(cell_j - 1, 0); j < MIN(cell_j + 1, numCellsY); j ++)
    {
      for (int k = MAX(cell_k - 1, 0); k < MIN(cell_k + 1, numCellsZ); k ++)
      {
        int id = getCellID(i, j, k);

        std::list<Particle*>& cell = vectorSpatialGrid[id];

        for (std::list<Particle*>::iterator particleIter = cell.begin(); particleIter != cell.end(); ++ particleIter)
        {
          if (*particleIter != &particle && particle.id < (*particleIter)->id)
          {
            addSingleParticleForce(**particleIter, particle);
          }
        }
      }
    }
  }

}

//------------------------------------------------------------------------------------------------------------------------------------

// perform one time-integration step
void  integrate(Particle& particle)
{
  switch (particle.integration)
  {
    case Euler:
      particle.position = particle.position + particle.velocity * TIMESTEP;
      particle.velocity = particle.velocity + particle.force / particle.mass * TIMESTEP;
      break;

    case EulerCromer:
      particle.velocity = particle.velocity + particle.force / particle.mass * TIMESTEP;
      particle.position = particle.position + particle.velocity * TIMESTEP;
      break;

    default:
      break;
  }
}

//------------------------------------------------------------------------------------------------------------------------------------

// collision handling with the world
void  worldCollision(Particle& particle)
{
  DclVector world = getWorld();

  world *= 0.5;

  if (particle.position.x < -world.x || particle.position.x > world.x)
  {
    particle.velocity.x *= -WORLD_ELASTICITY;
    particle.position.x = SIGN(particle.position.x) * world.x;
  }

  if (particle.position.y < -world.y || particle.position.y > world.y)
  {
    particle.velocity.y *= -WORLD_ELASTICITY;
    particle.position.y = SIGN(particle.position.y) * world.y;
  }

  if (particle.position.z < -world.z || particle.position.z > world.z)
  {
    particle.velocity.z *= -WORLD_ELASTICITY;
    particle.position.z = SIGN(particle.position.z) * world.z;
  }
}

//------------------------------------------------------------------------------------------------------------------------------------

// compute and output the total energies in the scene
void  computeEnergies()
{
  DclFloat  e_kin = 0, e_pot = 0;
  DclVector world = getWorld();

  for (int part = 0; part < vectorParticles.size(); part ++)
  {
    Particle& particle = vectorParticles[part];

    e_kin += particle.velocity.length2() * 0.5 * particle.mass;
    e_pot += (particle.position.y + world.z * 0.5) * particle.mass * 9.81;
  }

  Textout::printvar(6, 0, "Kinetic energy %f, Potential energy %f, Sum %f", e_kin, e_pot, e_kin + e_pot);
}

//------------------------------------------------------------------------------------------------------------------------------------

// perform one simulation step
void  simulationStep()
{
  static int splitCounter = 0;

  if (frame % 2 == 0 && vectorParticles.size() < NUM_PARTICLES)
    createParticle();


  // reset the vector of external forces
  for (int part = 0; part < vectorParticles.size(); part ++)
  {
    vectorParticles[part].force.clear();
  }

  clearSpatialGrid();

  // compute external forces on the particles
  for (int part = 0; part < vectorParticles.size(); part ++)
  {
    insertParticle(vectorParticles[part]);
  }

  // compute external forces on the particles
  for (int part = 0; part < vectorParticles.size(); part ++)
  {
    addForces(vectorParticles[part]);
  }

  // compute external forces on the particles
  for (int part = 0; part < vectorParticles.size(); part ++)
  {
    addParticleForces(vectorParticles[part]);
  }

  // compute collision impulses
  for (int part = 0; part < vectorParticles.size(); part ++)
  {
    worldCollision(vectorParticles[part]);
  }

  // perform one integration step
  for (int part = 0; part < vectorParticles.size(); part ++)
  {
    integrate(vectorParticles[part]);
  }

  // compute and output energies
  computeEnergies();

  // draw particles
  visualizeParticles();

  // output statistics
  Textout::printvar(5, 0, "Frame %d, Num. particles %d", frame, vectorParticles.size());

  int maxListLength = 0;
  for (int i = 0; i < vectorSpatialGrid.size(); i ++)
  {
    if (vectorSpatialGrid[i].size() > maxListLength)
      maxListLength = vectorSpatialGrid[i].size();
  }
  Textout::printvar(7, 0, "Max list length %d", maxListLength);

  frame ++;
}


