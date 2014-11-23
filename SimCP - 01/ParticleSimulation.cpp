#pragma warning(disable:4244)
#pragma warning(disable:4018)

#include "definitions.h"
#include "textoutput.h"
#include "precisetimer.h"
#include "particle.h"

DclVector getWorld();



// Integration step h
const DclFloat  TIMESTEP = 0.005;

// Elasticity of collision with walls
const DclFloat  WORLD_ELASTICITY = 1.0;



//////////// GLOBALS /////////////////
std::vector<Particle> vectorParticles;
int frame = 0;

//////////////////////////////////////

void  visualizeParticles();
void  addForces(Particle& particle);

//------------------------------------------------------------------------------------------------------------------------------------

// initialize the particle properties
void  initializeSimulation()
{
	DclVector world = getWorld();

	srand(0);


	int MAX_PARTICLE = 1;
	for (int i = 0; i < MAX_PARTICLE; i++)
	{

		srand(0);

		DclFloat x = rand() * 0.8 * world.x / RAND_MAX - world.x / 2.5;
		DclFloat y = rand() * 0.8 * world.y / RAND_MAX - world.y / 2.5;
		DclFloat z = rand() * 0.8 * world.z / RAND_MAX - world.z / 2.5;

		DclFloat vx = rand() * 10.0 / RAND_MAX - 5.0;
		DclFloat vy = rand() * 10.0 / RAND_MAX - 5.0;
		DclFloat vz = rand() * 10.0 / RAND_MAX - 5.0;

		for (int j = 0; j < 4; j++)
		{
			Particle  particle;
			particle.mass = 1.0;
			particle.position.x = x;
			particle.position.y = y;
			particle.position.z = z;

			particle.velocity.x = vx;
			particle.velocity.y = vy;
			particle.velocity.z = vz;


			if (j == 0) {
				particle.integration = Euler;       
				particle.r = 1.0; 
				particle.g = 0.0; 
				particle.b = 0.0;
			}
			if (j == 1) {
				particle.integration = EulerCromer;
				particle.r = 0.0;
				particle.g = 1.0;
				particle.b = 0.0;
				//particle.position.z += 0.1;
			}
			if (j == 2) {
				particle.integration = Verlet;
				particle.r = 0.0;
				particle.g = 0.0;
				particle.b = 1.0;
				//particle.position.z += 0.2;
			}
			if (j == 3) {
				particle.integration = Heun;
				particle.r = 1.0;
				particle.g = 1.0;
				particle.b = 0.0;
				//particle.position.z -= 0.1;
			}
			particle.previousPosition = particle.position - particle.velocity * TIMESTEP;

			particle.force.clear();

			// put the particle into the vector
			vectorParticles.push_back(particle);

		}
	}
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
	DclVector pt, vt, v1, v2, a1, a2;
	switch (particle.integration)
	{
	case Euler:
		particle.position += particle.velocity * TIMESTEP;
		particle.velocity += particle.force / particle.mass * TIMESTEP;
		break;

	case EulerCromer:
		particle.velocity += particle.force / particle.mass * TIMESTEP;
		particle.position += particle.velocity * TIMESTEP;
		break;

	case Verlet:
		pt = particle.position;
		particle.position = particle.position*2.0 - particle.previousPosition + particle.force * TIMESTEP * TIMESTEP / particle.mass;
		particle.previousPosition = pt;
		particle.velocity = (particle.position - particle.previousPosition) / TIMESTEP;
		break;

	case Heun:

		v1 = particle.velocity;                    // velocity at t
		a1 = particle.force / particle.mass;       // acceleration at t
		v2 = particle.velocity + a1 * TIMESTEP;    // predict velocity at t+h

		// The following implementation considers that addForces()
		// generally depends on particle.position and particle.velocity .
		// Note that Heun computes forces at t, but also at t+h with predicted position and velocity.
		// Although this is not the case in our simple example, an implementation
		// should be prepared for this case.

		pt = particle.position;                    // save position at t
		vt = particle.velocity;                    // save position at t
		particle.position += v1 * TIMESTEP;        // predict position at t+h 
		particle.velocity = v2;                    // predicted velocity at t+h
		particle.force.clear();                    // predict forces at t+h ...
		addForces(particle);                       // ... using predicted position and velocity at t+h 
		a2 = particle.force / particle.mass;       // predict acceleration at t+h
		particle.position = pt;                    // restore current position
		particle.velocity = vt;                    // restore current velocity

		particle.position += (v1 + v2)*0.5*TIMESTEP; // compute position at t+h using velocities at t and t+h
		particle.velocity += (a1 + a2)*0.5*TIMESTEP; // compute velocity at t+h using accelerations at t and t+h

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
		particle.previousPosition = particle.position - particle.velocity * TIMESTEP;
	}

	if (particle.position.y < -world.y || particle.position.y > world.y)
	{
		particle.velocity.y *= -WORLD_ELASTICITY;
		particle.position.y = SIGN(particle.position.y) * world.y;
		particle.previousPosition = particle.position - particle.velocity * TIMESTEP;
	}

	if (particle.position.z < -world.z || particle.position.z > world.z)
	{
		particle.velocity.z *= -WORLD_ELASTICITY;
		particle.position.z = SIGN(particle.position.z) * world.z;
		particle.previousPosition = particle.position - particle.velocity * TIMESTEP;
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

	Textout::printvar(6, 0, "Kinetic energy %f, Potential energy %f, Sum %f", e_kin, e_pot, e_kin + e_pot);
}

//------------------------------------------------------------------------------------------------------------------------------------

// perform one simulation step
void  simulationStep()
{
	// reset the vector of external forces
	for (int part = 0; part < vectorParticles.size(); part++)
	{
		vectorParticles[part].force.clear();
	}

	// compute external forces on the particles
	for (int part = 0; part < vectorParticles.size(); part++)
	{
		addForces(vectorParticles[part]);
	}

	// perform one integration step
	for (int part = 0; part < vectorParticles.size(); part++)
	{
		integrate(vectorParticles[part]);
	}

	// compute collision impulses
	for (int part = 0; part < vectorParticles.size(); part++)
	{
		worldCollision(vectorParticles[part]);
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
}
