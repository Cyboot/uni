/******************************************************************************
* vector.h
* Copyright (C) 2004 Bruno Heidelberger <heidelberger@inf.ethz.ch>
*                    Matthias Teschner <teschner@inf.ethz.ch>
******************************************************************************/

#ifndef DCL_VECTOR_H
#define DCL_VECTOR_H

//****************************************************************************//
// Forward declarations
//****************************************************************************//

class DclMatrix33;

typedef double DclFloat;

//****************************************************************************//
// Class declaration
//****************************************************************************//

/*****************************************************************************/
/** The vector class.
*****************************************************************************/

class DclVector
{
  // member variables
public:
  DclFloat x;
  DclFloat y;
  DclFloat z;
  //bool touched; //->only needed for moving points insight the data set while reading the structure-file

  // constructors/destructor
public:
  inline DclVector();
  inline DclVector(const DclVector& v);
  inline DclVector(DclFloat vx, DclFloat vy, DclFloat vz);
  inline ~DclVector();

  // member functions
public:
  inline void add(const DclMatrix33& a, const DclMatrix33& b, const DclVector& f);		// MG plasticity
  inline void blend(DclFloat d, const DclVector& v);
  inline void clear();
  inline void invert();
  inline DclFloat length() const;
  inline DclFloat length2() const;
  inline void multiply(const DclMatrix33& a);
  inline void negate();
  inline DclFloat normalize();
  inline void scale(const DclVector& f);
  inline void square();
  //substract the product of a*b*f from the vector instance. 
  inline void subtract(const DclMatrix33& a, const DclMatrix33& b, const DclVector& f); 
  inline void operator-=(const DclVector& v);
  inline void operator%=(const DclVector& v);
  inline DclFloat operator*(const DclVector& v);
  inline void operator*=(const DclFloat d);
  inline void operator/=(const DclFloat d);
  inline DclFloat& operator[](unsigned int i);
  inline const DclFloat& operator[](unsigned int i) const;
  inline void operator+=(const DclVector& v);
  inline void operator=(const DclVector& v);
  inline bool operator==(const DclVector& v);
	inline bool operator<(const DclVector& v);
  inline void set(DclFloat vx, DclFloat vy, DclFloat vz);
  friend inline DclVector operator-(const DclVector &v1, const DclVector &v2);
  inline DclVector operator-() const;
  friend inline DclVector operator%(const DclVector &v1, const DclVector &v2);
  friend inline DclVector operator*(const DclVector &v, const DclFloat d);
  friend inline DclFloat operator*(const DclVector &v1, const DclVector &v2);
  friend inline DclVector operator/(const DclVector &v, const DclFloat d);
  friend inline DclVector operator+(const DclVector &v1, const DclVector &v2);

  friend inline DclMatrix33 tensor(const DclVector &v1, const DclVector &v2);

  //inline DclVector projectOn(const DclVector& direction);

	inline DclVector DclVector::projectOn(const DclVector& direction);


#ifdef _DEBUG
   inline std::string toStr();
#endif
};

// include the inline functions
//#include "matrix.h"
#include "matrix33.h"
#include "vector.inl"

#endif

//****************************************************************************//
