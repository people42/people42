import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export const instance = axios.create({
  baseURL: API_URL,
  timeout: 1000,
  headers: {},
});
