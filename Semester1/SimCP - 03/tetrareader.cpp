#include "particle.h"
#include "tetramesh.h"
#include <stdio.h>
#include <set>
#include <utility>
#include <string>

// Loads the .dcstructure mesh from the file filename
void  loadTetraFile(Mesh& m, char *filename)
{
  FILE  *objFile = fopen(filename, "r");
  char line[160];
  char  tag[32], parameter[128];

  if (!objFile)
    return;

  while (fgets(line, 160, objFile))
  {
      // test for single comment lines
    if(line[0] == '/') continue;

    // set all characters in the parser strings to 0
    memset(parameter, 0, 128);
    memset(tag, 0, 32);
    sscanf(line, "%s %128c", tag, parameter); // Parse the line

    // Vertex
    if (std::string(tag) == "vertex")
    {
      Particle p;
      float x1, x2, x3;
      sscanf(parameter, "%f %f %f", &x1, &x2, &x3);
      p.position.set(x1, x2, x3);
      m.vectorParticles.push_back(p);
    }
    // Tetrahedron
    else if (std::string(tag) == "tetra")
    {
      Tetrahedron t;
      sscanf(parameter, "%d %d %d %d", &t.p1, &t.p2, &t.p3, &t.p4);

      m.vectorTetrahedra.push_back(t);
    }
    else
    {
      // treat other tags
    }
  }
  
  fclose(objFile);
}

//-------------------------------------------------------------------------------------------------------------------------

#define MIN_PAIR(x, y) ((x) < (y) ? int_pair(x, y) : int_pair(y, x))

void  computeSpringSet(Mesh& m)
{
  typedef std::pair<int, int> int_pair;

  std::set<int_pair> springSet;

  for (int tetra = 0; tetra < m.vectorTetrahedra.size(); tetra ++)
  {
    Tetrahedron& t = m.vectorTetrahedra[tetra];
    springSet.insert(MIN_PAIR(t.p1, t.p2));
    springSet.insert(MIN_PAIR(t.p1, t.p3));
    springSet.insert(MIN_PAIR(t.p2, t.p4));
    springSet.insert(MIN_PAIR(t.p2, t.p3));
    springSet.insert(MIN_PAIR(t.p3, t.p4));
    springSet.insert(MIN_PAIR(t.p4, t.p1));
  }

  for (std::set<int_pair>::iterator springSetIter = springSet.begin(); springSetIter != springSet.end(); ++ springSetIter)
  {
    Spring  s;
    s.p1 = springSetIter->first;
    s.p2 = springSetIter->second;
    m.vectorSprings.push_back(s);
  }
}