#include "CannonAlgorithm.h"
#include "../SerialAlgorithm/SerialAlgorithm.h"



void test(int numberOfTests);

int main(int argc, char** argv) {
	MPI_Init(argc, &argv);
	MPI_Comm_size(MPI_COMM_WORLD, &numberOfProcesses);
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);


	// for debug: print arguments
	if (rank == 0) {
		printf("CANNON ALGORITHM\n");
		printf("=== Arguments ===\n");
		for (int i = 1; i < argc; i++) {
			printf("[%d]\t %s \n", i, argv[i]);
		}
		printf("=================\n");
	}

	if (rank == 0) {
		srand((unsigned)clock());
	}

	if (strcmp("benchmark", argv[1]) == 0) {
		printf("Benchmark started!\n");
		// need to implement benchmark
	}
	else if (strcmp("test", argv[1]) == 0) {
		setMatrixSize(atoi(argv[3]));
		createGridTopology();
		MPI_Comm_rank(gridCommunicator, &rank);
		test(atoi(argv[2]));
	}

	MPI_Finalize();
	return 0;
}


void test(int numberOfTests) {
	int passed = 1;
	for (int i = 0; i < numberOfTests; i++) {
		initProcessMemory();

		double startTime = MPI_Wtime();
		multiplyMatricesByCannonAlgorithm();
		double dt = MPI_Wtime() - startTime;

		// check if matrices multiplied correctly
		if (rank == 0) {
			double* serialResult = (double*)malloc(matrixSize * matrixSize * sizeof(double));
			initMatrixByValue(serialResult, matrixSize, matrixSize, 0);
			serialMatrixMultiplication(matrixA, matrixB, serialResult, matrixSize);
			passed *= isMatricesEqual(matrixC, serialResult, matrixSize, 0);
			printf("test %d, time: %f s, IS PASSED: %d\n", i + 1, dt, passed);
		}

		freeProcessMemory();
	}

	if (rank == 0) {
		printf("ARE TESTS PASSED: %d", passed);
	}
}


// matrix[i][j] == matrixAsArray[i * size + j]