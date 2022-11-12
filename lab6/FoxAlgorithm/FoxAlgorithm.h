#pragma once
#pragma once

#include <mpi.h>

int numberOfProcesses, procRank, gridSize;
MPI_Comm GridCommunicator, ColCommunicator, RowCommunicator;
int currentProcessCoordinates[2];

// Creation of two-dimensional grid communicator and
// communicators for each row and each column of the grid
void CreateGridCommunicators();

// Function for memory allocation and data initialization
int ProcessInitializationForParallel(double** pAMatrix, double** pBMatrix,
	double** pCMatrix, double** pAblock, double** pBblock, double** pCblock,
	double** pMatrixAblock, int* Size, int* BlockSize);

void ProcessTerminationForParallel(double* pAMatrix, double* pBMatrix,
	double* pCMatrix, double* pAblock, double* pBblock, double* pCblock,
	double* pMatrixAblock);

// Function for checkerboard matrix decomposition
void CheckerboardMatrixScatter(double* pMatrix, double* pMatrixBlock,
	int Size, int BlockSize);

// Function for data distribution among the processes
void DataDistribution(double* pAMatrix, double* pBMatrix,
	double* pMatrixAblock, double* pBblock, int Size, int BlockSize);

// Test printing of the matrix block
void TestBlocks(double* pBlock, int BlockSize, char str[]);


// Main function of Fox algorithm
void ParallelResultCalculation(double* pAblock, double* pMatrixAblock,
	double* pBblock, double* pCblock, int BlockSize);

// Broadcasting matrix A blocks to process grid rows
void ABlockCommunication(int iter, double* pAblock, double* pMatrixAblock,
	int BlockSize);

// Cyclic shift of matrix B blocks in the process grid columns (from down to up)
void BblockCommunication(double* pBblock, int BlockSize/*,
	MPI_Comm ColumnComm*/);

	// Function for gathering the result matrix
void ResultCollection(double* pCMatrix, double* pCblock, int Size,
	int BlockSize);

int TestMultiplication(double* pAMatrix, double* pBMatrix, double* pCMatrix,
	int Size);


