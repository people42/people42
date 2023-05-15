/**
 * Set Refresh Token to Session
 */
export const setSessionRefreshToken = (refreshToken: string) => {
  return sessionStorage.setItem("42_RT", refreshToken);
};

/**
 * Get Refresh Token from Session
 */
export const getSessionRefreshToken = () => {
  return sessionStorage.getItem("42_RT");
};
