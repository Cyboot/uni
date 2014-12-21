/******************************************************************************
* matrix33.h
* Copyright (C) 2004 Bruno Heidelberger <heidelberger@inf.ethz.ch>
******************************************************************************/

#ifndef DCL_MATRIX33_H
#define DCL_MATRIX33_H

//****************************************************************************//
// Forward declarations
//****************************************************************************//

#include "matrix.h"

//****************************************************************************//
// Class declaration
//****************************************************************************//

/*****************************************************************************/
/** The matrix class for 3x3-matrices. Derived from the general matrix class.
*****************************************************************************/

class DclMatrix33 : public DclMatrix
{

  // constructors/destructor
public:
  ///Default constructor 
  inline DclMatrix33();

  ///Construct the matrix instance from the matrix a
  inline DclMatrix33(const DclMatrix33& a);

  ///Construct the matrix instance from the DclVectors v1,v2,v3 (viewed as column-vectors).
  inline DclMatrix33(const DclVector& v1, const DclVector& v2, const DclVector& v3);

  ///extracts a 3x3-matrix from M starting with (0,0) if M is big enough, else nothing is done. 
  inline DclMatrix33(const DclMatrix& M); 

  ///Destructor of the matrix-instance
  inline ~DclMatrix33();

  // member functions
public:
  //inline void clear(); //this function is already implemented in the base class

  /// Calculates the determinant of the matrix instance.
  virtual inline DclFloat determinant() const;

  /// Sets the matrix instance to the identity.
  virtual inline void identity();

  /// Inverts the matrix instance.
  virtual inline bool inverse();

  /// Multiplies two matrices into the matrix instance.
  inline void multiply(const DclMatrix33& a1, const DclMatrix33& a2);


  ///Multiplies the first matrix and the transposed second matrix into the matrix instance.
  inline void multiplyTranspose(const DclMatrix33& a1, const DclMatrix33& a2);

  ///Multiplies the first matrix, the transposed second matrix and the vertex into the matrix instance
  inline void multiplyTranspose(const DclMatrix33& a1, const DclMatrix33& a2, const DclVector& v);

  ///Multiplies the first vector and the transposed second vector into the matrix instance.
  inline void multiplyTranspose(const DclVector& v1, const DclVector& v2);

  ///Calculates the norm of the matrix instance: $\sum_{ij} m_{ij}^2$.
  inline DclFloat norm() const;

  //inline void transpose(); //implemented in the base class.

  ///Multiplies the transposed first matrix and the second matrix into the matrix instance.
  inline void transposeMultiply(const DclMatrix33& a1, const DclMatrix33& a2);

  ///This operator substracts another matrix from the matrix instance.
  inline void operator-=(const DclMatrix33& a);

  //virtual inline DclFloat& operator()(int row, int column); //implemented in base-class 
  //virtual inline const DclFloat& operator()(int row, int column) const; //implemented in base-class 

  ///This operator adds another matrix to the matrix instance.
  inline void operator+=(const DclMatrix33& a);

  inline DclMatrix33 operator*(const DclFloat d); 

  inline DclMatrix33 operator*(const DclMatrix33& ); 

  ///This operator multiplies another matrix to the matrix instance.
  inline void operator*=(const DclMatrix33& a);

  ///This operator multiplies every entry of the matrix instance with the value d.
  inline void operator*=(const DclFloat d);

  ///This operator divides every entry of the matrix instance by the value d. If d=0 no division takes place.
  inline void operator/=(const DclFloat d);

  ///This operator equates the matrix instance with another matrix. Returns a reference to the matrix-instance for concatentation. 
  inline DclMatrix33& operator=(const DclMatrix33& v);

  inline DclMatrix33& operator=(const DclMatrix& M); 


  ///This operator multiplies the matrix instance with the DclVector v from right and returns the result. The matrix instance is unchanged.
  inline DclVector operator*(const DclVector& v); 

  ///Multiplies the matrix instance from left with vector v and returns the result. The matrix instance is unchanged. 
  inline friend DclVector operator*(const DclVector& v, const DclMatrix33&);

  inline friend DclMatrix33 transpose(const DclMatrix33&);

  //DEBUG-Stuff
  inline void jacobiRotate(DclMatrix33 &R, int p, int q);
  inline void eigenDecomposition(DclMatrix33& R);
  inline void polarDecomposition(DclMatrix33& R);
  inline void polarDecomposition(DclMatrix33& R, DclVector& eigenValues);

#ifdef DCL_CLOTH_SUPPORT
  inline void calculatePolarDecomposition();
#endif
};

// include the inline functions
#include "vector.h"
#include "matrix33.inl"
#endif

//****************************************************************************//
