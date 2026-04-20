import axios from 'axios'


const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type" : "application/json"
    }
});


export const setAuthToken = (token) => {
    if(token){
        api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
        localStorage.setItem("token", token);
    }else{
        delete api.defaults.headers.common["Authorization"];
        localStorage.removeItem("token");
    }
};



const savedToken = localStorage.getItem("token");
if(savedToken){
    setAuthToken(savedToken);
}

export default api;