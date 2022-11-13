#pragma once
#include <mpi.h>

double* matrixA;
double* matrixB;
double* matrixC;
double* rowA;
double* columnB;
double* rowC;
int matrixSize, blockSize;
int rank, numberOfProcesses;
MPI_Comm topologyCommunicator;


void initProcessMemory();
void freeProcessMemory();

void setMatrixSize(int size);
void createRingTopology(MPI_Comm* topologyCommunicator);

void multiplyMatricesByStripeAlgorithm();
void distributeTasks();
void writeMatrixByColumns(double* matrix, double* matrixByColumns);
void calculateBlockC(int iteration);
void passColumnB();
void gatherMatrixC();