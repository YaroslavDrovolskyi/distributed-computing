#include <stdio.h>
#include <string.h>
#include <time.h>
#include <math.h>
#include <assert.h>

#include "StripeAlgorithm.h"

void multiplyMatricesByStripeAlgorithm() {
	distributeTasks();
	for (int i = 0; i < numberOfProcesses; i++) {
		calculateBlockC(i);
		passColumnB();
	}
	gatherMatrixC();
}

// size must be dividable by number of processors
void setMatrixSize(int size) {
	if (size % numberOfProcesses != 0) {
		if (rank == 0) {
			printf("ERROR: size of matrix must perfectly divide by number of processors");
		}
		MPI_Finalize();
		exit(-1);
	}
	matrixSize = size;
	blockSize = matrixSize / numberOfProcesses; // must be integer number - need control
}



void initProcessMemory() {

	/*
		Allocating dynamic memory for blocks and matrices, need to free() it after using
		Main processor initialize items of matrices and blocks
	*/
	if (rank == 0) {
		matrixA = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		matrixB = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		matrixC = (double*)malloc(matrixSize * matrixSize * sizeof(double));

		initMatrixByRandom(matrixA, matrixSize, matrixSize);
		initMatrixByRandom(matrixB, matrixSize, matrixSize);
		initMatrixByValue(matrixC, matrixSize, matrixSize, 0);
	}

	rowA = (double*)malloc(blockSize * matrixSize * sizeof(double));
	columnB = (double*)malloc(matrixSize * blockSize * sizeof(double));
	rowC = (double*)malloc(blockSize * matrixSize * sizeof(double));
	initMatrixByValue(rowC, blockSize, matrixSize, 0);
}

void freeProcessMemory() {
	if (rank == 0) {
		free(matrixA);
		free(matrixB);
		free(matrixC);
	}
	free(rowA);
	free(columnB);
	free(rowC);
}

void createRingTopology(MPI_Comm* topologyCommunicator) {
	int ndims = 1;
	int dims[1] = { numberOfProcesses };
	int periods[1] = { 1 };
	int reorder = 1;

	MPI_Cart_create(MPI_COMM_WORLD, ndims, dims, periods, reorder, topologyCommunicator);
	//	MPI_Comm_rank(*topologyCommunicator, &rank); // get rank of process in this topology
	//	int coordinates[1]; // rank == coordinates[0] because ranks distributed by rows
	//	MPI_Cart_coords(*topologyCommunicator, rank, ndims, coordinates);
	//	printf("Process with oldRank: %d, new rank: %d, coordinate: %d", globalRank, rank, coordinates[0]);
}

void distributeTasks() {
	// send row of matrix A
	MPI_Scatter(matrixA, blockSize * matrixSize, MPI_DOUBLE, rowA, blockSize * matrixSize, MPI_DOUBLE, 0, topologyCommunicator);

	// send column of matrix B
	double* matrixByCols = NULL;
	if (rank == 0) {
		matrixByCols = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		writeMatrixByColumns(matrixB, matrixByCols);
	}
	MPI_Scatter(matrixByCols, matrixSize * blockSize, MPI_DOUBLE, columnB, matrixSize * blockSize, MPI_DOUBLE, 0, topologyCommunicator);
	if (rank == 0) {
		free(matrixByCols);
	}
}

void calculateBlockC(int iteration) {
	int numberOfBlocksInRow = numberOfProcesses;
	int offset = ((rank + iteration) % numberOfBlocksInRow) * blockSize;

	//	printf("\nrank = %d, iteration = %d rowA:\n", rank, iteration);
	//	printRectangularMatrix(rowA, blockSize, matrixSize);
	//	printf("rank = %d, iteration = %d columnB:\n", rank, iteration);
	//	printRectangularMatrix(columnB, matrixSize, blockSize);

	for (int i = 0; i < blockSize; i++) {
		for (int j = 0; j < blockSize; j++) {
			for (int k = 0; k < matrixSize; k++) {
				rowC[i * matrixSize + j + offset] += rowA[i * matrixSize + k] * columnB[k * blockSize + j];
			}
		}
	}

	//	printf("rank = %d, iteration = %d rowC:\n", rank, iteration);
	//	printRectangularMatrix(rowC, blockSize, matrixSize);
}

void gatherMatrixC() {
	MPI_Gather(rowC, blockSize * matrixSize, MPI_DOUBLE,
		matrixC, blockSize * matrixSize, MPI_DOUBLE, 0, MPI_COMM_WORLD);
}

/*
	Writes matrix columns in array to read them as a rows
*/
void writeMatrixByColumns(double* matrix, double* matrixByColumns) {
	int k = 0;
	for (int i = 0; i < matrixSize; i++) {
		for (int j = 0; j < matrixSize; j++) {
			int blockIndex = j / blockSize;
			int localJ = j % blockSize;
			int offset = matrixSize * blockSize * blockIndex;
			matrixByColumns[i * blockSize + localJ + offset] = matrix[i * matrixSize + j];
			k++;
		}
	}
}

void passColumnB() {
	double* copyColumnB = (double*)malloc(blockSize * matrixSize * sizeof(double));
	for (int i = 0; i < blockSize * matrixSize; i++) {
		copyColumnB[i] = columnB[i];
	}
	//	memcopy(copyColumnB, columnB, blockSize * matrixSize);
	int nextProcessRank = (rank + 1) % numberOfProcesses;
	int prevProcessRank = (rank == 0) ? (numberOfProcesses - 1) : (rank - 1);
	MPI_Status status;
	MPI_Sendrecv(copyColumnB, matrixSize * blockSize, MPI_DOUBLE, prevProcessRank, 0,
		columnB, matrixSize * blockSize, MPI_DOUBLE, nextProcessRank, 0, topologyCommunicator, &status);
	free(copyColumnB);
}