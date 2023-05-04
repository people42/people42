import Meta from "./Meta";
import { postFCMToken, postLocation } from "./api";
import { getAccessToken } from "./api/auth";
import "./assets/fonts/pretendard/pretendard-subset.css";
import "./assets/fonts/pretendard/pretendard.css";
import { NotificationCard } from "./components";
import AppleAccountCheck from "./pages/AppleAccountCheck/AppleAccountCheck";
import Logout from "./pages/Logout/Logout";
import Withdrawal from "./pages/Withdrawal/Withdrawal";
import { Home, Place, Policy, SignIn, SignUp, User } from "./pages/index";
import {
  isLocationPermittedState,
  locationInfoState,
} from "./recoil/location/atoms";
import { userLocationUpdateState } from "./recoil/location/selectors";
import {
  isFirebaseLoadState,
  isNotificationPermittedState,
} from "./recoil/notification/atoms";
import { updateNotificationState } from "./recoil/notification/selector";
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
  const setUserRefresh = useSetRecoilState(userState);

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
  const [isLocationPermitted, setIsLocationPermitted] = useRecoilState(
    isLocationPermittedState
  );
  function requestLocationPermission() {
    if ("geolocation" in navigator) {
      navigator.permissions
        .query({ name: "geolocation" })
        .then(function (permissionStatus) {
          if (permissionStatus.state === "granted") {
            // 위치 권한이 켜져 있음
            setIsLocationPermitted(true);
          } else {
            // 위치 권한이 꺼져 있음
            setIsLocationPermitted(false);
          }
        });
    } else {
      // 브라우저가 위치 정보를 지원하지 않음
      setIsLocationPermitted(false);
    }
  }
  // 사용자 위치 업데이트 함수
  const updateCurrentLocation = async () => {
    getUserLocation().then((res: any) =>
      setLocation({
        latitude: res.coords.latitude,
        longitude: res.coords.longitude,
      })
    );
  };
  useEffect(() => {
    requestLocationPermission();
  }, []);

  useEffect(() => {
    // 사용자 위치 업데이트
    if (isLocationPermitted) {
      updateCurrentLocation();
    }
    // 사용자 위치 5분마다 업데이트
    let postLocationInterval = setInterval(() => {
      if (isLocationPermitted) {
        updateCurrentLocation();
      }
    }, 300000);

    return () => {
      clearInterval(postLocationInterval);
    };
  }, [isLocationPermitted]);

  //////////////////////////
  // isLogin
  const [user, userLogout] = useRecoilState(userLogoutState);
  const setIsLogin = useSetRecoilState(isLoginState);
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
  const [firebaseConfig, setFirebaseConfig] = useState<TfirebaseConfig>();
  // firebase config
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
  const setIsNotificationPermitted = useSetRecoilState(
    isNotificationPermittedState
  );
  // firebase request permission
  const requestNotificationPermission = async (app: any) => {
    Notification.requestPermission().then(async (permission) => {
      if (permission === "granted") {
        // 알림 설정되어있는 경우
        setIsNotificationPermitted(true);
        const messaging = getMessaging(app);
        if (messaging) {
          const newSw = await navigator.serviceWorker.register(
            "./firebase-messaging-sw.js"
          );

          getToken(messaging, {
            // 최초 토큰 발행
            vapidKey: V_API_ID_KEY,
            serviceWorkerRegistration: newSw,
          })
            .then((currentToken) => {
              // 토큰 서버에 전달
              if (currentToken && user) {
                postFCMToken(user.accessToken, currentToken).catch((e) => {
                  alert("알림 설정에 문제가 발생했습니다. 다시 시도해주세요.");
                  setIsNotificationPermitted(false);
                });
              } else {
                alert("알림 설정에 문제가 발생했습니다. 다시 시도해주세요.");
              }
            })
            .catch((err) => console.log(err));
        }
        // 유저 접속해있을 때 수신된 메시지
        onMessage(messaging, (payload) => {
          console.log("수신된 메시지: ", payload);
          setNewNotification({
            isShow: true,
            title: payload.notification?.title ?? "",
            body: payload.notification?.body ?? "",
            icon: payload.notification?.icon ?? "",
          });
        });
      } else if (permission === "denied") {
        // 유저 알림 설정 꺼져있는 경우
        setIsNotificationPermitted(false);
      }
    });
  };

  // firebase init
  const [isFirebaseLoad, setIsFirebaseLoad] =
    useRecoilState(isFirebaseLoadState);
  useEffect(() => {
    if (firebaseConfig && user && !isFirebaseLoad) {
      const firebaseApp = initializeApp(firebaseConfig);
      requestNotificationPermission(firebaseApp);
      setIsFirebaseLoad(true);
    }
  }, [firebaseConfig, user]);

  // notification
  const [newNotification, setNewNotification] = useRecoilState(
    updateNotificationState
  );
  useEffect(() => {
    if (newNotification && newNotification.isShow) {
      console.log(newNotification);
      const notificationTimeout = setTimeout(() => {
        const newNotificationCopy = Object.assign({}, newNotification);
        newNotificationCopy.isShow = false;
        setNewNotification(newNotificationCopy);
      }, 5000);

      return () => {
        clearTimeout(notificationTimeout);
      };
    }
  }, [newNotification]);

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
          <NotificationCard></NotificationCard>
          <RouterProvider router={router} />
        </NavermapsProvider>
      </GoogleOAuthProvider>
    </ThemeProvider>
  );
}

export default App;
