/**
 * Nikolaus Mayer, 2014 (mayern@cs.uni-freiburg.de)
 *
 * Image Processing and Computer Graphics
 * Winter Term 2014/2015
 * Exercise Sheet 2 (Image Processing part)
 *
 * Sparse matrix system for Horn-Schunck optical flow estimation
 */

/// Local files
#include "../lib/CMatrix.h"



class HornSchunckMatrix
{

public:

  /// Constructor
  HornSchunckMatrix(
      const CMatrix<float>& derivX, 
      const CMatrix<float>& derivY, 
      const CMatrix<float>& derivT,
      float alpha=1.f );

  /// Access to coefficient matrix entries (A in Ax=b)
  float operator()(
      int x, 
      int y ) const;

  /// Result vector entries (b in Ax=b)
  float res(
      int i ) const;

protected:

  inline bool on_main_diagonal(
      int x,
      int y ) const;
  inline bool on_near_diagonal(
      int x,
      int y ) const;
  inline bool on_far_diagonal(
      int x,
      int y ) const;
  inline bool on_off_block_diagonal(
      int x,
      int y ) const;

  CMatrix<float> derivX, derivY, derivT;
  float ialpha;
  int pixelcount, width, height;

};

