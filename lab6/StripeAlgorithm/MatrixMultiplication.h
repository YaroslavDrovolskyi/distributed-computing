#ifndef MATRIX_MULTIPLICATION_H_
#define MATRIX_MULTIPLICATION_H_

// Function for memory allocation and initialization of matrix elements
void ProcessInitialization(double** pAMatrix, double** pBMatrix,
	double** pCMatrix, int* Size);

void ProcessTermination(double* pAMatrix, double* pBMatrix,
	double* pCMatrix);

void serialMatrixMultiplication(double* pAMatrix, double* pBMatrix, double* pCMatrix, int Size);
void testSerialMultiplication();

void initMatrixByValue(double* matrix, int nRow, int nCol, int  value);
void initMatrixByRandom(double* matrix, int nRow, int nCol);
void printMatrix(double* matrix, int size);
void printRectangularMatrix(double* matrix, int nRow, int nCol);

int isMatricesEqual(double* m1, double* m2, int size, double eps);








#endif // #ifndef MATRIX_MULTIPLICATION_H_