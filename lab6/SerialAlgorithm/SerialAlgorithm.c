#include <math.h>
#include <stdio.h>
#include "SerialAlgorithm.h"



void initMatrixByValue(double* matrix, int nRow, int nCol, int value) {
	for (size_t i = 0; i < nRow; i++) {
		for (size_t j = 0; j < nCol; j++) {
			matrix[i * nCol + j] = value;
		}
	}
}

void initMatrixByRandom(double* matrix, int nRow, int nCol) {
	int maxValue = 100;
	if (nRow * nCol > 50000) {
		maxValue = 100000;
	}
	else if (nRow * nCol > 1000000) {
		maxValue = 1000001;
	}

	for (size_t i = 0; i < nRow; i++) {
		for (size_t j = 0; j < nCol; j++) {
			matrix[i * nCol + j] = rand() % maxValue;
		}
	}
}

void printMatrix(double* matrix, int size) {
	for (size_t i = 0; i < size; i++) {
		for (size_t j = 0; j < size; j++) {
			printf("%f \t", matrix[i * size + j]);
		}
		printf("\n");
	}
}

void printRectangularMatrix(double* matrix, int nRow, int nCol) {
	for (size_t i = 0; i < nRow; i++) {
		for (size_t j = 0; j < nCol; j++) {
			printf("%f \t", matrix[i * nCol + j]);
		}
		printf("\n");
	}
}

int isMatricesEqual(double* m1, double* m2, int size, double eps) {
	for (int i = 0; i < size; i++) {
		if (fabs(m1[i] - m2[i]) > eps) {
			return 0;
		}
	}
	return 1;
}

void multiplyMatricesBySerialAlgorithm(double* pAMatrix, double* pBMatrix, double* pCMatrix, int Size) {
	int i, j, k;
	for (i = 0; i < Size; i++) {
		for (j = 0; j < Size; j++) {
			for (k = 0; k < Size; k++) {
				pCMatrix[i * Size + j] += pAMatrix[i * Size + k] * pBMatrix[k * Size + j];
			}
		}
	}
}