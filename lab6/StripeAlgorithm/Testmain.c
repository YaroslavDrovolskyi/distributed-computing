#include <stdio.h>
#include <time.h>
#include <math.h>
#include "mpi.h"
#include "MatrixMultiplication.h"
#include "FoxMultiplication.h"



int main�(int* argc, char** argv) {
	// [i][j] == [i * size + j]

//	testSerialMultiplication();

//	getch();

	double* pAMatrix;
	double* pBMatrix;
	double* pCMatrix;
	int size;
	int BlockSize; // Sizes of matrix blocks on current process
	double* pMatrixAblock; // Initial block of matrix A on current process
	double* pAblock; // Current block of matrix A on current process
	double* pBblock; // Current block of matrix B on current process
	double* pCblock; // Block of result matrix C on current process
	double start, finish, duration;

	MPI_Init(argc, &argv);
	MPI_Comm_size(MPI_COMM_WORLD, &numberOfProcesses);
	MPI_Comm_rank(MPI_COMM_WORLD, &procRank);

//	printf("Number of processes %d", numberOfProcesses); ///////////////////////////////////

	
	
	// check condition about p is full square
	gridSize = sqrt((double)numberOfProcesses);
	// printf("gridSize = %d", gridSize);
	if (numberOfProcesses != gridSize * gridSize) {
		if (procRank == 0) {
			printf("Number of processes must be full square");
		}
	}
	else {
		if (procRank == 0) {
			printf("Parallel matrix multiplication program\n");
		}
		
		size = 3; // set size of matrices

		// Create grid communicators
		CreateGridCommunicators();

		// Memory allocation and initialization of matrix elements
		int ok = ProcessInitializationForParallel(&pAMatrix, &pBMatrix, &pCMatrix, &pAblock, &pBblock,
			&pCblock, &pMatrixAblock, &size, &BlockSize);
		
		if (ok == 1) {
			if (procRank == 0) {
				printf("Size of matrices must be divisible by the grid size (sqrt processed number)!");
			}
			MPI_Finalize();
			return 0;
		}

		if (procRank == 0) {
			printf("Initial matrix A \n");
			printMatrix(pAMatrix, size);
			printf("Initial matrix B \n");
			printMatrix(pBMatrix, size);
		}

		start = MPI_Wtime();

		// Data distribution among the processes
		DataDistribution(pAMatrix, pBMatrix, pMatrixAblock, pBblock, size,
			BlockSize);

		// Execution of Fox method
		ParallelResultCalculation(pAblock, pMatrixAblock, pBblock, pCblock, BlockSize);

		// collect result
		ResultCollection(pCMatrix, pCblock, size, BlockSize);
		finish = MPI_Wtime();
		duration = finish - start;

		if (procRank == 0) {
			printf("Result matrix \n");
			printMatrix(pCMatrix, size);
			printf("Time of execution = % f s\n\n", duration);
		}

		// check if result is correct
		if (procRank == 0) {
			printf("Correct result matrix:\n");
			double* pSerialResult = (double*)malloc(size * size * sizeof(double));
			initMatrixByValue(pSerialResult, size, size, 0);
			serialMatrixMultiplication(pAMatrix, pBMatrix, pSerialResult, size);
			printMatrix(pSerialResult, size);
			free(pSerialResult);
			printf("Result is correct: %d",
				TestMultiplication(pAMatrix, pBMatrix, pCMatrix, size));
		}
		
		// Process termination (free memory)
		ProcessTerminationForParallel(pAMatrix, pBMatrix, pCMatrix, pAblock, pBblock, pCblock, pMatrixAblock);
		
	}
	

	MPI_Finalize();
	return 0;
}