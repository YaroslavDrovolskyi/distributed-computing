#pragma once

#include <mpi.h>

int numberOfProcesses, matrixSize, gridSize, blockSize;
MPI_Comm gridCommunicator;
int rank, upProcessRank, downProcessRank;
double* matrixA;
double* matrixB;
double* matrixC;
double* initialBlockA;
double* blockA;
double* blockB;
double* blockC;

// set matrix size, grid size and block size, check if size is valid (according to algo restrictions)
void setMatrixSize(int size);

void createGridTopology();

void initProcessMemory();
void freeProcessMemory();

void writeMatrixByBlocks(double* matrix, double* block, int matrixSize, int blockSize, int gridSize);
void readMatrixFromBlocks(double* blocksMatrix, double* resultMatrix, int matrixSize, int blockSize, int gridSize);

void multiplyMatricesByFoxAlgorithm();
void distributeTasks();
void passOverRowInitialBlockA(int iteration);
void calculateBlockC();
void passBlockB();
void gatherMatrixC();