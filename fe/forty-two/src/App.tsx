import Meta from "./Meta";
import { postFCMToken, postLocation } from "./api";
import { getAccessToken } from "./api/auth";
import "./assets/fonts/pretendard/pretendard-subset.css";
import "./assets/fonts/pretendard/pretendard.css";
import AppleAccountCheck from "./pages/AppleAccountCheck/AppleAccountCheck";
import Logout from "./pages/Logout/Logout";
import Withdrawal from "./pages/Withdrawal/Withdrawal";
import { Home, Place, Policy, SignIn, SignUp, User } from "./pages/index";
import { isFirebaseLoadState } from "./recoil/FCM/atoms";
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
import { initializeApp } from "firebase/app";
import { getMessaging, getToken, onMessage } from "firebase/messaging";
import { useEffect, useState } from "react";
import { NavermapsProvider } from "react-naver-maps";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
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
  //////////////////////////
  // naver map client id import
  const NAVER_MAP_CLIENT_ID = import.meta.env.VITE_NAVER_MAP_CLIENT_ID;

  //////////////////////////
  // theme
  const [isDark, setIsDark] = useRecoilState(themeState);
  useEffect(() => {
    // 시스템 테마 모드 가져오기
    const isSystemDark: MediaQueryList = window.matchMedia(
      "(prefers-color-scheme: dark)"
    );
    // 테마 모드 변경 핸들러
    const handleSystemDarkChange = (e: MediaQueryListEvent) => {
      if (e.matches) {
        setIsDark(true);
      } else {
        setIsDark(false);
      }
      localStorage.setItem("isDark", e.matches.toString());
    };
    // 시스템 테마 모드 변경 리스너
    isSystemDark.addEventListener("change", handleSystemDarkChange);
    return () => {
      isSystemDark.removeEventListener("change", handleSystemDarkChange);
    };
  }, []);

  //////////////////////////
  // location background update
  const [location, setLocation] = useRecoilState<TLocation | null>(
    userLocationUpdateState
  );
  const setLocationInfo = useSetRecoilState<TLocationInfo | null>(
    locationInfoState
  );
  const setUserRefresh = useSetRecoilState(userState);
  const [user, userLogout] = useRecoilState(userLogoutState);
  // 사용자 위치 업데이트 함수
  const updateCurrentLocation = async () => {
    getUserLocation().then((res: any) =>
      setLocation({
        latitude: res.coords.latitude,
        longitude: res.coords.longitude,
      })
    );
  };
  const [isLogin, setIsLogin] = useRecoilState(isLoginState);
  useEffect(() => {
    // 사용자 로그인 여부 검증
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
    // 사용자 위치 업데이트
    updateCurrentLocation();
    // 사용자 위치 5분마다 업데이트
    const postLocationInterval = setInterval(() => {
      updateCurrentLocation();
    }, 300000);

    return () => {
      clearInterval(postLocationInterval);
    };
  }, []);
  // 사용자 위치 변경될 때마다 전송
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

  //////////////////////////
  // firebase
  const APP_KEY = import.meta.env.VITE_FIREBASE_APP_KEY;
  const AUTH_DOMAIN = import.meta.env.VITE_FIREBASE_AUTH_DOMAIN;
  const PROJECT_ID = import.meta.env.VITE_FIREBASE_PROJECT_ID;
  const STORAGE_BUCKET = import.meta.env.VITE_FIREBASE_STORAGE_BUCKET;
  const MESSAGING_SENDER_ID = import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID;
  const APP_ID = import.meta.env.VITE_FIREBASE_APP_ID;
  const V_API_ID_KEY = import.meta.env.VITE_FIREBASE_V_API_ID_KEY;
  const [firebaseConfig, setFirebaseConfig] = useState<any>();
  useEffect(() => {
    setFirebaseConfig({
      apiKey: APP_KEY,
      authDomain: AUTH_DOMAIN,
      projectId: PROJECT_ID,
      storageBucket: STORAGE_BUCKET,
      messagingSenderId: MESSAGING_SENDER_ID,
      appId: APP_ID,
    });
  }, []);
  // firebase init
  function requestPermission(app: any) {
    Notification.requestPermission().then((permission) => {
      if (permission === "granted") {
        // 알림 설정되어있는 경우
        const messaging = getMessaging(app);
        onMessage(messaging, (payload) => {
          // 유저 접속해있을 때 수신된 메시지
          console.log("수신된 메시지: ", payload);
        });
        getToken(messaging, {
          // 최초 토큰 발행
          vapidKey: V_API_ID_KEY,
        })
          .then((currentToken) => {
            // 토큰 서버에 전달
            if (currentToken && user) {
              postFCMToken(user.accessToken, currentToken)
                .then((res) => console.log(res))
                .catch((e) => console.log(e));
            } else {
              console.log("등록된 토큰이 없습니다.");
            }
          })
          .catch((err) => console.log(err));
      } else if (permission === "denied") {
        // 유저 알림 설정 꺼져있는 경우
        alert(
          "죄송합니다. 브라우저에서 알림 권한 요청 대화 상자를 더 이상 자동으로 표시하지 않도록 설정한 것 같습니다. 알림을 받으려면 수동으로 브라우저 설정에서 알림 권한을 허용해야 합니다. 이를 위해 브라우저 설정을 열고 해당 사이트의 권한을 확인해주세요."
        );
      }
    });
  }

  const [isFirebaseLoad, setIsFirebaseLoad] =
    useRecoilState(isFirebaseLoadState);
  useEffect(() => {
    if (firebaseConfig && user && !isFirebaseLoad) {
      const firebaseApp = initializeApp(firebaseConfig);
      requestPermission(firebaseApp);
      setIsFirebaseLoad(true);
    }
  }, [firebaseConfig, user]);

  const BASE_APP_URL = import.meta.env.VITE_BASE_APP_URL;

  return (
    <ThemeProvider theme={isDark ? darkStyles : lightStyles}>
      <GlobalStyle />
      <Meta
        title={"42"}
        description={"너랑 나 사이"}
        keywords={"SNS, 생각, 지도, 공유, 낭만, 익명"}
        imgsrc={
          "https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/etc/OG_image.png"
        }
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
