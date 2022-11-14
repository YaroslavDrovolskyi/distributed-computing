--------------------------------------------- Algorithms explanations ---------------------------------------------
This solution can multiply only square matrices (C = A*B)
n is size of matrix
p is numberOfProcesses
q is size of grid topology (q = sqrt(p))


1. Serial algorithm - classical algorithm

2. Stripe algorithm
	CONSTRAINTS: n must be divisibly by p
	1) Create cyclic one-dimensiobal topology for processes
	2) Divide A on horizontal tapes, and B - on vertical tapes (all tapes have the same size)
	   One process is responsible for calculation one row of result matrix. In one iteration, it calculates block of row
	3) Initial distributing of tasks: process i gets i-th rowA and i-th columnB
	4) Perform p iterations. On each iteration each process should:
		- perform blockC = rowA * columnB and place it correctly in resulting row
		- i-th process pass columnB to (i-1)-th process and get columnB from (i+1)-th process (cyclic shift to the left)
	5) Gather matrix C from all rowC

3. Fox algorithm
	CONSTRAINTS: p must be a perfect square; n must be divisibly by q (q=sqrt(p))
	1) Create two-dimensiobal grid topology for processes (size of topology: q*q)
	2) Matrices are divided on blocks (size of one block is k*k, where k=n/q)
	   One process is responsible for calculation one block of result matrix. In one iteration, it calculates partial sum of blockC
	3) Initial distributing of tasks: process (i,j) must get blocks A(i,j) and B(i,j)
	4) Perform q iterations. On each iteration do:
		- on each row scatter initialBlockA (it is block, that process got in initial distribution)
		  from correct process (choosing this process is in code). And each process in row save it into blockA variable
		  (each process has initialBlockA and blockA variables)
		- for each process calculate blockC += blockA * blockB
		- each process should pass blockB to up process and get blockB from down process
	5) Gather matrix C from all blocks C(i,j)

4. Cannon algorithm
	Idea is the same with Fox algorithm
	CONSTRAINTS: p must be a perfect square; n must be divisibly by q (q=sqrt(p))
	1) Create two-dimensiobal grid topology for processes (size of topology: q*q)
	2) Matrices are divided on blocks (size of one block is k*k, where k=n/q)
	   One process is responsible for calculation one block of result matrix. In one iteration, it calculates partial sum of blockC
	3) Initial distributing of tasks: 
		- process (i,j) must get blocks A(i,j) and B(i,j)
		- cyclic shift of blockA by rowIndex positions to the left (rowIndex is row index of current process)
		- cyclic shift of blockB by columnIndex positions to the up (columnIndex is column index of current process)
	4) Perform q iterations with following steps (for each process):
		- calculate blockC += blockA * blockB
		- pass blockA to left process and get blockA from right process
		- pass blockB to up process and get blockB from down process
	5) Gather matrix C from all blocks C(i,j)





================================== Run MPI ===================================
mpiexec -n {number of processes} {path to .exe-file} {parameters for .exe-file}
if -n is not specified, number of processes will be the number of cores on computer
Run following commands from root directory of the solution

There are two modes of .exe-files to run:
	- test: multiply random matrices by algorithm and compare result with result of Serial algorithm
	- benchmark: multiply matrices with hardcoded sizes, and measure time of running

Run Serial algorithm:
	Debug\SerialAlgorithm.exe benchmark
	Debug\SerialAlgorithm.exe test {numberOfTests} {matrixSize}
Run other algorithms:
	mpiexec -n {number of processes} Debug\{NameAlgorithm}.exe benchmark
	mpiexec -n {number of processes} Debug\{NameAlgorithm}.exe test {numberOfTests} {matrixSize}
	

1) Serial algorithm
	Debug\SerialAlgorithm.exe benchmark
	Debug\SerialAlgorithm.exe test 10 100

2) Stripe algorithm
	mpiexec -n 4 Debug\StripeAlgorithm.exe benchmark
	mpiexec -n 9 Debug\StripeAlgorithm.exe benchmark
	mpiexec -n 2 Debug\StripeAlgorithm.exe test 10 16
		CONSTRAINTS: 
			- matrixSize must be divisibly by numberOfProcesses

3) Fox algorithm 
	mpiexec -n 4 Debug\FoxAlgorithm.exe benchmark
	mpiexec -n 9 Debug\FoxAlgorithm.exe benchmark
	mpiexec -n 9 Debug\FoxAlgorithm.exe test 10 54
		CONSTRAINTS: 
			- numberOfProcesses must be a perfect square
			- matrixSize must be divisibly by sqrt(numberOfProcesses)

4) Cannon algorithm
	mpiexec -n 4 Debug\CannonAlgorithm.exe benchmark
	mpiexec -n 9 Debug\CannonAlgorithm.exe benchmark
	mpiexec -n 9 Debug\CannonAlgorithm.exe test 10 54
		CONSTRAINTS: 
			- numberOfProcesses must be a perfect square
			- matrixSize must be divisibly by sqrt(numberOfProcesses)



-------------------------------- Useful resources --------------------------------
http://www.hpcc.unn.ru/mskurs/RUS/DOC/ppr08.pdf - algorithms explanation
https://www.codingame.com/playgrounds/47058/have-fun-with-mpi-in-c/mpi-process-topologies - explanation of topologies
https://www.mpich.org/static/docs/v3.1/www3/ - official docs of MPI C
https://www.mpich.org/static/docs/v3.1/www1/mpiexec.html - about mpiexec command
https://www.mpich.org/static/docs/v3.1/www3/MPI_Wtime.html - about function MPI_Wtime()
https://en.cppreference.com/w/c/chrono/timespec_get - how to measure time in accuracy to nanoseconds