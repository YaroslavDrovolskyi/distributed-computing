package main

import (
	"fmt"
	"math/rand"
	"strconv"
	"sync"
)

type Monk struct {
	name         string
	power        int
	group_number int
}

func fight(m1, m2 Monk, sync_print *sync.Mutex) Monk {

	winner := m2
	looser := m1
	if m1.power >= m2.power {
		winner = m1
		looser = m2
	}

	if winner.power >= 5*looser.power {
		winner.power += looser.power
	} else if winner.power >= looser.power {
		winner.power -= looser.power
	}

	// print result of a fight; need to syncronize it
	sync_print.Lock()
	fmt.Println(m1.name, "(", m1.power, ",", m1.group_number, ") vs ",
		m2.name, "(", m2.power, ",", m2.group_number, ") -> ",
		winner.name, "(", winner.power, ",", winner.group_number, ")")
	sync_print.Unlock()

	return winner

}

func getWinnerImpl(monks []Monk, ch_output chan Monk, sync_print *sync.Mutex) {
	if len(monks) == 2 {
		m1 := monks[0]
		m2 := monks[1]
		ch_output <- fight(m1, m2, sync_print)
		return

	} else {
		ch_input := make(chan Monk, 2)

		go getWinnerImpl(monks[:len(monks)/2], ch_input, sync_print)
		go getWinnerImpl(monks[len(monks)/2:], ch_input, sync_print)

		m1 := <-ch_input
		m2 := <-ch_input

		ch_output <- fight(m1, m2, sync_print)
	}
}

func getWinner(monks []Monk) Monk {
	ch_input := make(chan Monk, 1)
	var sync_print sync.Mutex
	getWinnerImpl(monks, ch_input, &sync_print) // launch recursion
	return <-ch_input
}

// number must be exp of 2
func generateMonks(size int) []Monk {
	monks := make([]Monk, size, size)

	for i := 0; i < size; i++ {
		name := "monk-" + strconv.Itoa(i)
		monks[i] = Monk{group_number: i % 2, power: rand.Intn(1000), name: name}
	}

	return monks
}

func main() {
	monks := generateMonks(32)
	fmt.Println("\n\n\nStart monks array: \n", monks, "\n")
	winner := getWinner(monks)
	fmt.Printf("\nWinner is:\nname: %s,\npower: %d,\nmonastery: %d\n\n", winner.name, winner.power, winner.group_number)
}
