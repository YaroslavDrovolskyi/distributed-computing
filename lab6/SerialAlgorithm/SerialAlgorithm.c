#include <stdio.h>
#include <time.h>
#include <math.h>

#include "SerialAlgorithm.h"


// Function for memory allocation and initialization of matrices’ elements
void ProcessInitialization(double** pAMatrix, double** pBMatrix,
	double** pCMatrix, int* Size) {

	/*
		Getting size of matrices from user

	do {
		printf("\nEnter size of matricies: ");
		scanf_s("%d", Size);
		printf("\nChosen matricies size = %d\n", *Size);
		if (*Size <= 0)
			printf("\nSize of objects must be greater than 0!\n");
	} while (*Size <= 0);
	*/

	/*
		Allocating dynamic memory for matrices,
		need to make free(pMatrix) somewhere
	*/
	* pAMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));
	*pBMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));
	*pCMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));

	/*
		Initialize matrices' items
	*/
	srand((unsigned)clock());
	initMatrixByRandom(*pAMatrix, *Size, *Size);
	initMatrixByRandom(*pBMatrix, *Size, *Size);
	initMatrixByValue(*pCMatrix, *Size, *Size, 0);
}



void ProcessTermination(double* pAMatrix, double* pBMatrix,
	double* pCMatrix) {
	free(pAMatrix);
	free(pBMatrix);
	free(pCMatrix);
}


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


void serialMatrixMultiplication(double* pAMatrix, double* pBMatrix, double* pCMatrix, int Size) {
	int i, j, k;
	for (i = 0; i < Size; i++) {
		for (j = 0; j < Size; j++) {
			for (k = 0; k < Size; k++) {
				pCMatrix[i * Size + j] += pAMatrix[i * Size + k] * pBMatrix[k * Size + j];
			}
		}
	}
}

void testSerialMultiplication() {
	const int SIZE = 8;
	int sizesOfMatrices[8] = { 10, 100, 500, 1000, 1500, 2000, 2500, 3000 };
	for (int i = 0; i < SIZE; i++) {
		double* pAMatrix;
		double* pBMatrix;
		double* pCMatrix;
		int size;
		time_t start, finish;
		double duration;

		size = sizesOfMatrices[i];
		ProcessInitialization(&pAMatrix, &pBMatrix, &pCMatrix, &size);



		start = clock();
		serialMatrixMultiplication(pAMatrix, pBMatrix, pCMatrix, size);
		finish = clock();
		duration = (finish - start) / (double)(CLOCKS_PER_SEC); // duration in seconds
		ProcessTermination(pAMatrix, pBMatrix, pCMatrix);

		printf("n = %d,\t time = %f s\n", sizesOfMatrices[i], duration);

	}
}



