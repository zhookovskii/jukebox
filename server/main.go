package main

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"strconv"
	"github.com/jackc/pgx/v5"
	"context"
	"log"
)

var (
	db *pgx.Conn
)

type SongRequest struct {
	Name string `json:"name"`
	Artist string `json:"artist"`
	Duration int `json:"duration"`
	FileUri string `json:"fileUri"`
	CoverUri string `json:"coverUri"`
}

type SongResponse struct {
	Id int `json:"id"`
	Name string `json:"name"`
	Artist string `json:"artist"`
	Duration int `json:"duration"`
}

func main() {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /songs/{id}", getSong)
	mux.HandleFunc("GET /songs", getSongs)
	mux.HandleFunc("POST /songs", createSong)
	mux.HandleFunc("DELETE /songs/{id}", deleteSong)
	mux.HandleFunc("GET /songs/{id}/play", playSong)
	mux.HandleFunc("GET /songs/{id}/cover", getCover)

	initDB()
	defer db.Close(context.Background())
	fmt.Println("Server listening on port 8080")
	http.ListenAndServe("0.0.0.0:8080", mux)
}

func initDB() {
	var err error
	db, err = pgx.Connect(context.Background(), "postgres://postgres:penguin@localhost:5432/postgres")
	if err != nil {
		log.Fatalf("Unable to connect to database: %v\n", err)
	}
	fmt.Println("Connected to database")
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

	var song SongResponse
	err = db.QueryRow(
		context.Background(),
		"SELECT id, name, artist, duration FROM songs WHERE id = $1",
		id,
	).Scan(&song.Id, &song.Name, &song.Artist, &song.Duration)

	if err != nil {
		http.Error(w, "Song not found", http.StatusNotFound)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	jsonResponse, err := json.Marshal(song)

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
	rows, err := db.Query(context.Background(), "SELECT id, name, artist, duration FROM songs")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var songs []SongResponse
	for rows.Next() {
		var song SongResponse
		if err := rows.Scan(&song.Id, &song.Name, &song.Artist, &song.Duration); err == nil {
			songs = append(songs, song)
		}
	}

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

	if song.FileUri == "" {
		http.Error(w, "\"fileUri\" must be specified", http.StatusBadRequest)
		return
	}

	if song.CoverUri == "" {
		http.Error(w, "\"coverUri\" must be specified", http.StatusBadRequest)
		return
	}

	_, err = db.Exec(
		context.Background(),
		"INSERT INTO songs (name, artist, duration, fileUri, coverUri) VALUES ($1, $2, $3, $4, $5)",
		song.Name,
		song.Artist,
		song.Duration,
		song.FileUri,
		song.CoverUri,
	)

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

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

	_, err = db.Exec(context.Background(), "DELETE FROM songs WHERE id = $1", id)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusNoContent)
}

func playSong(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.Atoi(r.PathValue("id"))

	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	var audioPath string
	err = db.QueryRow(
		context.Background(),
		"SELECT fileUri FROM songs WHERE id = $1",
		id,
	).Scan(&audioPath)

	if err != nil {
		http.Error(w, "Song file not found", http.StatusInternalServerError)
		return
	}

	http.ServeFile(w, r, audioPath)
}

func getCover(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.Atoi(r.PathValue("id"))
	if err != nil {
		http.Error(w, err.Error(), http.StatusNotFound)
		return
	}

	var imagePath string
	err = db.QueryRow(
		context.Background(),
		"SELECT coverUri FROM songs WHERE id = $1",
		id,
	).Scan(&imagePath)

	if err != nil {
		http.Error(w, "Song cover not found", http.StatusInternalServerError)
		return
	}

	file, err := os.Open(imagePath)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	defer file.Close()

	w.Header().Set("Content-Type", "image/jpeg")

	_, err = io.Copy(w, file)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
