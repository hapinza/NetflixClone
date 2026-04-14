import { useState } from "react";
import api from "../api/axios";

function RegisterPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [status, setStatus] = useState("");

  const handleRegister = async (e) => {
    e.preventDefault();

    try {
      await api.post("/api/members/register", { email, password });
      setStatus("Registration successful");
    } catch (err) {
      setStatus("Registration failed");
      console.error(err);
    }
  };

  return (
    <div>
      <h2>Register</h2>
      <form onSubmit={handleRegister} style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button type="submit">Register</button>
      </form>
      <p>{status}</p>
    </div>
  );
}

export default RegisterPage;