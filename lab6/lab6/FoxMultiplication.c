#include "FoxMultiplication.h"
#include "MatrixMultiplication.h"

// Creation of two-dimensional grid communicator and
// communicators for each row and each column of the grid
void CreateGridCommunicators() {
	int DimSize[2]; // Number of processes in each dimension of the grid
	int Periodic[2]; // =1, if the grid dimension should be periodic
	int Subdims[2]; // =1, if the grid dimension should be fixed

	DimSize[0] = gridSize;
	DimSize[1] = gridSize;

	Periodic[0] = 1;
	Periodic[1] = 1;
	// Determination of the size of the virtual grid
	MPI_Dims_create(numberOfProcesses, 2, DimSize);

	

	// Creation of the Cartesian communicator
	MPI_Cart_create(MPI_COMM_WORLD, 2, DimSize, Periodic, 1, &GridCommunicator);

	// Determination of the cartesian coordinates for every process
	MPI_Cart_coords(GridCommunicator, procRank, 2, currentProcessCoordinates);

	// Creating communicators for rows
	Subdims[0] = 0; // Dimension is fixed
	Subdims[1] = 1; // Dimension belong to the subgrid
	MPI_Cart_sub(GridCommunicator, Subdims, &RowCommunicator);

	// Creating communicators for columns
	Subdims[0] = 1; // Dimension belong to the subgrid
	Subdims[1] = 0; // Dimension is fixed
	MPI_Cart_sub(GridCommunicator, Subdims, &ColCommunicator);
}


// Function for memory allocation and initialization of matrices’ elements
int ProcessInitializationForParallel(double** pAMatrix, double** pBMatrix,
	double** pCMatrix, double** pAblock, double** pBblock, double** pCblock,
	double** pMatrixAblock, int* Size, int* BlockSize) {


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
	*pMatrixAblock = (double*)malloc((*BlockSize) * (*BlockSize) * sizeof(double));

	

	/*
		Initialize matrices' items
	*/
	srand((unsigned)clock());
	if (procRank == 0) {
		initMatrixByRandom(*pAMatrix, *Size);
		initMatrixByRandom(*pBMatrix, *Size);
		initMatrixByValue(*pCMatrix, *Size, 0);
	}
	
	initMatrixByValue(*pCblock, *BlockSize, 0);

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