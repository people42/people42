import { Cookies } from "react-cookie";

const cookies = new Cookies();

/**
 * Set Refresh Token to Cookie
 */
export const setCookieRefreshToken = (refreshToken: string) => {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);

  return cookies.set("42_RT", refreshToken, {
    path: "/",
    expires: tomorrow,
    secure: true,
    httpOnly: true,
  });
};

/**
 * Get Refresh Token from Cookie
 */
export const getCookieRefreshToken = () => {
  return cookies.get("42_RT");
};

/**
 * Remove Refresh Token in Cookie
 */
export const removeCookieRefreshToken = () => {
  return cookies.remove("42_RT");
};
