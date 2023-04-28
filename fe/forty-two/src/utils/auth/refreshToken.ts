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

/**
 * Remove Refresh Token in Session
 */
export const removeSessionRefreshToken = () => {
  return sessionStorage.removeItem("42_RT");
};
