struct Tetrahedron {
  int p1, p2, p3, p4;
};

struct Spring {
  int p1, p2;

  DclFloat  initialLength;
};

struct Mesh {
  Integration integration;
  DclFloat  r, g, b;

  std::vector<Tetrahedron> vectorTetrahedra;
  std::vector<Spring> vectorSprings;
  std::vector<Particle> vectorParticles;
};

extern std::vector<Mesh> vectorMeshes;