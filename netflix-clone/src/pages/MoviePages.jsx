import { useEffect, useState } from "react";
import api from "../api/axios";

function MoviesPage() {
  const [movies, setMovies] = useState([]);
  const [status, setStatus] = useState("");

  useEffect(() => {
    fetchMovies();
  }, []);

  const fetchMovies = async () => {
    try {
      const res = await api.get("/movies");
      setMovies(res.data);
      setStatus("Movies loaded");
    } catch (err) {
      setStatus("Failed to load movies");
      console.error(err);
    }
  };

  const handleLike = async (movieId) => {
    try {
      await api.put(`/likes/${movieId}`, { like: true });
      setStatus(`Liked movie ${movieId}`);
    } catch (err) {
      setStatus("Like failed");
      console.error(err);
    }
  };

  return (
    <div>
      <h2>Movies</h2>
      <p>{status}</p>

      {movies.map((movie) => (
        <div
          key={movie.id}
          style={{ border: "1px solid #ccc", padding: "12px", marginBottom: "12px" }}
        >
          <h4>{movie.title}</h4>
          <p>{movie.overview}</p>
          <button onClick={() => handleLike(movie.id)}>👍 Like</button>
        </div>
      ))}
    </div>
  );
}

export default MoviesPage;