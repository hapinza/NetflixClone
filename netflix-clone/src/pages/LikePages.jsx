import { useEffect, useState } from "react";
import api from "../api/axios";

function LikesPage() {
  const [likes, setLikes] = useState([]);
  const [page, setPage] = useState(0);
  const [status, setStatus] = useState("");

  useEffect(() => {
    fetchLikes(0);
  }, []);

  const fetchLikes = async (targetPage = 0) => {
    try {
      const res = await api.get(`/likes?page=${targetPage}&size=5`);
      setLikes(res.data.content);
      setPage(targetPage);
      setStatus("Fetch success");
    } catch (err) {
      setStatus("Failed to fetch likes");
      console.error(err);
    }
  };

  const handleUnlike = async (movieId) => {
    try {
      await api.delete(`/likes/${movieId}`);
      setStatus(`Unliked movie ${movieId}`);
      fetchLikes(page);
    } catch (err) {
      setStatus("Unlike failed");
      console.error(err);
    }
  };

  return (
    <div>
      <h2>My Likes</h2>
      <p>{status}</p>
      <button onClick={() => fetchLikes(page)}>Refresh</button>

      {likes.length === 0 ? (
        <p>No liked movies</p>
      ) : (
        likes.map((item) => (
          <div
            key={item.movieId}
            style={{ border: "1px solid #ccc", padding: "12px", marginTop: "10px" }}
          >
            <p>Movie ID: {item.movieId}</p>
            <p>Liked: {item.like ? "Yes" : "No"}</p>
            <p>Updated: {item.updatedAt}</p>
            <button onClick={() => handleUnlike(item.movieId)}>Unlike</button>
          </div>
        ))
      )}

      <div style={{ marginTop: "16px" }}>
        <button onClick={() => page > 0 && fetchLikes(page - 1)} disabled={page === 0}>
          Prev
        </button>
        <span style={{ margin: "0 10px" }}>Page: {page}</span>
        <button onClick={() => fetchLikes(page + 1)}>Next</button>
      </div>
    </div>
  );
}

export default LikesPage;