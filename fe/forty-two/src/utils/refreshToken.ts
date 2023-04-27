import { Cookies } from "react-cookie";

const cookies = new Cookies();

export const setRefreshToken = (refreshToken: string) => {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);

  return cookies.set("42_RT", refreshToken, {
    path: "/",
    expires: tomorrow,
    secure: true,
    httpOnly: true,
  });
};

export const getRefreshToken = () => {
  return cookies.get("42_RT");
};

export const removeRefreshToken = () => {
  return cookies.remove("42_RT");
};
