import { useState } from "react";
import api, { setAuthToken } from "./api/axios"

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








