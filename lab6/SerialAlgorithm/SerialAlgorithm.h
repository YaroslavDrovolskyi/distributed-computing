#pragma once

void multiplyMatricesBySerialAlgorithm(double* pAMatrix, double* pBMatrix, double* pCMatrix, int Size);
void initMatrixByValue(double* matrix, int nRow, int nCol, int  value);
void initMatrixByRandom(double* matrix, int nRow, int nCol);
void printMatrix(double* matrix, int size);
void printRectangularMatrix(double* matrix, int nRow, int nCol);
int isMatricesEqual(double* m1, double* m2, int size, double eps);