import { getAccessToken } from "./api/auth";
import "./assets/fonts/pretendard/pretendard-subset.css";
import "./assets/fonts/pretendard/pretendard.css";
import Home from "./pages/Home/Home";
import { SignIn, SignUp } from "./pages/index";
import { userState } from "./recoil/account/atom";
import { themeState } from "./recoil/theme/atoms";
import "./reset.css";
import { GlobalStyle } from "./styles/globalStyle";
import { lightStyles, darkStyles } from "./styles/theme";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { useEffect } from "react";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { useRecoilState, useSetRecoilState } from "recoil";
import { ThemeProvider } from "styled-components";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home />,
  },
  {
    path: "/signin",
    element: <SignIn />,
  },
  {
    path: "/signup",
    element: <SignUp />,
  },
]);

function App() {
  const [isDark, setIsDark] = useRecoilState(themeState);

  useEffect(() => {
    const isSystemDark: MediaQueryList = window.matchMedia(
      "(prefers-color-scheme: dark)"
    );

    const handleSystemDarkChange = (e: MediaQueryListEvent) => {
      if (e.matches) {
        setIsDark(true);
      } else {
        setIsDark(false);
      }
      localStorage.setItem("isDark", e.matches.toString());
    };

    isSystemDark.addEventListener("change", handleSystemDarkChange);

    return () => {
      isSystemDark.removeEventListener("change", handleSystemDarkChange);
    };
  }, []);

  const setUserRefresh = useSetRecoilState(userState);
  useEffect(() => {
    getAccessToken()
      .then((res) => {
        setUserRefresh(res.data.data);
      })
      .catch((e) => console.log(e));
  }, []);

  return (
    <ThemeProvider theme={isDark ? darkStyles : lightStyles}>
      <GlobalStyle />
      <GoogleOAuthProvider clientId="630522923660-vjvl4kc0rh8eni5erbd9qb3a7tidshph.apps.googleusercontent.com">
        <RouterProvider router={router} />
      </GoogleOAuthProvider>
    </ThemeProvider>
  );
}

export default App;
