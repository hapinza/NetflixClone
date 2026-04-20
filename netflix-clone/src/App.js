import { useState } from "react";
import api, { setAuthToken } from "./api/axios"
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import MoviesPage from "./pages/MoviePages";
import LikesPage from "./pages/LikePages";


function App(){
return(
<Router>
      <div style={{ maxWidth: "900px", margin: "0 auto", padding: "24px" }}>
        <nav style={{ marginBottom: "20px", display: "flex", gap: "12px" }}>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
          <Link to="/movies">Movies</Link>
          <Link to="/likes">My Likes</Link>
        </nav>

        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/movies" element={<MoviesPage />} />
          <Route path="/likes" element={<LikesPage />} />
          <Route path="*" element={<LoginPage />} />
        </Routes>
      </div>
    </Router>

);

}


export default App;








