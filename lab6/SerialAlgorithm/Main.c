#include <stdio.h>
#include <time.h>

#include "SerialAlgorithm.h"


void initProcessMemory(double** matrixA, double** matrixB, double** matrixC, int size);
void freeProcessMemory(double* matrixA, double* matrixB, double* matrixC);
void test(int numberOfIterations, int matrixSize);

int main(int argc, char** argv) {
	// print arguments
	printf("SERIAL ALGORITHM\n");
	printf("=== Arguments ===\n");
	for (int i = 1; i < argc; i++) {
		printf("[%d]\t %s \n", i, argv[i]);
	}
	printf("=================\n");

	srand((unsigned)clock());

	if (strcmp("benchmark", argv[1]) == 0) {
		printf("Benchmark started!\n");
		// need to implement benchmark
	}
	else if (strcmp("test", argv[1]) == 0) {
		test(atoi(argv[2]), atoi(argv[3]));
	}

	return 0;
}

void test(int numberOfIterations, int matrixSize) {
	double* matrixA;
	double* matrixB;
	double* matrixC;
	double overallTime = 0;
	
	for (int i = 0; i < numberOfIterations; i++) {
		struct timespec start, finish;
		initProcessMemory(&matrixA, &matrixB, &matrixC, matrixSize);
		timespec_get(&start, TIME_UTC);
		multiplyMatricesBySerialAlgorithm(matrixA, matrixB, matrixC, matrixSize);
		timespec_get(&finish, TIME_UTC);
		double duration = (finish.tv_sec - start.tv_sec) + 
			(double)(finish.tv_nsec - start.tv_nsec) / 1e9; // duration in seconds
		overallTime += duration;
		printf("test %d, time: %f s\n", i + 1, duration);
		freeProcessMemory(matrixA, matrixB, matrixC);
	}
	if (numberOfIterations > 0) {
		printf("AVERAGE TIME: %f s\n", overallTime / numberOfIterations);
	}
}

// Allocate memory and initialize elements of matrixC by zeros
void initProcessMemory(double** matrixA, double** matrixB, double** matrixC, int size) {
	/*
		Allocating dynamic memory for matrices,
		need to make free(pMatrix) after usage
	*/
	*matrixA = (double*)malloc(size * size * sizeof(double));
	*matrixB = (double*)malloc(size * size * sizeof(double));
	*matrixC = (double*)malloc(size * size * sizeof(double));

	/*
		Initialize matrices' items
	*/
	initMatrixByRandom(*matrixA, size, size);
	initMatrixByRandom(*matrixB, size, size);
	initMatrixByValue(*matrixC, size, size, 0);
}

void freeProcessMemory(double* matrixA, double* matrixB, double* matrixC) {
	free(matrixA);
	free(matrixB);
	free(matrixC);
}