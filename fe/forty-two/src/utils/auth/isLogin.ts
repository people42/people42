/**
 * Set isLogin to localStorage
 */
export const setLocalIsLogin = () => {
  return localStorage.setItem("isLogin", "true");
};

/**
 * Get isLogin from localStorage
 */
export const getLocalIsLogin = () => {
  const isLogin = localStorage.getItem("isLogin");
  if (isLogin == "true") {
    return true;
  } else {
    return false;
  }
};

/**
 * Remove isLogin in localStorage
 */
export const removeLocalIsLogin = () => {
  return localStorage.removeItem("isLogin");
};
