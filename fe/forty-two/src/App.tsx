import Meta from "./Meta";
import { getAccessToken } from "./api/auth";
import "./assets/fonts/pretendard/pretendard-subset.css";
import "./assets/fonts/pretendard/pretendard.css";
import AppleAccountCheck from "./pages/AppleAccountCheck/AppleAccountCheck";
import Home from "./pages/Home/Home";
import { Policy, SignIn, SignUp } from "./pages/index";
import { themeState } from "./recoil/theme/atoms";
import { isLoginState } from "./recoil/user/atoms";
import { userLoadedState, userState } from "./recoil/user/atoms";
import { userLogoutState } from "./recoil/user/selectors";
import "./reset.css";
import { GlobalStyle } from "./styles/globalStyle";
import { lightStyles, darkStyles } from "./styles/theme";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { useEffect } from "react";
import {
  createBrowserRouter,
  RouterProvider,
  useNavigate,
} from "react-router-dom";
import { useRecoilState, useSetRecoilState } from "recoil";
import { ThemeProvider } from "styled-components";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home />,
  },
  {
    path: "/policy",
    element: <Policy />,
  },
  {
    path: "/signin/apple",
    element: <AppleAccountCheck />,
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
  const [user, userLogout] = useRecoilState(userLogoutState);
  const setUserLoading = useSetRecoilState(userLoadedState);
  const [isLogin, setIsLogin] = useRecoilState(isLoginState);
  useEffect(() => {
    const isLocalLogin: string | null = localStorage.getItem("isLogin") ?? null;
    console.log(isLocalLogin);
    if (isLocalLogin) {
      getAccessToken()
        .then((res) => {
          console.log(res.data.data);
          setUserRefresh(res.data.data);
          setUserLoading(true);
          setIsLogin(true);
        })
        .catch((e) => {
          userLogout(user);
          localStorage.removeItem("isLogin");
          sessionStorage.removeItem("refreshToken");
          alert("오류가 발생했습니다. 다시 로그인해주세요.");
          setIsLogin(false);
        });
    } else {
      sessionStorage.removeItem("refreshToken");
      setIsLogin(false);
    }
  }, []);

  return (
    <ThemeProvider theme={isDark ? darkStyles : lightStyles}>
      <GlobalStyle />
      <Meta
        title={"42"}
        description={"너랑 나 사이"}
        keywords={"SNS, 생각, 지도, 공유, 낭만, 익명"}
        imgsrc={""}
        url={"https://people42.com"}
      ></Meta>
      <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
        <RouterProvider router={router} />
      </GoogleOAuthProvider>
    </ThemeProvider>
  );
}

export default App;
