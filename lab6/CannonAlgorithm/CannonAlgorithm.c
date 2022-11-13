#include <math.h>
#include "CannonAlgorithm.h"
#include "../SerialAlgorithm/SerialAlgorithm.h"

// Create two-dimensional grid topology, and init communicator
void createGridTopology() {
	int ndims = 2;
	int dims[2] = { gridSize, gridSize };
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


// Allocate and initialize memory for matrices according to algorithm specific
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
	free(blockA);
	free(blockB);
	free(blockC);
}

// Convert matrix into sequence of blocks
void writeMatrixByBlocks(double* matrix, double* matrixByBlocks, int matrixSize, int blockSize, int gridSize) {
	int k = 0;
	// iterate over blocks and write them in matrix
	for (int blockI = 0; blockI < gridSize; blockI++) {
		for (int blockJ = 0; blockJ < gridSize; blockJ++) {
			// write one block
			for (int i = blockI * blockSize; i < (blockI + 1) * blockSize; i++) {
				for (int j = blockJ * blockSize; j < (blockJ + 1) * blockSize; j++) {
					matrixByBlocks[k] = matrix[i * matrixSize + j];
					k++;
				}
			}
		}
	}
}

// Convert sequence of blocks into matrix
void readMatrixFromBlocks(double* matrixByBlocks, double* resultMatrix, int matrixSize, int blockSize, int gridSize) {
	int k = 0;
	// iterate over blocks and write them in matrix
	for (int blockI = 0; blockI < gridSize; blockI++) {
		for (int blockJ = 0; blockJ < gridSize; blockJ++) {
			// write one block
			for (int i = blockI * blockSize; i < (blockI + 1) * blockSize; i++) {
				for (int j = blockJ * blockSize; j < (blockJ + 1) * blockSize; j++) {
					resultMatrix[i * matrixSize + j] = matrixByBlocks[k];
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
	if (rank == 0) {
		blocksOfMatrixA = (double*)malloc(matrixSize * matrixSize * sizeof(double));
		writeMatrixByBlocks(matrixA, blocksOfMatrixA, matrixSize, blockSize, gridSize);
	}
	MPI_Scatter(blocksOfMatrixA, blockSize * blockSize, MPI_DOUBLE, blockA, blockSize * blockSize, MPI_DOUBLE, 0, gridCommunicator);
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

	// cycling shifting blocks A and B
	int rowIndex = rank / gridSize;
	int columnIndex = rank % gridSize;
	shiftLeftBlockA(rowIndex);
	shiftUpBlockB(columnIndex);

	// for debug
//	printf("\nrank: %d, initialBlockA:\n", rank);
//	printMatrix(initialBlockA, blockSize);
//	printf("\nrank: %d, blockB:\n", rank);
//	printMatrix(blockB, blockSize);
}

// Perform cycling shifting of blockA by [offset] positions to the left
void shiftLeftBlockA(int offset) {
	// determiantion of ranks if destination & source processes
	int rowIndex = rank / gridSize;
	int firstRankInRow = rowIndex * gridSize;
	int lastRankInRow = rowIndex * gridSize + gridSize - 1;

	int destinationRank = (rank - offset >= firstRankInRow) ? (rank - offset) : 
		(lastRankInRow - (offset - (rank - firstRankInRow) - 1));

	// rank of process, from which current process get blockA
	int sourceRank = (rank + offset <= lastRankInRow) ? (rank + offset) : 
		(firstRankInRow + (offset - (lastRankInRow - rank) - 1));

	// sending & receiving itself
	double* copyBlockA = (double*)malloc(blockSize * blockSize * sizeof(double));
	for (int i = 0; i < blockSize * blockSize; i++) {
		copyBlockA[i] = blockA[i];
	}

	MPI_Status status;
	MPI_Sendrecv(copyBlockA, blockSize * blockSize, MPI_DOUBLE, destinationRank, 0,
		blockA, blockSize * blockSize, MPI_DOUBLE, sourceRank, 0, gridCommunicator, &status);
	free(copyBlockA);
}

// Perform cycling shifting of blockB by [offset] positions to the up
void shiftUpBlockB(int offset) {
	// determiantion of ranks if destination & source processes
	int colIndex = rank % gridSize;
	int firstRankInColumn = colIndex;
	int lastRankInColumn = gridSize * (gridSize - 1) + colIndex;

	int destinationRank = (rank - offset * gridSize >= firstRankInColumn) ? (rank - offset * gridSize) :
		(lastRankInColumn - gridSize * (offset - (rank - firstRankInColumn)/gridSize - 1));

	// rank of process, from which current process get blockA
	int sourceRank = (rank + offset * gridSize <= lastRankInColumn) ? (rank + offset * gridSize) :
		(firstRankInColumn + gridSize * (offset - (lastRankInColumn - rank) / gridSize - 1));


	// sending & reveiving itself
	double* copyBlockB = (double*)malloc(blockSize * blockSize * sizeof(double));
	for (int i = 0; i < blockSize * blockSize; i++) {
		copyBlockB[i] = blockB[i];
	}

	MPI_Status status;
	MPI_Sendrecv(copyBlockB, blockSize * blockSize, MPI_DOUBLE, destinationRank, 0,
		blockB, blockSize * blockSize, MPI_DOUBLE, sourceRank, 0, gridCommunicator, &status);
	free(copyBlockB);
}


void calculateBlockC() {
	serialMatrixMultiplication(blockA, blockB, blockC, blockSize);
}

// send blockA to left process, and receive blockA from right process
void passBlockA() {
	double* copyBlockA = (double*)malloc(blockSize * blockSize * sizeof(double));
	for (int i = 0; i < blockSize * blockSize; i++) {
		copyBlockA[i] = blockA[i];
	}

	MPI_Status status;
	MPI_Sendrecv(copyBlockA, blockSize * blockSize, MPI_DOUBLE, leftProcessRank, 0,
		blockA, blockSize * blockSize, MPI_DOUBLE, rightProcessRank, 0, gridCommunicator, &status);
	free(copyBlockA);
}


// send blockB to up process, and receive blockB from down process
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
		readMatrixFromBlocks(matrixByBlocks, matrixC, matrixSize, blockSize, gridSize);
		free(matrixByBlocks);
	}
}


/*
	Perform Cannon algorithm
	Steps:
		- distribute data
			- process (i,j) must get blocks A(i,j) and B(i,j)
			- cyclic shift of blockA by rowIndex positions to the left (rowIndex is row index of current process)
			- cyclic shift of blockB by columnIndex positions to the up (columnIndex is column index of current process)
		- do gridSize iterations with following steps (for each process):
			- calculate blockC += blockA * blockB
			- pass blockA to left process and get blockA from right process
			- pass blockB to up process and get blockB from down process
		- process 0 should gather matrix C from blocks C(i,j)
*/
void multiplyMatricesByCannonAlgorithm() {
	distributeTasks();
	for (int i = 0; i < gridSize; i++) {
		calculateBlockC();
		passBlockA();
		passBlockB();
		// for debug
//		printf("\nIteration %d, rank: %d, blockA:\n", i, rank);
//		printMatrix(blockA, blockSize);
//		printf("\nIteration %d, rank: %d, blockC:\n", i, rank);
//		printMatrix(blockC, blockSize);
	}
	gatherMatrixC();
}


