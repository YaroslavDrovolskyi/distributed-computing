#pragma once

#include <mpi.h>

int numberOfProcesses, matrixSize, gridSize, blockSize;
MPI_Comm gridCommunicator;
int rank, upProcessRank, downProcessRank, leftProcessRank, rightProcessRank;
double* matrixA;
double* matrixB;
double* matrixC;
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

void multiplyMatricesByCannonAlgorithm();
void distributeTasks();
void shiftLeftBlockA(int offset);
void shiftUpBlockB(int offset);
void calculateBlockC();
void passBlockA();
void passBlockB();
void gatherMatrixC();

