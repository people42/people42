import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export const instance = (
  header?: { "ACCESS-TOKEN": string } | { "REFRESH-TOKEN": string }
) => {
  const axiosInstance = axios.create({
    baseURL: API_URL,
    timeout: 5000,
    headers: header,
    withCredentials: true,
  });

  // axiosInstance.interceptors.response.use(
  //   (res) => {
  //     return res;
  //   },
  //   async (error) => {
  //     switch (error.response.status) {
  //       case 401:
  //         const accessToken = await getAccessToken().then(
  //           (res) => res.data.data.accessToken
  //         );

  //         error.config.headers["ACCESS-TOKEN"] = accessToken;

  //         return await axios.request(error.config);

  //       default:
  //         return Promise.reject(error);
  //     }
  //   }
  // );

  return axiosInstance;
};

export * from "./auth";
export * from "./notification";
export * from "./account";
export * from "./policy";
export * from "./feed";
export * from "./background";
export * from "./socket";
