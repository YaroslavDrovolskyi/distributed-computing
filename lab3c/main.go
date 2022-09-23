package main

import (
	"fmt"
	"math/rand"
	"time"
)

type MediatorThread struct {
	smokersInvoker chan int
	myInvoker      chan int
	items_on_table []int
}

type SmokerThread struct {
	smokersInvoker    chan int
	mediatorInvoker   chan int
	item              int
	items_on_table    []int
	start_smoke_value int // value, when smoker need to start smoke
	name              string
}

func (this *MediatorThread) run() {
	for true {
		<-this.myInvoker
		this.generateRandomItems()
		fmt.Println("Mediator put random items on table", this.items_on_table, "& invoked smokers")
		time.Sleep(1 * time.Second)
		this.smokersInvoker <- 1
	}
}

func (this *SmokerThread) run() {
	for true {
		<-this.smokersInvoker // sleep if blocked
		if this.canSmoke() {
			fmt.Println(this.name + " started to smoke...")
			time.Sleep(3 * time.Second)
			fmt.Println(this.name + " end smoke & invoked mediator")
			this.mediatorInvoker <- 1 // invoke mediator
		} else {
			this.smokersInvoker <- 1 // invoke other smoker
		}
	}
}

func (this *SmokerThread) canSmoke() bool {
	sum := this.item + sum(this.items_on_table)
	return (sum == this.start_smoke_value)
}

func (m *MediatorThread) generateRandomItems() {
	options := [][]int{
		[]int{1, 2},
		[]int{1, 3},
		[]int{2, 3},
	}
	i := rand.Intn(len(options))

	for j := range options[i] {
		m.items_on_table[j] = options[i][j]
	}
}

func sum(array []int) int {
	sum := 0
	for _, v := range array {
		sum += v
	}

	return sum
}

func main() {
	mediatorInvoker := make(chan int, 1)
	mediatorInvoker <- 1

	smokersInvoker := make(chan int, 1)
	items_on_table := make([]int, 2, 2)

	mediator := MediatorThread{smokersInvoker: smokersInvoker, myInvoker: mediatorInvoker, items_on_table: items_on_table}

	smoker_1 := SmokerThread{smokersInvoker: smokersInvoker, mediatorInvoker: mediatorInvoker,
		item: 1, items_on_table: items_on_table, start_smoke_value: 6, name: "Smoker-1"}

	smoker_2 := SmokerThread{smokersInvoker: smokersInvoker, mediatorInvoker: mediatorInvoker,
		item: 2, items_on_table: items_on_table, start_smoke_value: 6, name: "Smoker-2"}

	smoker_3 := SmokerThread{smokersInvoker: smokersInvoker, mediatorInvoker: mediatorInvoker,
		item: 3, items_on_table: items_on_table, start_smoke_value: 6, name: "Smoker-3"}

	waiting_buffer := make(chan int, 1)

	go mediator.run()
	go smoker_1.run()
	go smoker_2.run()
	go smoker_3.run()

	<-waiting_buffer

}

/*
	I used channels as a semaphore:
 	 - getting value from channel means semaphore.acquire()
	 - putting value in channel means semaphore.release()
*/
