#include <stdio.h>
#include <time.h>

#include "MatrixMultiplication.h"


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
	*pAMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));
	*pBMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));
	*pCMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));

	/*
		Initialize matrices' items
	*/
	srand((unsigned)clock());
	initMatrixByRandom(*pAMatrix, *Size);
	initMatrixByRandom(*pBMatrix, *Size);
	initMatrixByValue(*pCMatrix, *Size, 0);
}



void ProcessTermination(double* pAMatrix, double* pBMatrix,
	double* pCMatrix) {
	free(pAMatrix);
	free(pBMatrix);
	free(pCMatrix);
}


void initMatrixByValue(double* matrix, int size, int value) {
	for (size_t i = 0; i < size; i++) {
		for (size_t j = 0; j < size; j++) {
			matrix[i * size + j] = value;
		}
	}
}

void initMatrixByRandom(double* matrix, int size) {
	for (size_t i = 0; i < size; i++) {
		for (size_t j = 0; j < size; j++) {
			matrix[i * size + j] = rand() / (double)1000;
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

void testSerialMultiplication(){
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



