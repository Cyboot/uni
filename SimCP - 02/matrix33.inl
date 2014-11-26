/******************************************************************************
* matrix33.inl
* The definitions of the matrix33-class. 
* Copyright (C) 2004 Bruno Heidelberger <heidelberger@inf.ethz.ch>
******************************************************************************/

inline DclMatrix33::DclMatrix33() : DclMatrix(3,3)
{
  //values are set to zero by MATRIX-constructor
}

/*****************************************************************************/
inline DclMatrix33::DclMatrix33(const DclMatrix33& a) : DclMatrix(3,3)
{
  m[0][0] = a.m[0][0]; m[0][1] = a.m[0][1]; m[0][2] = a.m[0][2];
  m[1][0] = a.m[1][0]; m[1][1] = a.m[1][1]; m[1][2] = a.m[1][2];
  m[2][0] = a.m[2][0]; m[2][1] = a.m[2][1]; m[2][2] = a.m[2][2];
}

/*****************************************************************************/
inline DclMatrix33::DclMatrix33(const DclVector& v1, const DclVector& v2, const DclVector& v3) : DclMatrix(3,3)
{
  m[0][0] = v1.x; m[0][1] = v2.x; m[0][2] = v3.x;
  m[1][0] = v1.y; m[1][1] = v2.y; m[1][2] = v3.y;
  m[2][0] = v1.z; m[2][1] = v2.z; m[2][2] = v3.z;
}

/*****************************************************************************/
inline DclMatrix33::DclMatrix33(const DclMatrix& M) : DclMatrix(3,3) 
{
  if (M.getrow() < 3 || M.getcolumn() < 3)
    return; 
  for (int i = 0; i < 3; i++)
    for (int j = 0; j < 3; j++)
      m[i][j] = M.m[i][j]; 
}

/*****************************************************************************/
inline DclMatrix33::~DclMatrix33()
{		
}

/*****************************************************************************/
inline DclFloat DclMatrix33::determinant() const
{
  return  m[0][0] * m[1][1] * m[2][2] + m[0][1] * m[1][2] * m[2][0] + m[0][2] * m[1][0] * m[2][1]
  - m[0][2] * m[1][1] * m[2][0] - m[0][1] * m[1][0] * m[2][2] - m[0][0] * m[1][2] * m[2][1];
}

/*****************************************************************************/
inline void DclMatrix33::identity()
{
  m[0][0] = (DclFloat)1.0; m[0][1] = 0; m[0][2] = 0;
  m[1][0] = 0; m[1][1] = (DclFloat)1.0; m[1][2] = 0;
  m[2][0] = 0; m[2][1] = 0; m[2][2] = (DclFloat)1.0;
}

/*****************************************************************************/
inline bool DclMatrix33::inverse()
{
  DclFloat temp00 = m[1][1] * m[2][2] - m[1][2] * m[2][1];
  DclFloat temp01 = m[0][2] * m[2][1] - m[0][1] * m[2][2];
  DclFloat temp02 = m[0][1] * m[1][2] - m[0][2] * m[1][1];
  DclFloat temp10 = m[1][2] * m[2][0] - m[1][0] * m[2][2];
  DclFloat temp11 = m[0][0] * m[2][2] - m[0][2] * m[2][0];
  DclFloat temp12 = m[0][2] * m[1][0] - m[0][0] * m[1][2];
  DclFloat temp20 = m[1][0] * m[2][1] - m[1][1] * m[2][0];
  DclFloat temp21 = m[0][1] * m[2][0] - m[0][0] * m[2][1];
  DclFloat temp22 = m[0][0] * m[1][1] - m[0][1] * m[1][0];

  DclFloat d = temp00 * m[0][0] + temp01 * m[1][0] + temp02 * m[2][0];

  // check if singular
  if(d == 0)
  {
    identity();
    return false;
  }

  d = (DclFloat)1.0 / d;

  m[0][0] = temp00 * d; m[0][1] = temp01 * d; m[0][2] = temp02 * d;
  m[1][0] = temp10 * d; m[1][1] = temp11 * d; m[1][2] = temp12 * d;
  m[2][0] = temp20 * d; m[2][1] = temp21 * d; m[2][2] = temp22 * d;

  return true;
}

/*****************************************************************************/
inline void DclMatrix33::multiply(const DclMatrix33& a1, const DclMatrix33& a2)
{
  DclFloat temp00 = a1.m[0][0] * a2.m[0][0] + a1.m[0][1] * a2.m[1][0] + a1.m[0][2] * a2.m[2][0];
  DclFloat temp01 = a1.m[0][0] * a2.m[0][1] + a1.m[0][1] * a2.m[1][1] + a1.m[0][2] * a2.m[2][1];
  DclFloat temp02 = a1.m[0][0] * a2.m[0][2] + a1.m[0][1] * a2.m[1][2] + a1.m[0][2] * a2.m[2][2];
  DclFloat temp10 = a1.m[1][0] * a2.m[0][0] + a1.m[1][1] * a2.m[1][0] + a1.m[1][2] * a2.m[2][0];
  DclFloat temp11 = a1.m[1][0] * a2.m[0][1] + a1.m[1][1] * a2.m[1][1] + a1.m[1][2] * a2.m[2][1];
  DclFloat temp12 = a1.m[1][0] * a2.m[0][2] + a1.m[1][1] * a2.m[1][2] + a1.m[1][2] * a2.m[2][2];
  DclFloat temp20 = a1.m[2][0] * a2.m[0][0] + a1.m[2][1] * a2.m[1][0] + a1.m[2][2] * a2.m[2][0];
  DclFloat temp21 = a1.m[2][0] * a2.m[0][1] + a1.m[2][1] * a2.m[1][1] + a1.m[2][2] * a2.m[2][1];
  DclFloat temp22 = a1.m[2][0] * a2.m[0][2] + a1.m[2][1] * a2.m[1][2] + a1.m[2][2] * a2.m[2][2];

  m[0][0] = temp00; m[0][1] = temp01; m[0][2] = temp02;
  m[1][0] = temp10; m[1][1] = temp11; m[1][2] = temp12;
  m[2][0] = temp20; m[2][1] = temp21; m[2][2] = temp22;
}


/*****************************************************************************/
inline void DclMatrix33::multiplyTranspose(const DclMatrix33& a1, const DclMatrix33& a2)
{
  DclFloat temp00 = a1.m[0][0] * a2.m[0][0] + a1.m[0][1] * a2.m[0][1] + a1.m[0][2] * a2.m[0][2];
  DclFloat temp01 = a1.m[0][0] * a2.m[1][0] + a1.m[0][1] * a2.m[1][1] + a1.m[0][2] * a2.m[1][2];
  DclFloat temp02 = a1.m[0][0] * a2.m[2][0] + a1.m[0][1] * a2.m[2][1] + a1.m[0][2] * a2.m[2][2];
  DclFloat temp10 = a1.m[1][0] * a2.m[0][0] + a1.m[1][1] * a2.m[0][1] + a1.m[1][2] * a2.m[0][2];
  DclFloat temp11 = a1.m[1][0] * a2.m[1][0] + a1.m[1][1] * a2.m[1][1] + a1.m[1][2] * a2.m[1][2];
  DclFloat temp12 = a1.m[1][0] * a2.m[2][0] + a1.m[1][1] * a2.m[2][1] + a1.m[1][2] * a2.m[2][2];
  DclFloat temp20 = a1.m[2][0] * a2.m[0][0] + a1.m[2][1] * a2.m[0][1] + a1.m[2][2] * a2.m[0][2];
  DclFloat temp21 = a1.m[2][0] * a2.m[1][0] + a1.m[2][1] * a2.m[1][1] + a1.m[2][2] * a2.m[1][2];
  DclFloat temp22 = a1.m[2][0] * a2.m[2][0] + a1.m[2][1] * a2.m[2][1] + a1.m[2][2] * a2.m[2][2];

  m[0][0] = temp00; m[0][1] = temp01; m[0][2] = temp02;
  m[1][0] = temp10; m[1][1] = temp11; m[1][2] = temp12;
  m[2][0] = temp20; m[2][1] = temp21; m[2][2] = temp22;
}

/*****************************************************************************/
inline void DclMatrix33::multiplyTranspose(const DclMatrix33& a1, const DclMatrix33& a2, const DclVector& v)
{
  DclFloat temp00 = v.x * a1.m[0][0] * a2.m[0][0] + v.y * a1.m[0][1] * a2.m[0][1] + v.z * a1.m[0][2] * a2.m[0][2];
  DclFloat temp01 = v.x * a1.m[0][0] * a2.m[1][0] + v.y * a1.m[0][1] * a2.m[1][1] + v.z * a1.m[0][2] * a2.m[1][2];
  DclFloat temp02 = v.x * a1.m[0][0] * a2.m[2][0] + v.y * a1.m[0][1] * a2.m[2][1] + v.z * a1.m[0][2] * a2.m[2][2];
  DclFloat temp10 = v.x * a1.m[1][0] * a2.m[0][0] + v.y * a1.m[1][1] * a2.m[0][1] + v.z * a1.m[1][2] * a2.m[0][2];
  DclFloat temp11 = v.x * a1.m[1][0] * a2.m[1][0] + v.y * a1.m[1][1] * a2.m[1][1] + v.z * a1.m[1][2] * a2.m[1][2];
  DclFloat temp12 = v.x * a1.m[1][0] * a2.m[2][0] + v.y * a1.m[1][1] * a2.m[2][1] + v.z * a1.m[1][2] * a2.m[2][2];
  DclFloat temp20 = v.x * a1.m[2][0] * a2.m[0][0] + v.y * a1.m[2][1] * a2.m[0][1] + v.z * a1.m[2][2] * a2.m[0][2];
  DclFloat temp21 = v.x * a1.m[2][0] * a2.m[1][0] + v.y * a1.m[2][1] * a2.m[1][1] + v.z * a1.m[2][2] * a2.m[1][2];
  DclFloat temp22 = v.x * a1.m[2][0] * a2.m[2][0] + v.y * a1.m[2][1] * a2.m[2][1] + v.z * a1.m[2][2] * a2.m[2][2];

  m[0][0] = temp00; m[0][1] = temp01; m[0][2] = temp02;
  m[1][0] = temp10; m[1][1] = temp11; m[1][2] = temp12;
  m[2][0] = temp20; m[2][1] = temp21; m[2][2] = temp22;
}

/*****************************************************************************/
inline void DclMatrix33::multiplyTranspose(const DclVector& v1, const DclVector& v2)
{
  m[0][0] = v1.x * v2.x; m[0][1] = v1.x * v2.y; m[0][2] = v1.x * v2.z;
  m[1][0] = v1.y * v2.x; m[1][1] = v1.y * v2.y; m[1][2] = v1.y * v2.z;
  m[2][0] = v1.z * v2.x; m[2][1] = v1.z * v2.y; m[2][2] = v1.z * v2.z;
}

/*****************************************************************************/
inline DclFloat DclMatrix33::norm() const
{
  return   m[0][0] * m[0][0] + m[0][1] * m[0][1] + m[0][2] * m[0][2]
  + m[1][0] * m[1][0] + m[1][1] * m[1][1] + m[1][2] * m[1][2]
  + m[2][0] * m[2][0] + m[2][1] * m[2][1] + m[2][2] * m[2][2];
}

/*****************************************************************************/
inline void DclMatrix33::transposeMultiply(const DclMatrix33& a1, const DclMatrix33& a2)
{
  DclFloat temp00 = a1.m[0][0] * a2.m[0][0] + a1.m[1][0] * a2.m[1][0] + a1.m[2][0] * a2.m[2][0];
  DclFloat temp01 = a1.m[0][0] * a2.m[0][1] + a1.m[1][0] * a2.m[1][1] + a1.m[2][0] * a2.m[2][1];
  DclFloat temp02 = a1.m[0][0] * a2.m[0][2] + a1.m[1][0] * a2.m[1][2] + a1.m[2][0] * a2.m[2][2];
  DclFloat temp10 = a1.m[0][1] * a2.m[0][0] + a1.m[1][1] * a2.m[1][0] + a1.m[2][1] * a2.m[2][0];
  DclFloat temp11 = a1.m[0][1] * a2.m[0][1] + a1.m[1][1] * a2.m[1][1] + a1.m[2][1] * a2.m[2][1];
  DclFloat temp12 = a1.m[0][1] * a2.m[0][2] + a1.m[1][1] * a2.m[1][2] + a1.m[2][1] * a2.m[2][2];
  DclFloat temp20 = a1.m[0][2] * a2.m[0][0] + a1.m[1][2] * a2.m[1][0] + a1.m[2][2] * a2.m[2][0];
  DclFloat temp21 = a1.m[0][2] * a2.m[0][1] + a1.m[1][2] * a2.m[1][1] + a1.m[2][2] * a2.m[2][1];
  DclFloat temp22 = a1.m[0][2] * a2.m[0][2] + a1.m[1][2] * a2.m[1][2] + a1.m[2][2] * a2.m[2][2];

  m[0][0] = temp00; m[0][1] = temp01; m[0][2] = temp02;
  m[1][0] = temp10; m[1][1] = temp11; m[1][2] = temp12;
  m[2][0] = temp20; m[2][1] = temp21; m[2][2] = temp22;
}

/*****************************************************************************/
inline void DclMatrix33::operator-=(const DclMatrix33& a)
{
  m[0][0] -= a.m[0][0]; m[0][1] -= a.m[0][1]; m[0][2] -= a.m[0][2];
  m[1][0] -= a.m[1][0]; m[1][1] -= a.m[1][1]; m[1][2] -= a.m[1][2];
  m[2][0] -= a.m[2][0]; m[2][1] -= a.m[2][1]; m[2][2] -= a.m[2][2];
}

/*****************************************************************************/
inline void DclMatrix33::operator+=(const DclMatrix33& a)
{
  m[0][0] += a.m[0][0]; m[0][1] += a.m[0][1]; m[0][2] += a.m[0][2];
  m[1][0] += a.m[1][0]; m[1][1] += a.m[1][1]; m[1][2] += a.m[1][2];
  m[2][0] += a.m[2][0]; m[2][1] += a.m[2][1]; m[2][2] += a.m[2][2];
}

/*****************************************************************************/

inline DclMatrix33 DclMatrix33::operator*(const DclFloat d)
{
  DclMatrix33 temp; 
  temp.m[0][0] = m[0][0] * d; temp.m[0][1] = m[0][1] * d; temp.m[0][2] = m[0][2] * d; 
  temp.m[1][0] = m[1][0] * d; temp.m[1][1] = m[1][1] * d; temp.m[1][2] = m[1][2] * d; 
  temp.m[2][0] = m[2][0] * d; temp.m[2][1] = m[2][1] * d; temp.m[2][2] = m[2][2] * d; 
  return temp; 
}

/*****************************************************************************/

inline DclMatrix33 DclMatrix33::operator*(const DclMatrix33& a)
{
  DclMatrix33 temp; 
  temp.m[0][0] = m[0][0] * a.m[0][0] + m[0][1] * a.m[1][0] + m[0][2] * a.m[2][0];
  temp.m[0][1] = m[0][0] * a.m[0][1] + m[0][1] * a.m[1][1] + m[0][2] * a.m[2][1];
  temp.m[0][2] = m[0][0] * a.m[0][2] + m[0][1] * a.m[1][2] + m[0][2] * a.m[2][2];
  temp.m[1][0] = m[1][0] * a.m[0][0] + m[1][1] * a.m[1][0] + m[1][2] * a.m[2][0];
  temp.m[1][1] = m[1][0] * a.m[0][1] + m[1][1] * a.m[1][1] + m[1][2] * a.m[2][1];
  temp.m[1][2] = m[1][0] * a.m[0][2] + m[1][1] * a.m[1][2] + m[1][2] * a.m[2][2];
  temp.m[2][0] = m[2][0] * a.m[0][0] + m[2][1] * a.m[1][0] + m[2][2] * a.m[2][0];
  temp.m[2][1] = m[2][0] * a.m[0][1] + m[2][1] * a.m[1][1] + m[2][2] * a.m[2][1];
  temp.m[2][2] = m[2][0] * a.m[0][2] + m[2][1] * a.m[1][2] + m[2][2] * a.m[2][2];

  return temp; 
}

/*****************************************************************************/
inline void DclMatrix33::operator*=(const DclMatrix33& a)
{
  DclFloat temp00 = m[0][0] * a.m[0][0] + m[0][1] * a.m[1][0] + m[0][2] * a.m[2][0];
  DclFloat temp01 = m[0][0] * a.m[0][1] + m[0][1] * a.m[1][1] + m[0][2] * a.m[2][1];
  DclFloat temp02 = m[0][0] * a.m[0][2] + m[0][1] * a.m[1][2] + m[0][2] * a.m[2][2];
  DclFloat temp10 = m[1][0] * a.m[0][0] + m[1][1] * a.m[1][0] + m[1][2] * a.m[2][0];
  DclFloat temp11 = m[1][0] * a.m[0][1] + m[1][1] * a.m[1][1] + m[1][2] * a.m[2][1];
  DclFloat temp12 = m[1][0] * a.m[0][2] + m[1][1] * a.m[1][2] + m[1][2] * a.m[2][2];
  DclFloat temp20 = m[2][0] * a.m[0][0] + m[2][1] * a.m[1][0] + m[2][2] * a.m[2][0];
  DclFloat temp21 = m[2][0] * a.m[0][1] + m[2][1] * a.m[1][1] + m[2][2] * a.m[2][1];
  DclFloat temp22 = m[2][0] * a.m[0][2] + m[2][1] * a.m[1][2] + m[2][2] * a.m[2][2];

  m[0][0] = temp00; m[0][1] = temp01; m[0][2] = temp02;
  m[1][0] = temp10; m[1][1] = temp11; m[1][2] = temp12;
  m[2][0] = temp20; m[2][1] = temp21; m[2][2] = temp22;
}

/*****************************************************************************/
inline void DclMatrix33::operator*=(const DclFloat d)
{
  m[0][0] *= d; m[0][1] *= d; m[0][2] *= d;
  m[1][0] *= d; m[1][1] *= d; m[1][2] *= d;
  m[2][0] *= d; m[2][1] *= d; m[2][2] *= d;
}

/*****************************************************************************/
inline void DclMatrix33::operator/=(const DclFloat d)
{
  if (d == 0)
    return;
  m[0][0] /= d; m[1][0] /= d; m[0][2] /= d;
  m[1][0] /= d; m[1][1] /= d; m[1][2] /= d;
  m[2][0] /= d; m[2][1] /= d; m[2][2] /= d;
}

/*****************************************************************************/
inline DclMatrix33& DclMatrix33::operator=(const DclMatrix33& a)
{
  m[0][0] = a.m[0][0]; m[0][1] = a.m[0][1]; m[0][2] = a.m[0][2];
  m[1][0] = a.m[1][0]; m[1][1] = a.m[1][1]; m[1][2] = a.m[1][2];
  m[2][0] = a.m[2][0]; m[2][1] = a.m[2][1]; m[2][2] = a.m[2][2];
  return (*this);
}

inline DclMatrix33& DclMatrix33::operator=(const DclMatrix& M)
{
  if (M.getcolumn() != 3 || M.getrow() != 3)
    return (*this);
  for (int i = 0; i < 3; i++)
    for (int j = 0; j < 3; j++)
      m[i][j] = M.m[i][j];
  return (*this); 
}



/**************************************************************************/
inline DclVector DclMatrix33::operator*(const DclVector& v)
{
  DclVector temp; 
  temp.x = m[0][0]*v[0] + m[0][1]*v[1] + m[0][2] * v[2];
  temp.y = m[1][0]*v[0] + m[1][1]*v[1] + m[1][2] * v[2];
  temp.z = m[2][0]*v[0] + m[2][1]*v[1] + m[2][2] * v[2];
  return temp; 
}	

/**************************************************************************/

inline DclVector operator*(const DclVector& v, const DclMatrix33& A)
{
  DclVector temp; 
  temp.x = v.x * A.m[0][0] + v.y * A.m[1][0] + v.z * A.m[2][0]; 
  temp.y = v.x * A.m[0][1] + v.y * A.m[1][1] + v.z * A.m[2][1]; 
  temp.z = v.x * A.m[0][2] + v.y * A.m[1][2] + v.z * A.m[2][2]; 
  return temp; 
}

inline DclMatrix33 transpose(const DclMatrix33& M)
{
  DclMatrix33 temp; 
  for (int i = 0; i < 3; i++)
    for (int j = 0; j < 3; j++)
      temp.m[i][j] = M.m[j][i]; 
  return temp;
}

//---------------------------------------------------------------------
//---------------------------------------------------------------------
//---------------------------------------------------------------------
// DEBUG
//---------------------------------------------------------------------
//---------------------------------------------------------------------
//---------------------------------------------------------------------

#define JACOBI_EPSILON 1e-15
#define JACOBI_ITERATIONS 100

inline void DclMatrix33::jacobiRotate(DclMatrix33 &R, int p, int q)
{
  // rotates A through phi in pq-plane to set A.get(p, q) = 0
  // rotation stored in R whose columns are eigenvectors of A
  DclFloat d = (m[p][p] - m[q][q])/((DclFloat)2.0 * m[p][q]);
  DclFloat t = (DclFloat)1.0 / (fabs(d) + sqrt(d * d + (DclFloat)1.0));
  if(d < 0) t = -t;
  DclFloat c = (DclFloat)1.0 / sqrt(t * t + 1);
  DclFloat s = t * c;
  m[p][p] += t * m[p][q];
  m[q][q] -= t * m[p][q];
  m[p][q] = m[q][p] = 0;
  // transform A
  int k;
  for (k = 0; k < 3; k++) {
    if(k != p && k != q) {
      DclFloat Akp = c * m[k][p] + s * m[k][q];
      DclFloat Akq =-s * m[k][p] + c * m[k][q];
      m[k][p] = m[p][k] = Akp;
      m[k][q] = m[q][k] = Akq;
    }
  }
  // store rotation in R
  for (k = 0; k < 3; k++) {
    DclFloat Rkp = c * R.m[k][p] + s * R.m[k][q];
    DclFloat Rkq =-s * R.m[k][p] + c * R.m[k][q];
    R.m[k][p] = Rkp;
    R.m[k][q] = Rkq;
  }
}


inline void DclMatrix33::eigenDecomposition(DclMatrix33 &R)
{
  // only for symmetric matrices!
  // A = R A' R^T, where A' is diagonal and R orthonormal

  R.identity();	// unit matrix
  int iter = 0;
  while (iter < JACOBI_ITERATIONS) {	// 3 off diagonal elements
    // find off diagonal element with maximum modulus
    int p,q;
    DclFloat a,max;
    max = fabs(m[0][1]);
    p = 0; q = 1;
    a  = fabs(m[0][2]);
    if(a > max) { p = 0; q = 2; max = a; }
    a  = fabs(m[1][2]);
    if(a > max) { p = 1; q = 2; max = a; }
    // all small enough -> done
    if(max < JACOBI_EPSILON) break;
    // rotate matrix with respect to that element
    jacobiRotate(R, p, q);
    iter++;
  }
}

inline void DclMatrix33::polarDecomposition(DclMatrix33& R)
{
  DclVector unused;
  polarDecomposition(R, unused);
}


inline void DclMatrix33::polarDecomposition(DclMatrix33& R, DclVector& eigenValues)
{
  // A = R S, where S is symmetric and R is orthonormal
  // -> S = (A^T A)^(1/2)
  // -> R = A S^-1

  DclMatrix33 ATA;
  ATA.transposeMultiply(*this, *this);

  DclMatrix33 U;
  ATA.eigenDecomposition(U);

  eigenValues.x = ATA.m[0][0];
  eigenValues.y = ATA.m[1][1];
  eigenValues.z = ATA.m[2][2];

  DclFloat l0 = ATA.m[0][0]; if(l0 <= 0) l0 = 0; else l0 = (DclFloat)1.0 / sqrt(l0);
  DclFloat l1 = ATA.m[1][1]; if(l1 <= 0) l1 = 0; else l1 = (DclFloat)1.0 / sqrt(l1);
  DclFloat l2 = ATA.m[2][2]; if(l2 <= 0) l2 = 0; else l2 = (DclFloat)1.0 / sqrt(l2);

  DclMatrix33 S1;
  S1.multiplyTranspose(U, U, DclVector(l0, l1, l2));

  R.multiply(*this, S1);

  // handle singular case
  DclVector r0, r1, r2;
  r0.x = R.m[0][0]; r0.y = R.m[1][0]; r0.z = R.m[2][0];
  r1.x = R.m[0][1]; r1.y = R.m[1][1]; r1.z = R.m[2][1];
  r2.x = R.m[0][2]; r2.y = R.m[1][2]; r2.z = R.m[2][2];

  int k = 0;
  DclFloat min = fabs(ATA.m[0][0]);
  if(fabs(ATA.m[1][1]) < min) { min = fabs(ATA.m[1][1]); k = 1; }
  if(fabs(ATA.m[2][2]) < min) { min = fabs(ATA.m[2][2]), k = 2; }
  if(k == 0) { r0 = r1; r0 %= r2; }
  if(k == 1) { r1 = r2; r1 %= r0; }
  if(k == 2) { r2 = r0; r2 %= r1; }

  R.m[0][0] = r0.x; R.m[1][0] = r0.y; R.m[2][0] = r0.z;
  R.m[0][1] = r1.x; R.m[1][1] = r1.y; R.m[2][1] = r1.z;
  R.m[0][2] = r2.x; R.m[1][2] = r2.y; R.m[2][2] = r2.z;
}

//****************************************************************************//

//****************************************************************************//

#ifdef DCL_CLOTH_SUPPORT
// NILZ: ADD:
// This is the two-dimensional version of polar decomposition
inline void DclMatrix33::calculatePolarDecomposition()
{
  // calculate the determinant
  DclFloat det = m[0][0] * m[1][1] - m[0][1] * m[1][0];
  
  // calculate the sign of determinant
  DclFloat signOfDet;
  if(det >= 0) signOfDet = 1.0;
  else signOfDet = -1.0;

  // A = R S
  // this is R: (only for two-dimensional matrices)
  DclFloat tmp00 = m[0][0] + signOfDet * m[1][1]; DclFloat tmp10 = m[1][0] - signOfDet * m[0][1];
  DclFloat tmp01 = m[0][1] - signOfDet * m[1][0]; DclFloat tmp11 = m[1][1] + signOfDet * m[0][0];
  
  // make columns unit vectors in order to prevent scaling
  DclFloat ascale = 1;
  ascale /= sqrt(tmp00 * tmp00 + tmp01 * tmp01);

  m[0][0] = ascale * tmp11; m[1][0] = ascale * tmp10;     m[2][0] = 0;
  m[0][1] = ascale * tmp01; m[1][1] = ascale * tmp00;     m[2][1] = 0;
  
  m[0][2] = 0;              m[1][2] = 0;                  m[2][2] = 1; 
}
// NILZ: END:
#endif
