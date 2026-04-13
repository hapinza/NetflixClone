import axios from "axios";


const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers:{
        "Content-Type": "application/json"

    }
});


export const setAuthToken = (token) => {
    if(token) {
        AudioParam.defaults.headers.Authorization =  'Bearer ${token}';
    }else{
        delete api.defaults.headers.Authorization;
    }
};


export default api;