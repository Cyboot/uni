#include <vector>
#include <math.h>
#include "vector.h"

enum Integration {
  Euler,
  EulerCromer,
  Heun,
  Verlet
};

struct Particle {
  DclVector  position;
  DclVector  previousPosition;
  DclVector  velocity;
  DclVector  force;
  double  mass;

  Integration integration;

  // visualization properties
  double  r, g, b;
  double  radius;

  Particle() 
  {
    integration = Euler;


    r = b = 1.0;
    g = 0.0;
    radius = 0.1;
  }
};

extern std::vector<Particle> vectorParticles;