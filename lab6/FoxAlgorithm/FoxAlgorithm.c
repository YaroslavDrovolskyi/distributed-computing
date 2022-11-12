#include <math.h>
#include "FoxAlgorithm.h"
#include "../SerialAlgorithm/SerialAlgorithm.h"

// Creation of two-dimensional grid communicator and
void createGridTopology() {
	int ndims = 2;
	int dims[2] = { gridSize, gridSize};
	int periods[2] = { 1,1 };
	int reorder = 1;

	MPI_Cart_create(MPI_COMM_WORLD, ndims, dims, periods, reorder, &gridCommunicator);
	MPI_Comm_rank(gridCommunicator, &rank);
	
	// using this property: "Row-major numbering is always used for the processes in a Cartesian structure."
	// so (0;0) -> 0, (0;1)->1, (0;2)->2, ...
	upProcessRank = (rank - gridSize >= 0) ? (rank - gridSize) : (rank + (gridSize - 1) * gridSize);
	downProcessRank = (rank + gridSize < numberOfProcesses) ? (rank + gridSize) : (rank - (gridSize - 1) * gridSize);

	int rowIndex = rank / gridSize;
	int firstRankInRow = rowIndex * gridSize;
	int lastRankInRow = rowIndex * gridSize + gridSize - 1;
	leftProcessRank = (rank - 1 >= firstRankInRow) ? (rank - 1) : (lastRankInRow);
	rightProcessRank = (rank + 1 <= lastRankInRow) ? (rank + 1) : (firstRankInRow);

	// for debug
//	printf("\nrank: %d, upRank: %d, rightRank: %d, downRank: %d, leftRank: %d",
//		rank, upProcessRank, rightProcessRank, downProcessRank, leftProcessRank);
}

void setMatrixSize(int size) {
	matrixSize = size;
	gridSize = sqrt((double)numberOfProcesses);
	if (numberOfProcesses != gridSize * gridSize) {
		if (rank == 0) {
			printf("ERROR: number of processes must be a perfect square");
		}
		MPI_Finalize();
		exit(-1);
	}
	else {
		if (matrixSize % gridSize != 0) {
			if (rank == 0) {
				printf("ERROR: size of matrix must be divideble by gridSize(=sqrt(numberOfProcesses))");
			}
			MPI_Finalize();
			exit(-1);
		}
		else {
			blockSize = matrixSize / gridSize;
		}
	}
}


// Function for memory allocation and initialization of matrices’ elements
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

	initialBlockA = (double*)malloc(blockSize * blockSize * sizeof(double));
	blockA = (double*)malloc(blockSize * blockSize * sizeof(double));
	blockB = (double*)malloc(blockSize * blockSize * sizeof(double));
	blockC = (double*)malloc(blockSize * blockSize * sizeof(double));
	initMatrixByValue(blockC, blockSize, blockSize, 0);
}


void freeProcessMemory() {
	if (rank == 0) {
		free(matrixA);
		free(matrixB);
		free(matrixC);
	}
	free(initialBlockA);
	free(blockA);
	free(blockB);
	free(blockC);
}

// Convert matrix into sequence of blocks
void writeMatrixByBlocks(double* matrix, double* block, int matrixSize, int blockSize, int gridSize) {
	int k = 0;
	// iterate over blocks and write them in matrix
	for (int blockI = 0; blockI < gridSize; blockI++) {
		for (int blockJ = 0; blockJ < gridSize; blockJ++) {
			// write one block
			for (int i = blockI * blockSize; i < (blockI + 1) * blockSize; i++) {
				for (int j = blockJ * blockSize; j < (blockJ + 1) * blockSize; j++) {
					block[k] = matrix[i * matrixSize + j];
					k++;
				}
			}
		}
	}
}

// Convert sequance of blocks into matrix
void writeMatrixFromBlocks(double* blocksMatrix, double* resultMatrix, int matrixSize, int blockSize, int gridSize) {
	int k = 0;
	// iterate over blocks and write them in matrix
	for (int blockI = 0; blockI < gridSize; blockI++) {
		for (int blockJ = 0; blockJ < gridSize; blockJ++) {
			// write one block
			for (int i = blockI * blockSize; i < (blockI + 1) * blockSize; i++) {
				for (int j = blockJ * blockSize; j < (blockJ + 1) * blockSize; j++) {
					resultMatrix[i * matrixSize + j] = blocksMatrix[k];
					k++;
				}
			}
		}
	}
}

// perform initial distribution of A's and B's blocks between processes 
void distributeTasks() {
	// send block (i,j) of matrix A to process (i,j)
	double* blocksOfMatrixA = NULL;
	if(rank == 0) {
		blocksOfMatrixA = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		writeMatrixByBlocks(matrixA, blocksOfMatrixA, matrixSize, blockSize, gridSize);
	}
	MPI_Scatter(blocksOfMatrixA, blockSize * blockSize, MPI_DOUBLE, initialBlockA, blockSize * blockSize, MPI_DOUBLE, 0, gridCommunicator);
	if (rank == 0) {
		free(blocksOfMatrixA);
	}
	

	// send block (i,j) of matrix B to process (i,j)
	double* blocksOfMatrixB = NULL;
	if (rank == 0) {
		blocksOfMatrixB = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		writeMatrixByBlocks(matrixB, blocksOfMatrixB, matrixSize, blockSize, gridSize);
	}
	MPI_Scatter(blocksOfMatrixB, blockSize * blockSize, MPI_DOUBLE, blockB, blockSize * blockSize, MPI_DOUBLE, 0, gridCommunicator);
	if (rank == 0) {
		free(blocksOfMatrixB);
	}
	

	// for debug
//	printf("\nrank: %d, initialBlockA:\n", rank);
//	printMatrix(initialBlockA, blockSize);
//	printf("\nrank: %d, blockB:\n", rank);
//	printMatrix(blockB, blockSize);
}

// decide what initialBlockA should be shared over all processes in row, and share block
// On the iteration #0 we should share initialBlockA of diagonal process
// on j-th iteration we should share initialBlockA of [i][(i+j) % gridSize] process
// where i - is index of current row
void passOverRowInitialBlockA(int iteration) {
	int i = rank / gridSize; // index of current row
	int rankOfMainProcessInRow = (i + iteration) % gridSize + (i * gridSize); // starting from
	if (rank == rankOfMainProcessInRow) { // send initBlockA ro all processes in row
		for (int k = i * gridSize; k < (i + 1) * gridSize; k++) {
			if (k == rankOfMainProcessInRow) {
				continue;
			}
			MPI_Send(initialBlockA, blockSize * blockSize, MPI_DOUBLE, k, 0, gridCommunicator);
		}
		for (int m = 0; m < blockSize * blockSize; m++) {
			blockA[m] = initialBlockA[m];
		}
	}
	else {
		MPI_Status status;
		MPI_Recv(blockA, blockSize * blockSize, MPI_DOUBLE, rankOfMainProcessInRow, 0, gridCommunicator, &status);
	}
}


void calculateBlockC() {
	serialMatrixMultiplication(blockA, blockB, blockC, blockSize);
}

// give blockB to up process, and receive blockB from down process
void passBlockB() {
	double* copyBlockB = (double*)malloc(blockSize * blockSize * sizeof(double));
	for (int i = 0; i < blockSize * blockSize; i++) {
		copyBlockB[i] = blockB[i];
	}

	MPI_Status status;
	MPI_Sendrecv(copyBlockB, blockSize * blockSize, MPI_DOUBLE, upProcessRank, 0,
		blockB, blockSize * blockSize, MPI_DOUBLE, downProcessRank, 0, gridCommunicator, &status);
	free(copyBlockB);
}

// merge blockC of all processes into matrixC
void gatherMatrixC() {
	double* matrixByBlocks = NULL;
	if (rank == 0) {
		matrixByBlocks = (double*)malloc(matrixSize * matrixSize * sizeof(double));
	}

	MPI_Gather(blockC, blockSize * blockSize, MPI_DOUBLE,
		matrixByBlocks, blockSize * blockSize, MPI_DOUBLE, 0, gridCommunicator); ////////////////////////////matrixByBlocks

	
	if (rank == 0) {
		writeMatrixFromBlocks(matrixByBlocks, matrixC, matrixSize, blockSize, gridSize);
		free(matrixByBlocks);
	}
}


/*
	Perform Fox algorithm
	Steps:
		- distribute data (process (i,j) must get blocks A(i,j) and B(i,j))
		- do gridSize iterations with following steps:
			- on each row scatter correct initialBlockA
			- for each process calculate blockC = blockA * blockB
			- each process should pass blockB to up process and get blockB from down process
		- gather matrix C
*/
void multiplyMatricesByFoxAlgorithm() {
	distributeTasks();
	for (int i = 0; i < gridSize; i++) {
		passOverRowInitialBlockA(i);
		calculateBlockC(i);
		passBlockB();
		// for debug
//		printf("\nIteration %d, rank: %d, blockA:\n", i, rank);
//		printMatrix(blockA, blockSize);
//		printf("\nIteration %d, rank: %d, blockC:\n", i, rank);
//		printMatrix(blockC, blockSize);
	}
	gatherMatrixC();
}


