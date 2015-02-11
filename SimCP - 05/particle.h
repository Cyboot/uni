#include <vector>
#include <math.h>
#include "vector.h"

enum Integration {
  Euler,
  EulerCromer,
  RungeKutta4,
  Verlet
};

struct Particle {
  DclVector  position;
  DclVector  velocity;
  DclVector  force;
  double  mass;

  int liveCounter;

  Integration integration;

  // visualization properties
  double  r, g, b;
  double  radius;
  int id;

  Particle() 
  {
    integration = Euler;


    r = b = 1.0;
    g = 0.0;
    radius = 0.1;
  }
};

extern std::vector<Particle> vectorParticles;

extern const int MAX_NUM_PARTICLES;