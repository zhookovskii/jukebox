package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"sync"
)

var (
	nextId = 0
	songCache = make(map[int]SongRequest)
	cacheMutex sync.RWMutex
)

type SongRequest struct {
	Name string `json:"name"`
	Artist string `json:"artist"`
}

type SongResponse struct {
	Id int `json:"id"`
	Name string `json:"name"`
	Artist string `json:"artist"`
}

func main() {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /songs/{id}", getSong)
	mux.HandleFunc("GET /songs", getSongs)
	mux.HandleFunc("POST /songs", createSong)
	mux.HandleFunc("DELETE /songs/{id}", deleteSong)

	fmt.Println("Server listening on port 8080")
	http.ListenAndServe(":8080", mux)
}

func getSong(
	w http.ResponseWriter,
	r *http.Request,
) {
	id, err := strconv.Atoi(r.PathValue("id"))

	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	cacheMutex.RLock()
	songRequest, ok := songCache[id]
	cacheMutex.RUnlock()

	if !ok {
		http.Error(w, "Song not found", http.StatusNotFound)
		return
	}

	songResponse := SongResponse{
		Id: id,
		Name: songRequest.Name,
		Artist: songRequest.Artist,
	}

	w.Header().Set("Content-Type", "application/json")
	jsonResponse, err := json.Marshal(songResponse)

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
	w.Write(jsonResponse)
}

func getSongs(
	w http.ResponseWriter,
	r *http.Request,
) {
	cacheMutex.RLock()
	songs := make([]SongResponse, 0, len(songCache))
	for id, songReq := range songCache {
		songs = append(songs, SongResponse{
			Id:     id,
			Name:   songReq.Name,
			Artist: songReq.Artist,
		})
	}
	cacheMutex.RUnlock()

	w.Header().Set("Content-Type", "application/json")
	jsonResponse, err := json.Marshal(songs)

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
	w.Write(jsonResponse)
}

func createSong(
	w http.ResponseWriter,
	r *http.Request,
) {
	var song SongRequest
	err := json.NewDecoder(r.Body).Decode(&song)

	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	if song.Name == "" {
		http.Error(w, "\"name\" must be specified", http.StatusBadRequest)
		return
	}

	if song.Artist == "" {
		http.Error(w, "\"artist\" must be specified", http.StatusBadRequest)
		return
	}

	cacheMutex.Lock()
	songCache[nextId] = song
	nextId++
	cacheMutex.Unlock()

	w.WriteHeader(http.StatusNoContent)
}

func deleteSong(
	w http.ResponseWriter,
	r *http.Request,
) {
	id, err := strconv.Atoi(r.PathValue("id"))

	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	cacheMutex.Lock()
	if _, ok := songCache[id]; !ok {
		http.Error(w, "Song not found", http.StatusNotFound)
		return
	}
	delete(songCache, id)
	cacheMutex.Unlock()

	w.WriteHeader(http.StatusNoContent)
}