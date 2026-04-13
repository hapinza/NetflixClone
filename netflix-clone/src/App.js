import { useState } from "react";
import api, { setAuthToken } from "./api/axios"

function App(){
const [token, setTokenState ] = useState("");
const [movieId, setMovieId] = useState("");
const [likes, setLikes] = useState("");
const [status, setStatus] = useState("");
const [page, setPage] = useState(0);


const handleSetToken = () => {
  setAuthToken(token);
  setStatus("Token setup");
}

const handleLike = async() => {
  if(!movieId) {
    setStatus("need to input movieId");
    return ;
  }

  try{
    await api.put(`/likes/${movieId}`, { like: true});
    setStatus(`Movie ${movieId} liked`);
    fetchLikes(page);
  }catch(err){
    setStatus("like failed");
    console.error(err);
  }
};


const handleUnlike = async() => {
  if(!movieId){
    setStatus("require movieId");
    return ;
  }

  try{
    await api.delete(`/likes/${movieId}`);
    setStatus(`Movie ${movieId} Unliked`);
    fetchLikes(page);
  }catch(err){
    setStatus("Unlike failed");
    console.error(err);
  }
};


const fetchLikes = async(targetPage = 0) => {
  try{
    const res = await api.get(`/likes?page=${targetPage}&size5`);
    setLikes(res.data.content);
    setPage(targetPage);
    setStatus("fetch success");
  }catch(err){
    setStatus("failed to fetch");
    console.log(err);
  }
};


return (
  <div style={{ maxWidth: "800px", margin: "0 auto", padding: "24px" }}>
    <h1>LIKE LIST</h1>

    {/* TOKEN */}
    <div style={{ marginBottom: "20px" }}>
      <h3>JWT Token</h3>
      <textarea
        rows="3"
        style={{ width: "100%" }}
        value={token}
        onChange={(e) => setTokenState(e.target.value)}
      />
      <button onClick={handleSetToken} style={{ marginTop: "8px" }}>
        Set Token
      </button>
    </div>

    {/* INPUT */}
    <div style={{ marginBottom: "20px" }}>
      <h3>Movie Action</h3>
      <input
        type="number"
        placeholder="Movie ID"
        value={movieId}
        onChange={(e) => setMovieId(e.target.value)}
      />
      <button onClick={handleLike} style={{ marginLeft: "8px" }}>
        👍 Like
      </button>
      <button onClick={handleUnlike} style={{ marginLeft: "8px" }}>
        ❌ Unlike
      </button>
    </div>

    {/* STATUS */}
    <div style={{ marginBottom: "20px" }}>
      <strong>Status:</strong> {status}
    </div>

    {/* LIST */}
    <div>
      <h3>My Likes</h3>
      <button onClick={() => fetchLikes(0)}>🔄 Refresh</button>

      {likes.length === 0 ? (
        <p>No liked movies</p>
      ) : (
        likes.map((item) => (
          <div
            key={item.movieId}
            style={{
              border: "1px solid #ccc",
              padding: "10px",
              marginTop: "10px",
            }}
          >
            <p>🎬 Movie ID: {item.movieId}</p>
            <p>❤️ Liked: {item.like ? "Yes" : "No"}</p>
            <p>⏱ Updated: {item.updatedAt}</p>
          </div>
        ))
      )}

      {/* PAGINATION */}
      <div style={{ marginTop: "12px" }}>
        <button
          onClick={() => page > 0 && fetchLikes(page - 1)}
          disabled={page === 0}
        >
          Prev
        </button>
        <span style={{ margin: "0 10px" }}>Page: {page}</span>
        <button onClick={() => fetchLikes(page + 1)}>Next</button>
      </div>
    </div>
  </div>
);
}



export default App;








