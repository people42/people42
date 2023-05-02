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
