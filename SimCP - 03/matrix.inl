/************************************
* matrix.inl
* Inline Functions of the general DclMatrix-class for arbitrary nxm-matrices. 
* Copyright (C) 2005 Markus Becker <mbecker@informatik.uni-freiburg.de>
*************************************/

/***************************************************************/
inline DclMatrix::DclMatrix()
{
  column = row = 0;
  m = NULL; 
}

/***************************************************************/
inline DclMatrix::DclMatrix(int rowNr, int colNr)
{
  if(rowNr <= 0 || colNr <= 0)
  {
    column = row = 0;
    m = NULL;
    return;
  }

  row      = rowNr;
  column   = colNr;
  m    = new DclFloat*[row];
  m[0] = new DclFloat [row * column];
  for(int i = 1; i < row; i++)
    m[i] = m[i - 1] + column;

  //initialize the values of the matrix with zero
  for(int i = 0; i < row; i++)
    for(int j = 0; j < column; j++)
      m[i][j] = 0;
}

/***************************************************************/
inline DclMatrix::DclMatrix(const DclMatrix &M)
{
  row      = M.row;
  column   = M.column;
  m    = new DclFloat*[row];
  m[0] = new DclFloat [row * column];

  for(int i = 1; i < row; i++)
    m[i] = m[i-1] + column;

  for(int i = 0; i < row; i++)
    for(int j = 0; j < column; j++)
      m[i][j] = M.m[i][j];
}

/***************************************************************/
inline DclMatrix::~DclMatrix()
{
  if(m)
    delete[] m[0];
  delete[] m;
}

/***************************************************************/
//TODO use memset to copy the zero!
inline void DclMatrix::clear()
{
  int i,j;
  for(i = 0; i < row; i++)
    for(j = 0; j < column; j++)
      m[i][j] = 0;
}

/***************************************************************/
inline DclFloat DclMatrix::determinant()
{
  if((row != column) || (row < 1)) 
    return 0;
  if(row == 1)
    return m[0][0];
  if(row ==2)
    return (m[0][0]*m[1][1] - m[1][0]*m[0][1]);	

  DclFloat determinant = 0; 

  int rowId;
  for(rowId = 0; rowId < row; rowId++)
  {
    if(m[rowId][0] == 0) 
      continue;
    determinant += m[rowId][0] * minorMatrixdet(rowId,0);
    //determinant += std::pow(-1,rowId+1)* m[rowId][0] * minorMatrixdet(rowId,0);
  }
  return determinant;
}	

/***************************************************************/
inline int DclMatrix::getcolumn() const
{
  return column;
}

/***************************************************************/
inline int DclMatrix::getrow() const
{
  return row;
}
/***************************************************************/
inline bool DclMatrix::inverse()
{
  if (row != column)
    return true;
  if(row == 1)
    m[0][0]=(m[0][0]==0 ? 0 : m[0][0]=(1/m[0][0]));
  if(row == 2)
  {
    DclFloat det = determinant();
    if (det==0)
      return true;
    DclFloat temp;
    temp = m[1][1];
    m[1][1] = m[0][0]*det;
    m[0][0] = temp*det;
    m[0][1] = -m[0][1]*det;
    m[1][0] = -m[1][0]*det;
    return false;
  }	
  DclMatrix temp(row,column); 
  int rowId, colId; 
  for (rowId = 0; rowId < row; rowId++)
    for (colId = 0; colId < column; colId++)
      temp.m[rowId][colId] = minorMatrixdet(colId,rowId); //row and colId are switched since one has to take the transposed!

  temp *= 1.0/determinant(); 

  (*this) = temp; 

  return true;	 
}
/***************************************************************/
inline DclFloat DclMatrix::minorMatrixdet(const int rowId, const int colId)
{
  DclMatrix temp(row-1,column-1);
  int rowNr=0,colNr=0;
  for(int i = 0; i < row; i++,colNr=0)
  {
    if(i == rowId)
    {
      rowNr++;	
      continue; 
    }
    for(int j = 0; j < column; j++)
    {
      if(j == colId)
      {
        colNr++;
        continue;
      }
      temp.m[i-rowNr][j-colNr] = m[i][j];
    }
  }	
  DclFloat signf = (rowId + colId)%2 == 0 ? 1 : -1; 
  return signf*temp.determinant();
}

/***************************************************************/
inline void DclMatrix::multiply(const DclMatrix& a1, const DclMatrix& a2)
{
  //check if the sizes match
  if (a1.column != a2.row)
    return;
  //check if the matrix instance has the right size, else adjust it. 
  if (row != a1.row || column != a2.column)
  {
    if (m)
      delete[] m[0];
    delete[] m;
    row      = a1.row;
    column   = a2.column;
    m    = new DclFloat*[row];
    m[0] = new DclFloat [row * column];
    for(int i = 1; i < row; i++)
      m[i] = m[i - 1] + column;
  }
  DclFloat temp; 
  for(int i = 0 ; i < a1.row ; i++)
    for(int j = 0 ; j < a2.column ; j++)
    {
      temp=0;
      for(int k = 0 ; k < a1.column ; k++)
        temp+=a1.m[i][k]*a2.m[k][j];
      m[i][j]=temp;
    }
}

/***************************************************************/
inline DclMatrix DclMatrix::subMat(int minRow, int maxRow, int minCol, int maxCol)
{
  //TODO exception if indices are out of bound
  DclMatrix temp(maxRow - minRow + 1, maxCol - minCol + 1); 

  for (int i = minRow; i <= maxRow; i++)
    for (int j = minCol; j <= maxCol; j++)
      temp.m[i-minRow][j-minCol] = m[i][j]; 

  return temp; 
}


/***************************************************************/
inline void DclMatrix::transpose()
{
  if(row == column)
  {	
    DclFloat temp;
    int i,j;
    for(j = 0; j < column; j++)
    {
      for(i = 0; i < j; i++)
      {
        temp = m[i][j]; 
        m[i][j] = m[j][i];
        m[j][i] = temp;
      }
    }
  }
  else
  {
    DclMatrix temp(column,row); 
    int rowId, colId; 
    for(rowId = 0; rowId < row; rowId++)
    {
      for (colId = 0; colId < column; colId++)
      {
        temp.m[colId][rowId] = m[rowId][colId];
      }
    }		
    (*this)=temp;
  }
}		

/***************************************************************/
//TODO Wie passiert event-handling? m zurückgeben oder leere matrix
DclMatrix DclMatrix::operator-(const DclMatrix &M) const
{
  if(M.row != row || M.column != column)
  {
    return (*this); 
  }

  DclMatrix Result(row, column);
  for(int i = 0; i < row; i++)
    for(int j = 0; j < column; j++)
      Result.m[i][j] = m[i][j] - M.m[i][j];

  return Result;   
}

/***************************************************************/
DclMatrix DclMatrix::operator+(const DclMatrix &M) const
{
  if(M.row != row || M.column != column)
  {
    return (*this);
  }

  DclMatrix Result(row, column);
  for(int i = 0; i < row; i++)
    for(int j = 0; j < column; j++)
      Result.m[i][j] = m[i][j] + M.m[i][j];

  return Result;   
}

/***************************************************************/
DclMatrix DclMatrix::operator*(const DclMatrix &M) const
{
  DclFloat temp;
  if(column !=M.row) 
    return (*this); 

  DclMatrix Result(row,M.column);
  for(int i=0;i<row;i++)
    for(int j=0;j<M.column;j++)
    {
      temp=0;
      for(int k=0;k<column;k++)
        temp+=m[i][k]*M.m[k][j];
      Result(i,j)=temp;
    }
    return Result;
}

inline DclMatrix& DclMatrix::operator*=(const DclFloat d)
{
  for (int i = 0; i < row; i++)
    for (int j = 0; j < column; j++)
      m[i][j] *= d; 

  return (*this); 
}

/***************************************************************/
DclMatrix& DclMatrix::operator=(const DclMatrix &M)
{
  if(this == &M)
    return *this;

  if (row != M.row || column != M.column)
  {
    if(m)
      delete[] m[0];
    delete[] m;
    row      = M.row;
    column   = M.column;
    m    = new DclFloat*[row];
    m[0] = new DclFloat [row * column];
    for(int i = 1; i < row; i++)
      m[i] = m[i-1] + column;
  }

  for(int i = 0; i < row; i++)
    for(int j = 0; j < column; j++)
      m[i][j] = M.m[i][j];

  return *this;
}

/***************************************************************/
inline DclMatrix DclMatrix::operator*(const DclFloat d) const
{
  DclMatrix Result(row, column);
  for(int i = 0; i < row; i++)
    for(int j = 0; j < column; j++)
      Result.m[i][j] = m[i][j] * d;

  return Result;
}

/***************************************************************/
//TODO return for non-valid arguments?
inline DclFloat& DclMatrix::operator() (int rowId,int colId)
{
  return m[rowId][colId];
}		

/***************************************************************/
inline const DclFloat& DclMatrix::operator()(int rowId, int colId) const
{
  return m[rowId][colId];
}		


/***************************************************************/

inline DclMatrix operator*(const DclFloat d, const DclMatrix &M)
{
  return M * d;
}

/***************************************************************/

inline std::ostream& operator<< (std::ostream& os, const DclMatrix& M)
{
  int i,j;
  for (i = 0; i < M.getrow(); i++)
  {
    for (j = 0; j < M.getcolumn(); j++)
    {
      os <<M.m[i][j] <<" ";
    }
    os <<std::endl; 
  }
  return os; 
}