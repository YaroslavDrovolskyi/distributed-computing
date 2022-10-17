package main

/*
   Module1 variant #2
*/

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

type Library struct {
	books []int
	size  int
}

func newLibrary(size int) *Library {
	library := new(Library)
	library.size = size

	library.books = make([]int, size)
	for i := range library.books {
		library.books[i] = 1
	}
	return library
}
func (lib *Library) getSize() int {
	return lib.size
}

func (lib *Library) isBookAvailable(id int) bool {
	return lib.books[id] > 0
}

func (lib *Library) takeBook(id int) int {
	lib.books[id]--
	return id
}

func (lib *Library) returnBook(id int) {
	lib.books[id]++
}

func (lib *Library) availableBooks() []int {
	availableBooks := make([]int, 0)

	for i := range lib.books {
		if lib.books[i] > 0 {
			availableBooks = append(availableBooks, i)
		}
	}
	return availableBooks
}

type Reader struct {
	id          int
	library     *Library
	libraryLock *sync.Mutex
	wantedBooks []int
	takenBooks  []int
}

func newReader(id int, library *Library, libraryLock *sync.Mutex) *Reader {
	reader := new(Reader)
	reader.id = id
	reader.library = library
	reader.libraryLock = libraryLock
	reader.wantedBooks = make([]int, 0, 3)
	reader.takenBooks = make([]int, 0, 3)

	return reader
}

func (reader *Reader) run() {
	for true {
		reader.libraryLock.Lock()

		// return previously taken books
		for _, book := range reader.takenBooks {
			reader.library.returnBook(book)
		}
		fmt.Print("Reader-", reader.id, "   returned: ", reader.takenBooks)
		reader.takenBooks = nil // clear taken books

		// generate wanted books
		allBooks := rand.Perm(reader.library.getSize())
		reader.wantedBooks = allBooks[0:3]

		// take wanted books if available
		for _, wantedBook := range reader.wantedBooks {
			if reader.library.isBookAvailable(wantedBook) {
				reader.takenBooks = append(reader.takenBooks, reader.library.takeBook(wantedBook))
			}
			time.Sleep(time.Second)
		}

		fmt.Println("   wanted:", reader.wantedBooks,
			"   taken:", reader.takenBooks,
			"   available in library:", reader.library.availableBooks())

		reader.libraryLock.Unlock()

		time.Sleep(5 * time.Second) // read books
	}
}

func main() {
	var wg sync.WaitGroup
	wg.Add(1)

	library := newLibrary(15)
	var libraryLock sync.Mutex

	// create readers
	readers := make([]*Reader, 5)
	for i := range readers {
		readers[i] = newReader(i, library, &libraryLock)
	}

	// start readers
	for _, reader := range readers {
		go reader.run()
	}

	wg.Wait()
}
