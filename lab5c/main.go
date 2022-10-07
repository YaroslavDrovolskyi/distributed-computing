package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

type CyclicBarrier struct {
	maxCount     int // number of threads that must go to barrier for barrier to open
	count        int
	openRequests []chan int
	awaitLock    sync.Mutex
}

func newCyclicBarrier(maxCount int) *CyclicBarrier {
	barrier := new(CyclicBarrier)
	barrier.maxCount = maxCount
	barrier.count = maxCount
	barrier.openRequests = make([]chan int, 0, maxCount-1)

	return barrier
}

func (this *CyclicBarrier) await() {
	this.awaitLock.Lock()
	this.count--

	if this.count == 0 {
		//		fmt.Println("Thread is in await(), count == 0")
		this.openBarrier()
		this.awaitLock.Unlock()
	} else {
		//		fmt.Println("Thread is in await(), count > 0")
		semaphore := make(chan int, 1) // create semaphore to awake this thread when barrier will be opened
		this.openRequests = append(this.openRequests, semaphore)
		this.awaitLock.Unlock()
		<-semaphore // sleep
		// there barrier is opened
	}
}

func (this *CyclicBarrier) openBarrier() {
	// run barrier action here
	//	fmt.Println("Barrier is opened")

	for _, thread_semaphore := range this.openRequests {
		thread_semaphore <- 1 // wake up sleeep threads
	}
	this.openRequests = this.openRequests[:0]
	this.count = this.maxCount
}

func arrayThread(id int, arrays [][]int, barrier *CyclicBarrier, wg *sync.WaitGroup) {
	for {
		myArraySum := calcSum(arrays[id])
		needContinue := checkSumsEqual(id, myArraySum, arrays)

		//		fmt.Println("id =", id, "\t", arrays[id], "\tsum =", myArraySum, "\tneed continue = ", needContinue)

		fmt.Println("Thread", id, "\tfinished comparing sums,  \tneed continue = ", needContinue)
		barrier.await()

		if needContinue {
			i := rand.Intn(len(arrays[id])) // generate index of item to edit

			// decrement or increment item in array
			possibleDelta := []int{-1, 1}
			arrays[id][i] += possibleDelta[rand.Intn(len(possibleDelta))]

			fmt.Println("Thread", id, "\tfinished current iteration")
			barrier.await()
		} else {
			wg.Done()
			break
		}

		time.Sleep(3 * time.Second)

	}

}

/*
func getAverageOfArraysSums(arrays [][]int) float64 {
	var sum float64 = 0

	for _, array := range arrays {
		sum += float64(calcSum(array))
	}

	return sum / float64(len(arrays))
}
*/

func checkSumsEqual(id int, sum int, arrays [][]int) bool {
	for _, array := range arrays {
		if sum != calcSum(array) {
			return true
		}
	}

	return false
}

func calcSum(array []int) int {
	sum := 0
	for _, element := range array {
		sum += element
	}

	return sum
}

func main() {
	var wg sync.WaitGroup
	wg.Add(3)

	barrier := newCyclicBarrier(3)

	arrays := [][]int{
		{1, 2, 3, 4, 5},
		{6, 7, 1, -5, 7},
		{0, 2, 1, 6, 5},
	}

	/*
		arrays := [][]int{
			{1, 2, 3, 4, 5},
			{1, 2, 3, 4, 5},
			{1, 2, 3, 4, 5},
		}
	*/

	go arrayThread(0, arrays, barrier, &wg)
	go arrayThread(1, arrays, barrier, &wg)
	go arrayThread(2, arrays, barrier, &wg)

	wg.Wait()
}
