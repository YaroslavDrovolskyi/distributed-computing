#include "FoxAlgorithm.h"
#include "SerialAlgorithm.h"

// Creation of two-dimensional grid communicator and
// communicators for each row and each column of the grid
void CreateGridCommunicators() {
	int DimSize[2]; // Number of processes in each dimension of the grid
	int Periodic[2]; // =1, if the grid dimension should be periodic
	int Subdims[2]; // =1, if the grid dimension should be fixed

	DimSize[0] = gridSize;
	DimSize[1] = gridSize;

	Periodic[0] = 0;
	Periodic[1] = 0;
	// Determination of the size of the virtual grid
//	MPI_Dims_create(numberOfProcesses, 2, DimSize);



	// Creation of the Cartesian communicator
	MPI_Cart_create(MPI_COMM_WORLD, 2, DimSize, Periodic, 1, &GridCommunicator);

	// Determination of the cartesian coordinates for every process
	MPI_Cart_coords(GridCommunicator, procRank, 2, currentProcessCoordinates);

	// Creating communicators for rows
	Subdims[0] = 0; // Dimension is fixed
	Subdims[1] = 1; // The presence of the given dimension in the subgrid
	MPI_Cart_sub(GridCommunicator, Subdims, &RowCommunicator); // need col -> row

	// Creating communicators for columns
	Subdims[0] = 1; // The presence of the given dimension in the subgrid
	Subdims[1] = 0; // Dimension is fixed
	MPI_Cart_sub(GridCommunicator, Subdims, &ColCommunicator); // need row -> col
}


// Function for memory allocation and initialization of matrices’ elements
int ProcessInitializationForParallel(double** pAMatrix, double** pBMatrix,
	double** pCMatrix, double** pAblock, double** pBblock, double** pCblock,
	double** pTemporaryAblock, int* Size, int* BlockSize) {


	//	if (procRank == 0) {
	if (*Size % gridSize != 0) {
		return 1;
	}
	//	}

	MPI_Bcast(Size, 1, MPI_INT, 0, MPI_COMM_WORLD);

	*BlockSize = *Size / gridSize;

	/*
		Allocating dynamic memory for blocks and matrices,
		need to make free(pMatrix) somewhere
	*/
	if (procRank == 0) {
		*pAMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));
		*pBMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));
		*pCMatrix = (double*)malloc((*Size) * (*Size) * sizeof(double));
	}

	*pAblock = (double*)malloc((*BlockSize) * (*BlockSize) * sizeof(double));
	*pBblock = (double*)malloc((*BlockSize) * (*BlockSize) * sizeof(double));
	*pCblock = (double*)malloc((*BlockSize) * (*BlockSize) * sizeof(double));
	*pTemporaryAblock = (double*)malloc((*BlockSize) * (*BlockSize) * sizeof(double));


	/*
		Initialize matrices' items
	*/
	srand((unsigned)clock());
	if (procRank == 0) {
		initMatrixByRandom(*pAMatrix, *Size, *Size);
		initMatrixByRandom(*pBMatrix, *Size, *Size);
		//		initMatrixByValue(*pAMatrix, *Size, 1); //// for test
		//		initMatrixByValue(*pBMatrix, *Size, 1); //// for test
		initMatrixByValue(*pCMatrix, *Size, *Size, 0);
	}

	initMatrixByValue(*pCblock, *BlockSize, *BlockSize, 0);

	return 0;
}


void ProcessTerminationForParallel(double* pAMatrix, double* pBMatrix,
	double* pCMatrix, double* pAblock, double* pBblock, double* pCblock,
	double* pMatrixAblock) {
	if (procRank == 0) {
		free(pAMatrix);
		free(pBMatrix);
		free(pCMatrix);
	}
	free(pAblock);
	free(pBblock);
	free(pCblock);
	free(pMatrixAblock);
}


// Function for checkerboard matrix decomposition
void CheckerboardMatrixScatter(double* pMatrix, double* pMatrixBlock,
	int Size, int BlockSize) {
	double* pMatrixRow = (double*)malloc(BlockSize * Size * sizeof(double));
	if (currentProcessCoordinates[1] == 0) {
		MPI_Scatter(pMatrix, BlockSize * Size, MPI_DOUBLE, pMatrixRow,
			BlockSize * Size, MPI_DOUBLE, 0, ColCommunicator);
	}

	for (int i = 0; i < BlockSize; i++) {
		MPI_Scatter(&pMatrixRow[i * Size], BlockSize, MPI_DOUBLE,
			&(pMatrixBlock[i * BlockSize]), BlockSize, MPI_DOUBLE, 0, RowCommunicator);
	}
	free(pMatrixRow);
}

// Function for data distribution among the processes
void DataDistribution(double* pAMatrix, double* pBMatrix,
	double* pMatrixAblock, double* pBblock, int Size, int BlockSize) {
	CheckerboardMatrixScatter(pAMatrix, pMatrixAblock, Size, BlockSize);
	CheckerboardMatrixScatter(pBMatrix, pBblock, Size, BlockSize);
}

// Test printing of the matrix block
void TestBlocks(double* pBlock, int BlockSize, char str[]) {
	MPI_Barrier(MPI_COMM_WORLD);
	if (procRank == 0) {
		printf("%s \n", str);
	}
	MPI_Barrier(MPI_COMM_WORLD);
	for (int i = 0; i < numberOfProcesses; i++) {
		if (procRank == i) {
			printf("ProcRank = %d \n", procRank);
			printMatrix(pBlock, BlockSize);
		}
		MPI_Barrier(MPI_COMM_WORLD);
	}
}


// Execution of the Fox method
void ParallelResultCalculation(double* pAblock, double* pMatrixAblock,
	double* pBblock, double* pCblock, int BlockSize) {
	for (int iter = 0; iter < gridSize; iter++) {
		// Sending blocks of matrix A to the process grid rows
		ABlockCommunication(iter, pAblock, pMatrixAblock, BlockSize);

		// Block multiplication
		serialMatrixMultiplication(pAblock, pBblock, pCblock, BlockSize);

		// Cyclic shift of blocks of matrix B in process grid columns
		BblockCommunication(pBblock, BlockSize/*, ColCommunicator*/);
	}
}


// Broadcasting matrix A blocks to process grid rows
void ABlockCommunication(int iter, double* pAblock, double* pMatrixAblock,
	int BlockSize) {
	// Defining the leading process of the process grid row
	int Pivot = (currentProcessCoordinates[0] + iter) % gridSize;

	// Copying the transmitted block in a separate memory buffer
	if (currentProcessCoordinates[1] == Pivot) {
		for (int i = 0; i < BlockSize * BlockSize; i++) {
			pAblock[i] = pMatrixAblock[i];
		}
	}
	// Block broadcasting
	MPI_Bcast(pAblock, BlockSize * BlockSize, MPI_DOUBLE, Pivot, RowCommunicator);
}

// Cyclic shift of matrix B blocks in the process grid columns
void BblockCommunication(double* pBblock, int BlockSize
/*,MPI_Comm ColumnComm*/) {
	MPI_Status Status;
	int NextProc = currentProcessCoordinates[0] + 1;
	if (currentProcessCoordinates[0] == gridSize - 1) NextProc = 0;
	int PrevProc = currentProcessCoordinates[0] - 1;
	if (currentProcessCoordinates[0] == 0) PrevProc = gridSize - 1;

	MPI_Sendrecv_replace(pBblock, BlockSize * BlockSize, MPI_DOUBLE,
		NextProc, 0, PrevProc, 0, ColCommunicator, &Status);
}

// Function for gathering the result matrix
void ResultCollection(double* pCMatrix, double* pCblock, int Size,
	int BlockSize) {
	double* pResultRow = (double*)malloc(Size * BlockSize * sizeof(double));
	for (int i = 0; i < BlockSize; i++) {
		MPI_Gather(&pCblock[i * BlockSize], BlockSize, MPI_DOUBLE,
			&pResultRow[i * Size], BlockSize, MPI_DOUBLE, 0, RowCommunicator);
	}
	if (currentProcessCoordinates[1] == 0) {
		MPI_Gather(pResultRow, BlockSize * Size, MPI_DOUBLE, pCMatrix,
			BlockSize * Size, MPI_DOUBLE, 0, ColCommunicator);
	}
	free(pResultRow);
}


// returns 1-OJ, 0-NOT Ok 
int TestMultiplication(double* pAMatrix, double* pBMatrix, double* pCMatrix,
	int Size) {
	double* pSerialResult; // Result matrix of serial multiplication
	double eps = 1.e-1; // Comparison accuracy
	int equal = 1; // =1, if the matrices are not equal
	int i; // Loop variable
	if (procRank == 0) {
		pSerialResult = (double*)malloc(Size * Size * sizeof(double));
		initMatrixByValue(pSerialResult, Size, Size, 0);

		serialMatrixMultiplication(pAMatrix, pBMatrix, pSerialResult, Size);
		for (i = 0; i < Size * Size; i++) {
			if (fabs(pSerialResult[i] - pCMatrix[i]) >= eps) {
				equal = 0;
			}
		}

		//		printf("Correct result matrix:\n");
		//		printMatrix(pSerialResult, Size);

		free(pSerialResult);

		/*
		if (equal == 1)
			printf("The results of serial and parallel algorithms "
				"are NOT identical. Check your code.");
		else
			printf("The results of serial and parallel algorithms "
				"are identical.");
		*/

		return equal;
	}
}