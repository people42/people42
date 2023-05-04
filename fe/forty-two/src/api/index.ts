import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export const instance = (
  header?: { "ACCESS-TOKEN": string } | { "REFRESH-TOKEN": string }
) =>
  axios.create({
    baseURL: API_URL,
    timeout: 3000,
    headers: header,
  });

export * from "./auth";
export * from "./notification";
export * from "./account";
export * from "./policy";
export * from "./feed";
export * from "./background";
