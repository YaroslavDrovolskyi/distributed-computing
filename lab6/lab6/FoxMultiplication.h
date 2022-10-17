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
