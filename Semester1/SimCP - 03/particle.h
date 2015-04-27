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

  DclVector positionOld;
  DclVector velocityOld;
  DclVector k1, k2, k3, k4;
  DclVector l1, l2, l3, l4;

  // visualization properties
  double  r, g, b;
  double  radius;

  Particle() 
  {
    r = b = 1.0;
    g = 0.0;
    radius = 0.1;
  }
};

extern const int MAX_NUM_PARTICLES;