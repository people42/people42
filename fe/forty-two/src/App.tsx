import Meta from "./Meta";
import { postLocation } from "./api";
import { getAccessToken } from "./api/auth";
import "./assets/fonts/pretendard/pretendard-subset.css";
import "./assets/fonts/pretendard/pretendard.css";
import AppleAccountCheck from "./pages/AppleAccountCheck/AppleAccountCheck";
import Logout from "./pages/Logout/Logout";
import Withdrawal from "./pages/Withdrawal/Withdrawal";
import { Home, Place, Policy, SignIn, SignUp, User } from "./pages/index";
import { locationInfoState } from "./recoil/location/atoms";
import { userLocationUpdateState } from "./recoil/location/selectors";
import { themeState } from "./recoil/theme/atoms";
import { isLoginState, userState } from "./recoil/user/atoms";
import { userLogoutState } from "./recoil/user/selectors";
import "./reset.css";
import { GlobalStyle } from "./styles/globalStyle";
import { lightStyles, darkStyles } from "./styles/theme";
import {
  getLocalIsLogin,
  getUserLocation,
  setSessionRefreshToken,
} from "./utils";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { useEffect } from "react";
import { NavermapsProvider } from "react-naver-maps";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { useRecoilState, useSetRecoilState } from "recoil";
import { ThemeProvider } from "styled-components";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home />,
  },
  {
    path: "/place",
    element: <Place />,
  },
  {
    path: "/user/:user_id",
    element: <User />,
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
  {
    path: "/logout",
    element: <Logout />,
  },
  {
    path: "/withdrawal/:platform",
    element: <Withdrawal />,
  },
]);

function App() {
  const NAVER_MAP_CLIENT_ID = import.meta.env.VITE_NAVER_MAP_CLIENT_ID;
  const [isDark, setIsDark] = useRecoilState(themeState);
  const [location, setLocation] = useRecoilState<TLocation | null>(
    userLocationUpdateState
  );
  const setLocationInfo = useSetRecoilState<TLocationInfo | null>(
    locationInfoState
  );
  const setUserRefresh = useSetRecoilState(userState);
  const [user, userLogout] = useRecoilState(userLogoutState);

  const updateCurrentLocation = async () => {
    getUserLocation().then((res: any) =>
      setLocation({
        latitude: res.coords.latitude,
        longitude: res.coords.longitude,
      })
    );
  };

  const setIsLogin = useSetRecoilState(isLoginState);

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

    const isLocalLogin: boolean = getLocalIsLogin();
    if (isLocalLogin) {
      getAccessToken()
        .then((res) => {
          setIsLogin(true);
          setUserRefresh(res.data.data);
          setSessionRefreshToken(res.data.data.refreshToken);
        })
        .catch((e) => {
          setIsLogin(false);
          userLogout(user);
        });
    } else {
      setIsLogin(false);
    }

    updateCurrentLocation();
    const postLocationInterval = setInterval(() => {
      updateCurrentLocation();
    }, 300000);

    return () => {
      isSystemDark.removeEventListener("change", handleSystemDarkChange);
      clearInterval(postLocationInterval);
    };
  }, []);

  useEffect(() => {
    if (location && user) {
      postLocation(user?.accessToken, location)
        .then((res) => setLocationInfo(res.data.data))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
              postLocation(res.data.data.accessToken, location).then((res) =>
                setLocationInfo(res.data.data)
              );
            });
          }
        });
    }
  }, [location, user]);

  const BASE_APP_URL = import.meta.env.VITE_BASE_APP_URL;

  return (
    <ThemeProvider theme={isDark ? darkStyles : lightStyles}>
      <GlobalStyle />
      <Meta
        title={"42"}
        description={"너랑 나 사이"}
        keywords={"SNS, 생각, 지도, 공유, 낭만, 익명"}
        imgsrc={""}
        url={BASE_APP_URL}
      ></Meta>
      <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
        <NavermapsProvider ncpClientId={NAVER_MAP_CLIENT_ID}>
          <RouterProvider router={router} />
        </NavermapsProvider>
      </GoogleOAuthProvider>
    </ThemeProvider>
  );
}

export default App;
