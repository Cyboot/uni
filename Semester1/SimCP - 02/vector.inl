/******************************************************************************
* vector.inl
* Copyright (C) 2004 Bruno Heidelberger <heidelberger@inf.ethz.ch>
*                    Matthias Teschner <teschner@inf.ethz.ch>
******************************************************************************/

/*****************************************************************************/
/** Constructs the vector instance.
*
* This function is the default constructor of the vector instance.
*****************************************************************************/

inline DclVector::DclVector()
: x(0), y(0), z(0)
{
}

/*****************************************************************************/
/** Constructs the vector instance.
*
* This function is a constructor of the vector instance.
*
* @param v The vector to construct this vector instance from.
*****************************************************************************/

inline DclVector::DclVector(const DclVector& v)
: x(v.x), y(v.y), z(v.z)
{
}

/*****************************************************************************/
/** Constructs the vector instance.
*
* This function is a constructor of the vector instance.
*
* @param vx The x component.
* @param vy The y component.
* @param vz The z component.
*****************************************************************************/

inline DclVector::DclVector(DclFloat vx, DclFloat vy, DclFloat vz)
: x(vx), y(vy), z(vz)
{
}

/*****************************************************************************/
/** Destructs the vector instance.
*
* This function is the destructor of the vector instance.
*****************************************************************************/

inline DclVector::~DclVector()
{
}

/*****************************************************************************/
// MG plasticity
inline void DclVector::add(const DclMatrix33& a, const DclMatrix33& b, const DclVector& f)
{
  DclFloat temp0 = f.x * (a.m[0][0]*b.m[0][0] + a.m[0][1]*b.m[1][0] + a.m[0][2]*b.m[2][0]) 
    + f.y * (a.m[0][0]*b.m[0][1] + a.m[0][1]*b.m[1][1] + a.m[0][2]*b.m[2][1])
    + f.z * (a.m[0][0]*b.m[0][2] + a.m[0][1]*b.m[1][2] + a.m[0][2]*b.m[2][2]); 
  DclFloat temp1 = f.x * (a.m[1][0]*b.m[0][0] + a.m[1][1]*b.m[1][0] + a.m[1][2]*b.m[2][0]) 
    + f.y * (a.m[1][0]*b.m[0][1] + a.m[1][1]*b.m[1][1] + a.m[1][2]*b.m[2][1])
    + f.z * (a.m[1][0]*b.m[0][2] + a.m[1][1]*b.m[1][2] + a.m[1][2]*b.m[2][2]); 
  DclFloat temp2 = f.x * (a.m[2][0]*b.m[0][0] + a.m[2][1]*b.m[1][0] + a.m[2][2]*b.m[2][0]) 
    + f.y * (a.m[2][0]*b.m[0][1] + a.m[2][1]*b.m[1][1] + a.m[2][2]*b.m[2][1])
    + f.z * (a.m[2][0]*b.m[0][2] + a.m[2][1]*b.m[1][2] + a.m[2][2]*b.m[2][2]); 
  x += temp0; 
  y += temp1; 
  z += temp2; 

}
// END MG

/*****************************************************************************/
/** Interpolates the vector instance to another vector.
*
* This function interpolates the vector instance to another vector by a given
* factor.
*
* @param d The blending factor in the range [0.0, 1.0].
* @param v The vector to be interpolated to.
*****************************************************************************/

inline void DclVector::blend(DclFloat d, const DclVector& v)
{
  x += d * (v.x - x);
  y += d * (v.y - y);
  z += d * (v.z - z);
}

/*****************************************************************************/
/** Clears the vector instance.
*
* This function clears the vector instance.
*****************************************************************************/

inline void DclVector::clear()
{
  x = 0;
  y = 0;
  z = 0;
}

/*****************************************************************************/
/** Inverts the vector instance.
*
* This function inverts the vector instance.
*****************************************************************************/

inline void DclVector::invert()
{
  x = (DclFloat)1.0 / x;
  y = (DclFloat)1.0 / y;
  z = (DclFloat)1.0 / z;
}

/*****************************************************************************/
/** Returns the length of the vector instance.
*
* This function returns the length of the vector instance.
*
* @return The length of the vector instance.
*****************************************************************************/

inline DclFloat DclVector::length() const
{
  return sqrt(x * x + y * y + z * z);
}

/*****************************************************************************/
/** Returns the squared length of the vector instance.
*
* This function returns the squared length of the vector instance.
*
* @return The squared length of the vector instance.
*****************************************************************************/

inline DclFloat DclVector::length2() const
{
  return x * x + y * y + z * z;
}

/*****************************************************************************/
/** Multiplies a matrix to the vector instance.
*
* This operator multiplies a matrix to the vector instance.
*
* @param a The matrix to be multiplied.
*****************************************************************************/

inline void DclVector::multiply(const DclMatrix33& a)
{
  DclVector temp(x, y, z);

  x = a.m[0][0] * temp.x + a.m[0][1] * temp.y + a.m[0][2] * temp.z;
  y = a.m[1][0] * temp.x + a.m[1][1] * temp.y + a.m[1][2] * temp.z;
  z = a.m[2][0] * temp.x + a.m[2][1] * temp.y + a.m[2][2] * temp.z;
}

/*****************************************************************************/
/** Negates the vector instance.
*
* This function negates the vector instance.
*****************************************************************************/

inline void DclVector::negate()
{
  x = -x;
  y = -y;
  z = -z;
}

/*****************************************************************************/
/** Normalizes the vector instance.
*
* This function normalizes the vector instance and returns its former length.
*
* @return The length of the vector instance before normalizing.
*****************************************************************************/

inline DclFloat DclVector::normalize()
{
  // calculate the length of the vector
  DclFloat length;
  length = sqrt(x * x + y * y + z * z);
  if(length == 0) return 0;

  // normalize the vector
  x /= length;
  y /= length;
  z /= length;

  return length;
}

/*****************************************************************************/
/** Scales the vector instance.
*
* This function scales the vector instance.
*
* @param f The scaling factors.
*****************************************************************************/

inline void DclVector::scale(const DclVector& f)
{
  x *= f.x;
  y *= f.y;
  z *= f.z;
}

/*****************************************************************************/
/** Sets new values.
*
* This function sets new values in the vector instance.
*
* @param x The x component.
* @param y The y component.
* @param z The z component.
*****************************************************************************/

inline void DclVector::set(DclFloat vx, DclFloat vy, DclFloat vz)
{
  x = vx;
  y = vy;
  z = vz;
}

/*****************************************************************************/
/** Squares the vector instance.
*
* This function squares the vector instance.
*****************************************************************************/

inline void DclVector::square()
{
  x *= x;
  y *= y;
  z *= z;
}

/*****************************************************************************/
inline void DclVector::subtract(const DclMatrix33& a, const DclMatrix33& b, const DclVector& f)
{
  DclFloat temp0 = f.x * (a.m[0][0]*b.m[0][0] + a.m[0][1]*b.m[1][0] + a.m[0][2]*b.m[2][0]) 
    + f.y * (a.m[0][0]*b.m[0][1] + a.m[0][1]*b.m[1][1] + a.m[0][2]*b.m[2][1])
    + f.z * (a.m[0][0]*b.m[0][2] + a.m[0][1]*b.m[1][2] + a.m[0][2]*b.m[2][2]); 
  DclFloat temp1 = f.x * (a.m[1][0]*b.m[0][0] + a.m[1][1]*b.m[1][0] + a.m[1][2]*b.m[2][0]) 
    + f.y * (a.m[1][0]*b.m[0][1] + a.m[1][1]*b.m[1][1] + a.m[1][2]*b.m[2][1])
    + f.z * (a.m[1][0]*b.m[0][2] + a.m[1][1]*b.m[1][2] + a.m[1][2]*b.m[2][2]); 
  DclFloat temp2 = f.x * (a.m[2][0]*b.m[0][0] + a.m[2][1]*b.m[1][0] + a.m[2][2]*b.m[2][0]) 
    + f.y * (a.m[2][0]*b.m[0][1] + a.m[2][1]*b.m[1][1] + a.m[2][2]*b.m[2][1])
    + f.z * (a.m[2][0]*b.m[0][2] + a.m[2][1]*b.m[1][2] + a.m[2][2]*b.m[2][2]); 
  x -= temp0; 
  y -= temp1; 
  z -= temp2; 

}

/*****************************************************************************/
/** Subtracts another vector from the vector instance.
*
* This operator subtracts another vector from the vector instance.
*
* @param v The vector to be subtracted.
*****************************************************************************/

inline void DclVector::operator-=(const DclVector& v)
{
  x -= v.x;
  y -= v.y;
  z -= v.z;
}

  inline DclVector DclVector::operator-() const
 {
		DclVector result(*this);
		result.negate();
		return result;
 }

/*****************************************************************************/
/** Calculates the vector product of two vectors.
*
* This operator calculates the vector product of two vectors.
*
* @param v The first vector.
* @param u The second vector.
*
* @return The vector product of the two vectors.
*****************************************************************************/

 inline void DclVector::operator%=(const DclVector& v)
{
  DclVector temp(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
  *this = temp;
}

/*****************************************************************************/
/** Computes the dot product of the vector instance with another vector.
*
* This operator computes the dot product of the vector instance with another
* vector.
*
* @param v The vector to compute the dot product with.
*****************************************************************************/

inline DclFloat DclVector::operator*(const DclVector& v)
{
  return x * v.x + y * v.y + z * v.z;
}

/*****************************************************************************/
/** Scales the vector instance.
*
* This operator scales the vector instance by multiplying its components by
* a specific factor.
*
* @param d The factor to multiply the vector components by.
*****************************************************************************/

inline void DclVector::operator*=(const DclFloat d)
{
  x *= d;
  y *= d;
  z *= d;
}


/*****************************************************************************/
/** Scales the vector instance.
*
* This operator scales the vector instance by dividing its components by a
* specific factor.
*
* @param d The factor to divide the vector components by.
*****************************************************************************/

inline void DclVector::operator/=(const DclFloat d)
{
  x /= d;
  y /= d;
  z /= d;
}

/*****************************************************************************/
/** Provides access to the components of the vector instance.
*
* This function provides read and write access to the three components of the
* vector instance.
*
* @param i The index to the specific component.
*
* @return A reference to the specific component.
*****************************************************************************/

inline DclFloat& DclVector::operator[](unsigned int i)
{
  return (&x)[i];
}

/*****************************************************************************/
/** Provides access to the components of the vector instance.
*
* This function provides read access to the three components of the vector
* instance.
*
* @param i The index to the specific component.
*
* @return A constant reference to the specific component.
*****************************************************************************/

inline const DclFloat& DclVector::operator[](unsigned int i) const
{
  return (&x)[i];
}

/*****************************************************************************/
/** Adds another vector to the vector instance.
*
* This operator adds another vector to the vector instance.
*
* @param v The vector to be added.
*****************************************************************************/

inline void DclVector::operator+=(const DclVector& v)
{
  x += v.x;
  y += v.y;
  z += v.z;
}

/*****************************************************************************/
/** Equates the vector instance with another vector.
*
* This operator equates the vector instance with another vector.
*
* @param v The vector to equate the vector instance with.
*****************************************************************************/

inline void DclVector::operator=(const DclVector& v)
{
  x = v.x;
  y = v.y;
  z = v.z;
}

/*****************************************************************************/
/** Tests the equality of 2 vectors
*
* This operator checks to see if 2 vectors are equal
*
* @param v The vector to be tested against.
*****************************************************************************/

inline bool DclVector::operator==(const DclVector& v)
{
  return ((x == v.x) && (y == v.y) && (z == v.z));
}

/*****************************************************************************/
/** Subtracts a vector from another vector.
*
* This operator subtracts a vector from another vector.
*
* @param v1 The first vector.
* @param v2 The second vector.
*
* @return The result of the vector subtraction.
*****************************************************************************/

 inline DclVector operator-(const DclVector &v1, const DclVector &v2)
{
  return DclVector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
}

/*****************************************************************************/
/** Calculates the vector product of two vectors.
*
* This operator calculates the vector product of two vectors.
*
* @param v1 The first vector.
* @param v2 The second vector.
*
* @return The vector product of the two vectors.
*****************************************************************************/

 inline DclVector operator%(const DclVector &v1, const DclVector &v2)
{
  return DclVector(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
}

/*****************************************************************************/
/** Scales a vector.
*
* This operator scales a vector instance.
*
* @param v The vector to scale.
* @param d The scale factor.
*
* @return The scaled vector.
*****************************************************************************/

 inline DclVector operator*(const DclVector &v, const DclFloat d)
{
  return DclVector(v.x * d, v.y * d, v.z * d);
}

/*****************************************************************************/
/** Calculates the dot product of two vectors.
*
* This operator calculates the dot product of two vectors.
*
* @param v1 The first vector.
* @param v2 The second vector.
*
* @return The dot product of the two vectors.
*****************************************************************************/

 inline DclFloat operator*(const DclVector &v1, const DclVector &v2)
{
  return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
}

/*****************************************************************************/
/** Scales a vector.
*
* This operator scales a vector instance.
*
* @param v The vector to scale.
* @param d The reciprocal scale factor.
*
* @return The scaled vector.
*****************************************************************************/

 inline DclVector operator/(const DclVector &v, const DclFloat d)
{
  return DclVector(v.x / d, v.y / d, v.z / d);
}

/*****************************************************************************/
/** Adds two vectors.
*
* This operator adds two vectors.
*
* @param v1 The first vector.
* @param v2 The second vector.
*
* @return The sum of the two vectors.
*****************************************************************************/

 inline DclVector operator+(const DclVector &v1, const DclVector &v2)
{
  return DclVector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
}

/*****************************************************************************/

 inline DclMatrix33 tensor(const DclVector &v1, const DclVector &v2)
{
  //return the tensor product of the vectors v1 and v2. 
  return DclMatrix33(v1*v2.x,v1*v2.y,v1*v2.z);
}

/*****************************************************************************/

inline DclVector DclVector::projectOn(const DclVector& direction)
{
  DclFloat cosine = (*this) * direction;
  return DclVector(direction / direction.length2() * cosine);
}


//****************************************************************************//

