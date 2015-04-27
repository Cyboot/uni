#pragma warning(disable:4244)
#pragma warning(disable:4018)

#include "definitions.h"
#include "textoutput.h"
#include "precisetimer.h"
#include "particle.h"

DclVector getWorld();

const int MAX_NUM_PARTICLES = 10000;

// Integration step h
const DclFloat  TIMESTEP = 0.005;

// Elasticity of collision with walls
const DclFloat  WORLD_ELASTICITY = 0.6;



//////////// GLOBALS /////////////////
std::vector<Particle> vectorParticles;
int frame = 0;

//////////////////////////////////////

void  visualizeParticles();

//------------------------------------------------------------------------------------------------------------------------------------


// initialize the particle properties
void  initializeSimulation()
{
	vectorParticles.clear();
	vectorParticles.reserve(MAX_NUM_PARTICLES);

	for (int i = 1; i < 2; i++)
	{
		Particle  particle(i * 10);
		particle.velocity = DclVector(0, rand() % 15 + 5, 0);
		particle.position = DclVector(0, -4.9, 0);
		vectorParticles.push_back(particle);
	}

	DclVector world = getWorld();
	// TODO: create an initial particle (the "rocket")
}

//------------------------------------------------------------------------------------------------------------------------------------

// add external forces to the sum of forces acting on the particle
void  addForces(Particle& particle)
{
	particle.force += DclVector(0, 1.0, 0) * particle.mass * -9.81; // gravitational force
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

	for (int part = 0; part < vectorParticles.size(); part++)
	{
		Particle& particle = vectorParticles[part];

		e_kin += particle.velocity.length2() * 0.5 * particle.mass;
		e_pot += (particle.position.y + world.z * 0.5) * particle.mass * 9.81;
	}

	Textout::printvar(7, 0, "Kinetic energy %f, Potential energy %f, Sum %f", e_kin, e_pot, e_kin + e_pot);
}

//------------------------------------------------------------------------------------------------------------------------------------

// perform one simulation step
void  simulationStep()
{
	static int splitCounter = 0;

	// reset the vector of external forces
	for (int part = 0; part < vectorParticles.size(); part++)
	{
		vectorParticles[part].force.clear();
	}


	// split particles...
	if (frame == 100){

		Particle rocket = vectorParticles[0];
		vectorParticles.clear();

		double vX = rocket.velocity.x;
		double vY = rocket.velocity.y;
		double vZ = rocket.velocity.z;

		Textout::printvar(9, 0, "Impulse before: (%f, %f, %f)", vX*rocket.mass, vY*rocket.mass, vZ*rocket.mass);

		double impulsX = 0;
		double impulsY = 0;
		double impulsZ = 0;

		const int NUM_PARTICLE = 50;
		const int EXPLOSION_FORCE = 8;
		for (int i = 0; i < NUM_PARTICLE - 1; i++)
		{
			Particle particle(rocket.mass / NUM_PARTICLE);
			particle.position = rocket.position;

			double dx = (((double)rand() / (double)RAND_MAX) - 0.5)*EXPLOSION_FORCE;
			double dy = (((double)rand() / (double)RAND_MAX) - 0.5)*EXPLOSION_FORCE;
			double dz = (((double)rand() / (double)RAND_MAX) - 0.5)*EXPLOSION_FORCE;

			particle.velocity = DclVector(vX + dx, vY + dy, vZ + dz);

			impulsX += dx * particle.mass;
			impulsY += dy* particle.mass;
			impulsZ += dz* particle.mass;

			vectorParticles.push_back(particle);
		}
		Textout::printvar(13, 0, "Remaining: (%f, %f, %f)", impulsX, impulsY, impulsZ);

		//eliminate remaining impulse
		Particle particle(rocket.mass / NUM_PARTICLE);
		particle.position = rocket.position;

		double dx = -impulsX / particle.mass;
		double dy = -impulsY / particle.mass;
		double dz = -impulsZ / particle.mass;

		particle.velocity = DclVector(vX + dx, vY + dy, vZ + dz);

		impulsX += dx * particle.mass;
		impulsY += dy* particle.mass;
		impulsZ += dz* particle.mass;

		vectorParticles.push_back(particle);


		Textout::printvar(10, 0, "Impulse after: (%f, %f, %f)", impulsX, impulsY, impulsZ);
	}

	// compute external forces on the particles
	for (int part = 0; part < vectorParticles.size(); part++)
	{
		addForces(vectorParticles[part]);
	}

	// compute collision impulses
	for (int part = 0; part < vectorParticles.size(); part++)
	{
		worldCollision(vectorParticles[part]);
	}

	// perform one integration step
	for (int part = 0; part < vectorParticles.size(); part++)
	{
		integrate(vectorParticles[part]);
	}

	// compute and output energies
	computeEnergies();

	// draw particles
	visualizeParticles();

	// measure time for frame
	double  dt = PreciseTimer::stop("SimulationStep");
	PreciseTimer::start("SimulationStep");

	// output statistics
	Textout::printvar(5, 0, "Frame %d, Frames/second %f, Num. particles %d", frame, 1000.0 / dt, vectorParticles.size());
	frame++;

	if (frame == 500){
		frame = 0;
		initializeSimulation();
	}

}
