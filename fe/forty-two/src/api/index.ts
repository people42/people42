import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export const instance = (header?: string) =>
  axios.create({
    baseURL: API_URL,
    timeout: 3000,
    headers: { header },
  });

export * from "./auth";
export * from "./account";
