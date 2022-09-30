package main

import (
	"fmt"
	"strconv"
	"sync"
	"time"

	"github.com/yourbasic/graph"
)

type TransportGraph struct {
	names_of_cities []*string
	g               *graph.Mutable
}

func newTransportGraph() *TransportGraph {
	g := new(TransportGraph)
	g.names_of_cities = make([]*string, 5, 5)
	g.g = graph.New(5)

	return g
}

func (this *TransportGraph) getFreeIndex() int {
	for i := 0; i < len(this.names_of_cities); i++ {
		if this.names_of_cities[i] == nil {
			return i
		}
	}
	return -1
}

func (this *TransportGraph) getCityIndex(name *string) int {
	for i := 0; i < len(this.names_of_cities); i++ {
		if this.names_of_cities[i] != nil &&
			*(this.names_of_cities[i]) == *name {
			return i
		}
	}
	return -1
}

func (this *TransportGraph) getCitiesNumber() int {
	count := 0
	for i := 0; i < len(this.names_of_cities); i++ {
		if this.names_of_cities[i] != nil {
			count++
		}
	}
	return count
}

func (this *TransportGraph) getCities() []string {
	result := make([]string, 0)
	for i := 0; i < len(this.names_of_cities); i++ {
		if this.names_of_cities[i] != nil {
			result = append(result, *(this.names_of_cities[i]))
		}
	}
	return result
}

func (this *TransportGraph) expandGraph() {
	new_graph := graph.New(2 * this.g.Order()) // create new graph

	// copy edges from old graph to new one
	for i := 0; i < this.g.Order(); i++ {
		for j := 0; j < this.g.Order(); j++ {
			if this.g.Edge(i, j) {
				new_graph.AddCost(i, j, this.g.Cost(i, j))
			}
		}
	}

	this.g = new_graph
}

func (this *TransportGraph) addCity(city string) bool {
	if this.getCityIndex(&city) != -1 {
		return false
	}

	freeIndex := this.getFreeIndex()
	if freeIndex == -1 {
		this.expandGraph()
		this.names_of_cities = append(this.names_of_cities, &city)
	} else {
		this.names_of_cities[freeIndex] = &city
	}

	return true
}

func (this *TransportGraph) removeCity(city string) bool {
	index := this.getCityIndex(&city)
	if index == -1 {
		return false
	}
	this.names_of_cities[index] = nil // delete its name

	// delete all edges with this city in real graph
	for i := 0; i < this.g.Order(); i++ {
		if this.g.Edge(index, i) {
			this.g.DeleteBoth(index, i)
		}
	}

	return true
}

func (this *TransportGraph) addEdge(dep string, dest string, cost int64) bool {
	dep_index := this.getCityIndex(&dep)
	dest_index := this.getCityIndex(&dest)

	if dep_index != -1 && dest_index != -1 && dep_index != dest_index {
		this.g.AddBothCost(dep_index, dest_index, cost)
		return true
	} else {
		return false
	}
}

func (this *TransportGraph) removeEdge(dep string, dest string) bool {
	dep_index := this.getCityIndex(&dep)
	dest_index := this.getCityIndex(&dest)

	if dep_index != -1 && dest_index != -1 && dep_index != dest_index {
		this.g.DeleteBoth(dep_index, dest_index)
		return true
	} else {
		return false
	}
}

func (this *TransportGraph) String() string {
	result := "\n\nCities:\n"

	for i := 0; i < len(this.names_of_cities); i++ {
		if this.names_of_cities[i] != nil {
			result += strconv.Itoa(i) + ": " + *(this.names_of_cities[i]) + "\n"
		} else {
			result += strconv.Itoa(i) + ": <nil> \n"
		}

	}

	return result + "\n\n" + this.g.String()
}

func (this *TransportGraph) costOfTravel(dep, dest string) int64 {
	dep_index := this.getCityIndex(&dep)
	dest_index := this.getCityIndex(&dest)

	if dep_index != -1 && dest_index != -1 {
		_, cost := graph.ShortestPath(this.g, dep_index, dest_index)
		return cost
	} else {
		return -1
	}
}

func (this *TransportGraph) changeTicketCost(dep, dest string, cost int64) bool {
	dep_index := this.getCityIndex(&dep)
	dest_index := this.getCityIndex(&dest)

	if dep_index != -1 && dest_index != -1 && dep_index != dest_index {
		if this.g.Edge(dep_index, dest_index) {
			this.g.AddBothCost(dep_index, dest_index, cost) // overwrite cost
			return true
		} else {
			return false
		}
	} else {
		return false
	}
}

func changeTicketCostThread(g *TransportGraph, lock *sync.RWMutex) {
	for true {
		lock.Lock()
		fmt.Println("Started changing cost")
		time.Sleep(2 * time.Second)
		g.changeTicketCost("A", "B", 2000)
		g.changeTicketCost("B", "C", 2500)
		fmt.Println("Finished changing cost")
		lock.Unlock()

		time.Sleep(5 * time.Second)
	}
}

func AddRoutesThread(g *TransportGraph, lock *sync.RWMutex) {
	for true {
		lock.Lock()
		fmt.Println("Started adding routes")
		time.Sleep(2 * time.Second)
		g.addEdge("A", "B", 1000)
		g.addEdge("A", "C", 1100)
		g.addEdge("D", "C", 1200)
		g.addEdge("M", "B", 1300)
		g.addEdge("D", "E", 1400)
		g.addEdge("M", "E", 1500)
		fmt.Println("Finished adding routes")
		lock.Unlock()

		time.Sleep(2 * time.Second)
	}
}

func RemoveRoutesThread(g *TransportGraph, lock *sync.RWMutex) {
	for true {
		lock.Lock()
		fmt.Println("Started removing routes")
		time.Sleep(2 * time.Second)
		g.removeEdge("D", "E")
		g.removeEdge("E", "M")
		fmt.Println("Finished removing routes")
		lock.Unlock()

		time.Sleep(2 * time.Second)
	}
}

func AddCitiesThread(g *TransportGraph, lock *sync.RWMutex) {
	for true {
		lock.Lock()
		fmt.Println("Started adding cities")
		time.Sleep(2 * time.Second)
		g.addCity("A")
		g.addCity("B")
		g.addCity("C")
		g.addCity("D")
		g.addCity("E")
		g.addCity("F")
		g.addCity("M")
		fmt.Println("Finished adding cities")
		lock.Unlock()

		time.Sleep(2 * time.Second)
	}
}

func RemoveCitiesThread(g *TransportGraph, lock *sync.RWMutex) {
	for true {
		lock.Lock()
		fmt.Println("Started removing cities")
		time.Sleep(2 * time.Second)
		g.removeCity("D")
		g.removeCity("E")
		fmt.Println("Finished removing cities")
		lock.Unlock()

		time.Sleep(2 * time.Second)
	}
}

func CalculateTravelPrice(g *TransportGraph, lock *sync.RWMutex) {
	for true {
		lock.RLock()
		fmt.Println("Started calculating price")
		time.Sleep(2 * time.Second)
		g.costOfTravel("A", "B")
		fmt.Println("Finished calculating price")
		lock.RUnlock()

		time.Sleep(1 * time.Second)
	}
}

func main() {

	// {1 2} - undirected
	tg := newTransportGraph()
	var lock sync.RWMutex

	waiting_buffer := make(chan int, 1)

	go AddCitiesThread(tg, &lock)
	go AddRoutesThread(tg, &lock)

	go CalculateTravelPrice(tg, &lock)
	go CalculateTravelPrice(tg, &lock)

	go changeTicketCostThread(tg, &lock)

	go CalculateTravelPrice(tg, &lock)
	go CalculateTravelPrice(tg, &lock)

	go RemoveRoutesThread(tg, &lock)
	go RemoveCitiesThread(tg, &lock)

	<-waiting_buffer
}
