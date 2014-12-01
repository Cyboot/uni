#pragma warning(disable:4244)
#pragma warning(disable:4018)

#include "definitions.h"
#include "textoutput.h"
#include "particle.h"
#include "tetramesh.h"

DclVector getWorld();
void  loadTetraFile(Mesh& m, char *filename);
void  computeSpringSet(Mesh& m);


const int MAX_NUM_PARTICLES = 10000;

// Integration step h
const DclFloat  TIMESTEP = 0.002;

// Elasticity of collision with walls
const DclFloat  WORLD_ELASTICITY = 1.0;

const DclFloat  SPRING_CONSTANT = 1000.0;
const DclFloat  SPRING_DAMPING_CONSTANT = 1.0;

const Integration INTEGRATION = RungeKutta4;

//////////// GLOBALS /////////////////
std::vector<Mesh> vectorMeshes;

int frame = 0;

//////////////////////////////////////

void  visualizeParticles();

//------------------------------------------------------------------------------------------------------------------------------------

void  initializeTetraMesh(Mesh& m, DclVector translation)
{
  for (int p = 0; p < m.vectorParticles.size(); p ++)
  {
    Particle& particle = m.vectorParticles[p];
    particle.position += translation;
    particle.force.clear();
    particle.velocity.clear();
    particle.mass = 1.0;
    particle.r = m.r; particle.g = m.g; particle.b = m.b;
  }
  for (int s = 0; s < m.vectorSprings.size(); s ++)
  {
    Spring& spring = m.vectorSprings[s];
    spring.initialLength = (m.vectorParticles[spring.p1].position - m.vectorParticles[spring.p2].position).length();
  }
}


//------------------------------------------------------------------------------------------------------------------------------------

// initialize the particle properties
void  initializeSimulation(int argc, char ** argv)
{
  char  *param;
  char  defaultParam[] = "cube.dcstructure";

  if (argc > 1) 
    param = argv[1];
  else
    param = defaultParam;

  vectorMeshes.push_back(Mesh());
  vectorMeshes.push_back(Mesh());
  vectorMeshes.push_back(Mesh());
  vectorMeshes.push_back(Mesh());

  Mesh& m1 = vectorMeshes[0];
  m1.integration = Euler;
  loadTetraFile(m1, param);
  computeSpringSet(m1);
  initializeTetraMesh(m1, DclVector(-3, 0, 0));

  Mesh& m2 = vectorMeshes[1];
  m2.integration = EulerCromer;
  loadTetraFile(m2, param);
  computeSpringSet(m2);
  initializeTetraMesh(m2, DclVector(-1, 0, 0));

  Mesh& m3 = vectorMeshes[2];
  m3.integration = Verlet;
  loadTetraFile(m3, param);
  computeSpringSet(m3);
  initializeTetraMesh(m3, DclVector(1, 0, 0));

  Mesh& m4 = vectorMeshes[3];
  m4.integration = RungeKutta4;
  loadTetraFile(m4, param);
  computeSpringSet(m4);
  initializeTetraMesh(m4, DclVector(3, 0, 0));
}

//------------------------------------------------------------------------------------------------------------------------------------

// add external forces to the sum of forces acting on the particle
void  addForces(Particle& particle)
{
  particle.force += DclVector(0, 1.0, 0) * particle.mass * -9.81; // gravitational force
}

//------------------------------------------------------------------------------------------------------------------------------------

void  addSpringForces(Mesh& m, Spring& spring)
{
	Particle& particle1 = m.vectorParticles[spring.p1];
	Particle& particle2 = m.vectorParticles[spring.p2];

	DclVector direction = particle1.position - particle2.position;
	double lengthDiff = direction.length() - spring.initialLength;
	direction.normalize();

	particle1.force -= direction * lengthDiff * SPRING_CONSTANT;
	particle2.force += direction * lengthDiff * SPRING_CONSTANT;

  // TODO: Compute spring force
  //particle1.force -= ...
  //particle2.force += ...

  // TODO: Compute damping force
  //particle1.force -= ...
  //particle2.force += ...
}

void  addTetraForces(Mesh& m, Tetrahedron& tetrahedron)
{
	// TODO: add forces that preserve the volume of a tetrahedron
}

//------------------------------------------------------------------------------------------------------------------------------------

// perform one time-integration step
void  integrate(Integration integration, Particle& particle)
{
  switch (integration)
  {
    case Euler:
      particle.position = particle.position + particle.velocity * TIMESTEP;
      particle.velocity = particle.velocity + particle.force / particle.mass * TIMESTEP;
      break;

    case EulerCromer:
      particle.velocity = particle.velocity + particle.force / particle.mass * TIMESTEP;
      particle.position = particle.position + particle.velocity * TIMESTEP;
      break;
    
    case Verlet:
      {
      // TODO: Implement Verlet-integration
      break;
      }

    default:
      break;
  }
}

void  integrationRungeKutta(Mesh& m)
{
  // TODO: Implement fourth-order Runge Kutta
  // use the member variables k1..k4 and l1..l4 defined in the Particle-struct
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
void  computeEnergies(Mesh& m, int i)
{
  DclFloat  e_kin = 0, e_pot = 0, e_def = 0;
  DclVector world = getWorld();

  for (int part = 0; part < m.vectorParticles.size(); part ++)
  {
    Particle& particle = m.vectorParticles[part];

    e_kin += particle.velocity.length2() * 0.5 * particle.mass;
    e_pot += (particle.position.y + world.z * 0.5) * particle.mass * 9.81;
  }

  // TODO: Compute deformation energy

  Textout::printvar(6 + i, 0, "Ekin %f, Epot %f, Edef %f, Sum %f", e_kin, e_pot, e_def, e_kin + e_pot + e_def);
}

//------------------------------------------------------------------------------------------------------------------------------------

// perform one simulation step
void  simulationStep()
{
  static int splitCounter = 0;

  int numParticles = 0;

  for (int mesh = 0; mesh < vectorMeshes.size(); mesh ++)
  {
    Mesh& m = vectorMeshes[mesh];

    // reset the vector of external forces
    for (int part = 0; part < m.vectorParticles.size(); part ++)
    {
      m.vectorParticles[part].force.clear();
      numParticles ++;
    }

    // compute external forces on the particles
    for (int part = 0; part < m.vectorParticles.size(); part ++)
    {
      addForces(m.vectorParticles[part]);
    }

    for (int spring = 0; spring < m.vectorSprings.size(); spring ++)
    {
      addSpringForces(m, m.vectorSprings[spring]);
    }

    for (int tetra = 0; tetra < m.vectorTetrahedra.size(); tetra ++)
    {
      addTetraForces(m, m.vectorTetrahedra[tetra]);
    }

    // compute collision impulses
    for (int part = 0; part < m.vectorParticles.size(); part ++)
    {
      worldCollision(m.vectorParticles[part]);
    }

    if (m.integration == RungeKutta4)
    {
      integrationRungeKutta(m);
    }
    else
    {
      // perform one integration step
      for (int part = 0; part < m.vectorParticles.size(); part ++)
      {
        integrate(m.integration, m.vectorParticles[part]);
      }
    }

    // compute and output energies
    computeEnergies(m, mesh);

    // draw particles
    visualizeParticles();
  }

}


