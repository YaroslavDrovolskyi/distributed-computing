#include <stdio.h>
#include <string.h>
#include <time.h>
#include <math.h>
#include "mpi.h"
#include "MatrixMultiplication.h"






double* matrixA;
double* matrixB;
double* matrixC;
double* rowA;
double* columnB;
double* rowC;
int matrixSize, blockSize;
int rank, numberOfProcessors;
MPI_Comm topologyCommunicator;

void initProcess(double** matrixA, double** matrixB, double** matrixC,
	double** rowA, double** columnB, double** rowC, int matrixSize, int blockSize);

void createTopology(MPI_Comm* topologyCommunicator);
void distributeTasks();
void calculateBlockC(int iteration);
void passColumnB();
void gatherMatrixC();
void writeMatrixByColumns(double* matrix, double* matrixByColumns);
void freeProcessMemory();

int main(int* argc, char** argv){
	MPI_Init(&argc, &argv);
	MPI_Comm_size(MPI_COMM_WORLD, &numberOfProcessors);
//	MPI_Comm_rank(MPI_COMM_WORLD, &rank);

	matrixSize = 4; // must be dividable by p
	blockSize = matrixSize / numberOfProcessors; // must be integer number - need control



	createTopology(&topologyCommunicator);

	initProcess(&matrixA, &matrixB, &matrixC, &rowA, &columnB, &rowC, matrixSize, blockSize);

	if (rank == 0) {
		printf("A:\n");
		printMatrix(matrixA, matrixSize);
		printf("B:\n");
		printMatrix(matrixB, matrixSize);
	}

	
	distributeTasks();
	for (int i = 0; i < numberOfProcessors; i++) {
		calculateBlockC(i);
		passColumnB();
	}
	gatherMatrixC();

	if (rank == 0) {
		printf("\nC:\n");
		printMatrix(matrixC, matrixSize);


		// print serial matrix multiplication result
		double* serialResult = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		initMatrixByValue(serialResult, matrixSize, matrixSize, 0);
		serialMatrixMultiplication(matrixA, matrixB, serialResult, matrixSize);
		printf("\nC (serial):\n");
		printMatrix(serialResult, matrixSize);

		printf("\nMatrices equal: %d\n", isMatricesEqual(matrixC, serialResult, matrixSize, 0));
		free(serialResult);
	}

	freeProcessMemory();
	


	
	MPI_Finalize();
	return 0;
}



void initProcess(double** matrixA, double** matrixB, double** matrixC,
	double** rowA, double** columnB, double** rowC, int matrixSize, int blockSize) {

	/*
		Allocating dynamic memory for blocks and matrices,
		need to make free(pMatrix) somewhere
	*/
	if (rank == 0) {
		*matrixA = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		*matrixB = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		*matrixC = (double*)malloc(matrixSize * matrixSize * sizeof(double));
	}

	*rowA = (double*)malloc(blockSize * matrixSize * sizeof(double));
	*columnB = (double*)malloc(matrixSize * blockSize * sizeof(double));
	*rowC = (double*)malloc(blockSize * matrixSize * sizeof(double));


	/*
		Main processor initialize items of matrices and blocks
	*/
	srand((unsigned)clock());
	if (rank == 0) {
		initMatrixByRandom(*matrixA, matrixSize, matrixSize);
		initMatrixByRandom(*matrixB, matrixSize, matrixSize);
		initMatrixByValue(*matrixC, matrixSize, matrixSize, 0);
	}
	initMatrixByValue(*rowC, blockSize, matrixSize, 0);
}

void freeProcessMemory() {
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	if (rank == 0) {
		free(matrixA);
		free(matrixB);
		free(matrixC);
	}
	free(rowA);
	free(columnB);
	free(rowC);
}

void createTopology(MPI_Comm* topologyCommunicator) {
	int ndims = 1;
	int dims[1] = { numberOfProcessors };
	int periods[1] = { 1 };
	int reorder = 1;

	MPI_Cart_create(MPI_COMM_WORLD, ndims, dims, periods, reorder, topologyCommunicator);
	MPI_Comm_rank(*topologyCommunicator, &rank); // get rank of process in this topology
	int coordinates[1]; // rank == coordinates[0] because ranks distributed by rows
	MPI_Cart_coords(*topologyCommunicator, rank, ndims, coordinates);
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
	int numberOfBlocksInRow = numberOfProcessors;
	int offset = ((rank + iteration) % numberOfBlocksInRow)* blockSize;

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
	double* copyColumnB= (double*)malloc(blockSize * matrixSize * sizeof(double));
	for (int i = 0; i < blockSize * matrixSize; i++) {
		copyColumnB[i] = columnB[i];
	}
//	memcopy(copyColumnB, columnB, blockSize * matrixSize);
	int nextProcessRank = (rank + 1) % numberOfProcessors;
	int prevProcessRank = (rank == 0) ? (numberOfProcessors - 1) : (rank - 1);
	MPI_Status status;
	MPI_Sendrecv(copyColumnB, matrixSize * blockSize, MPI_DOUBLE, prevProcessRank, 0,
				columnB, matrixSize * blockSize, MPI_DOUBLE, nextProcessRank, 0, topologyCommunicator, &status);
	free(copyColumnB);
}