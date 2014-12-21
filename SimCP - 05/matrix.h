/************************************
* matrix.h
* Copyright (C) 2005 Markus Becker <mbecker@informatik.uni-freiburg.de>
*************************************/


#ifndef DCL_MATRIX_H
#define DCL_MATRIX_H 

#include <iostream>

///General matrix-class for DclFloat-matrices of arbitrary size. 
class DclMatrix
{
  //member variables
private:
  int row;
  int column;

public:  
  DclFloat** m;

  //constructors / destructors  
public:
  ///Default constructor. 
  inline DclMatrix();

  ///Constructs a matrix instance with 'rowNr' rows and 'colNr' columns and initializes them with zero.
  inline DclMatrix(int rowNr, int colNr);

  ///Constructs a matrix instance from the matrix M.
  inline DclMatrix(const DclMatrix& M);

  ///Destructor of the matrix instance. 
  inline ~DclMatrix();

  //public member functions  
public:  

  ///Set all values of the matrix to zero. 
  inline void clear();

  ///Returns the determinant of the matrix. Just the standard algortihm and therefore not very fast.
  virtual inline DclFloat determinant();

  ///Get the number of columns of the matrix.
  inline int getcolumn() const;

  ///Get the number of rows of the matrix.
  inline int getrow() const;

  ///Inverts the matrix. Only implemented for size <=2. For size 3 use class DclMatrix33::public DclMatrix
  virtual inline bool inverse();

  //MB: I guess the following function is only fast if the matrix-instance already has the right size. 
  ///Multiplies a1*a2 into the matrix instance if a1.column = a2.row, else do nothing
  virtual inline void multiply(const DclMatrix& a1, const DclMatrix& a2);

  inline DclMatrix subMat(int,int,int,int); 

  ///Transpose the matrix. 
  inline void transpose();

  ///This operator substracts a matrix from the matrix instance and returns the result. The matrix instance is unchanged. 
  inline DclMatrix operator-(const DclMatrix&) const;

  //This operator adds a matrix to the matrix instance and returns the result. The matrix instance is unchanged. 
  virtual inline DclMatrix operator+(const DclMatrix&) const;

  ///This operator multiplies a matrix with the matrix instance and returns the result. The matrix instance is unchanged. 
  virtual inline DclMatrix operator*(const DclMatrix&) const;

  ///This operator equates the matrix instance with another matrix and returns a reference to the matrix-instance for concatenation.
  inline DclMatrix& operator=(const DclMatrix&);

  ///This operator multiplies all values of the matrix with a constant and return the result. The matrix instance is unchanged. 
  inline DclMatrix operator*(const DclFloat) const;

  inline DclMatrix& operator*=(const DclFloat); 

  //inline VECTOR operator*(const VECTOR&) const; //TODO implement vector multiplication with general vector

  ///This operator gives access to the entries of the matrix instance.
  inline DclFloat& operator()(int, int);

  ///This operator gives access to the entries of the matrix instance.
  inline const DclFloat& operator()(int, int) const;



  ///private member functions
private: 
  ///Returns the minor of the matrix instance with discarded row 'rowId' and discarded column 'colId'. 
  inline DclFloat minorMatrixdet(const int rowId, const int colId); 
public:	
  ///Multiplies the matrix instance from left with constant d and returns the result. The matrix instance is unchanged. 
  inline friend DclMatrix operator*(const DclFloat d, const DclMatrix&);

  inline friend std::ostream& operator<< (std::ostream&, const DclMatrix&); 
};


#include "matrix.inl"

#endif //DCL_MATRIX_H

