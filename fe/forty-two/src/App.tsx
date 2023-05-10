import Meta from "./Meta";
import { postFCMToken, postLocation } from "./api";
import { getAccessToken } from "./api/auth";
import "./assets/fonts/pretendard/pretendard-subset.css";
import "./assets/fonts/pretendard/pretendard.css";
import { NotificationCard } from "./components";
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
import {
  socketAllMessageCntState,
  socketGuestCntState,
  socketNearUserState,
  socketState,
} from "./recoil/socket/atoms";
import {
  socketNewMessageChangeState,
  socketPongSendState,
  socketUserChangeState,
  socketUserRemoveState,
} from "./recoil/socket/selectors";
import { themeState } from "./recoil/theme/atoms";
import { isLoginState, userState } from "./recoil/user/atoms";
import { userLogoutState } from "./recoil/user/selectors";
import "./reset.css";
import browserRouter from "./router";
import { GlobalStyle } from "./styles/globalStyle";
import { lightStyles, darkStyles } from "./styles/theme";
import {
  getLocalIsLogin,
  getUserLocation,
  handleClose,
  handleMove,
  setSessionRefreshToken,
  socketInfoReceive,
  socketInit,
} from "./utils";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { initializeApp } from "firebase/app";
import { getMessaging, getToken, onMessage } from "firebase/messaging";
import { useEffect, useState } from "react";
import { isDesktop } from "react-device-detect";
import { NavermapsProvider } from "react-naver-maps";
import { RouterProvider } from "react-router-dom";
import { useRecoilState, useSetRecoilState } from "recoil";
import { ThemeProvider } from "styled-components";

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

function App() {
  const setUserRefresh = useSetRecoilState(userState);
  const [socket, setSocket] = useRecoilState(socketState);
  const [userLocation, setUserLocation] = useRecoilState<TLocation | null>(
    userLocationUpdateState
  );

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
  const setLocationInfo = useSetRecoilState<TLocationInfo | null>(
    locationInfoState
  );
  const [isLocationPermitted, setIsLocationPermitted] = useRecoilState(
    isLocationPermittedState
  );
  function requestLocationPermission() {
    if (isDesktop) {
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
  }
  // 사용자 위치 업데이트 함수
  const updateCurrentLocation = async () => {
    getUserLocation().then((res: any) => {
      setUserLocation({
        latitude: res.coords.latitude,
        longitude: res.coords.longitude,
      });
    });
  };
  useEffect(() => {
    isDesktop ? requestLocationPermission() : null;
  }, []);

  useEffect(() => {
    // 사용자 위치 업데이트
    if (isLocationPermitted === true && isDesktop) {
      updateCurrentLocation();
    }
    // 사용자 위치 5분마다 업데이트
    let postLocationInterval = setInterval(() => {
      if (isLocationPermitted === true && isDesktop) {
        updateCurrentLocation();
      }
      if (socket && user?.accessToken && userLocation) {
        handleMove(socket, {
          latitude: userLocation.latitude,
          longitude: userLocation.longitude,
          status: "watching",
        });
      }
    }, 300000);

    return () => {
      clearInterval(postLocationInterval);
    };
  }, [isLocationPermitted]);

  //////////////////////////
  // isLogin
  const [user, userLogout] = useRecoilState(userLogoutState);
  const [isLogin, setIsLogin] = useRecoilState(isLoginState);
  useEffect(() => {
    // 사용자 로그인 여부 검증
    const isLocalLogin: boolean = getLocalIsLogin();
    if (isLocalLogin) {
      getAccessToken()
        .then((res) => {
          setUserRefresh(res.data.data);
          setSessionRefreshToken(res.data.data.refreshToken);
          setIsLogin(true);
        })
        .catch((e) => {
          userLogout(user);
          setIsLogin(false);
        });
    } else {
      setIsLogin(false);
    }
  }, []);

  useEffect(() => {
    if (user && user.accessToken && !isLogin) {
      setIsLogin(true);
    }
  }, [user?.accessToken]);

  // 사용자 위치 변경될 때마다 전송
  useEffect(() => {
    if (userLocation && user) {
      postLocation(user?.accessToken, userLocation)
        .then((res) => setLocationInfo(res.data.data))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
              postLocation(res.data.data.accessToken, userLocation).then(
                (res) => setLocationInfo(res.data.data)
              );
            });
          }
        });
    }
  }, [userLocation, user?.accessToken]);

  ////////////////
  // socket
  const setNearUser = useSetRecoilState(socketNearUserState);
  const setGuestCnt = useSetRecoilState(socketGuestCntState);
  const setPongSend = useSetRecoilState(socketPongSendState);
  const setUserChange = useSetRecoilState(socketUserChangeState);
  const setUserRemove = useSetRecoilState(socketUserRemoveState);
  const setNewMessage = useSetRecoilState(socketNewMessageChangeState);

  const socketOnMessage = (data: TSocketReceive) => {
    switch (data.method) {
      case "INFO":
        const socketInfoReceiveData = socketInfoReceive(data);
        setNearUser(socketInfoReceiveData.nearUserMap);
        setGuestCnt(socketInfoReceiveData.guestCnt);
        break;
      case "NEAR":
        if (data.data.type == "user") {
          const newUser: Map<number, TSocketNearUser> = new Map();
          newUser.set(data.data.userIdx, data.data);
          setUserChange(newUser);
        }
        break;
      case "CLOSE":
        const newCloseUser: Map<number, TSocketNearUser> = new Map();
        newCloseUser.set(data.data.userIdx, data.data);
        setUserRemove(newCloseUser);
        break;
      case "CHANGE_STATUS":
        const newChangeUser: Map<number, TSocketNearUser> = new Map();
        newChangeUser.set(data.data.userIdx, data.data);
        setUserChange(newChangeUser);
        break;
      case "MESSAGE_CHANGED":
        setNewMessage({
          userIdx: data.data.userIdx,
          message: data.data.message,
          nickname: data.data.nickname,
        });
        break;
      case "PING":
        setPongSend(socket);
        break;
      default:
        console.log("unknown method:", data);
        break;
    }
  };

  const initSocket = () => {
    if (userLocation) {
      setSocket(
        socketInit(
          user?.accessToken ? "user" : "guest",
          {
            latitude: userLocation.latitude,
            longitude: userLocation.longitude,
            status: "watching",
          },
          socketOnMessage,
          user?.user_idx
        )
      );
    }
  };
  useEffect(() => {
    const socketclean = () => {
      if (socket) {
        handleClose(socket);
        setSocket(null);
      }
    };
    if (!socket && userLocation && isLogin !== "check") {
      initSocket();
    }
    window.addEventListener("beforeunload", socketclean);
    return () => {
      window.removeEventListener("beforeunload", socketclean);
    };
  }, [userLocation, user?.accessToken]);

  useEffect(() => {
    if (socket && socket?.readyState === 1) {
      handleClose(socket);
      setSocket(null);
      initSocket();
    }
  }, [isLogin, userLocation]);

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
    isDesktop
      ? setFirebaseConfig({
          apiKey: APP_KEY,
          authDomain: AUTH_DOMAIN,
          projectId: PROJECT_ID,
          storageBucket: STORAGE_BUCKET,
          messagingSenderId: MESSAGING_SENDER_ID,
          appId: APP_ID,
        })
      : null;
  }, []);
  const setIsNotificationPermitted = useSetRecoilState(
    isNotificationPermittedState
  );
  // firebase request permission
  const requestNotificationPermission = async (app: any) => {
    Notification.requestPermission().then((permission) => {
      if (permission === "granted") {
        // 알림 설정되어있는 경우
        setIsNotificationPermitted(true);
        const messaging = getMessaging(app);
        onMessage(messaging, (payload) => {
          // 유저 접속해있을 때 수신된 메시지
          setNewNotification({
            isShow: true,
            title: payload.notification?.title ?? "",
            body: payload.notification?.body ?? "",
            icon: payload.notification?.icon ?? "",
          });
        });
        getToken(messaging, {
          // 최초 토큰 발행
          vapidKey: V_API_ID_KEY,
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
          .catch((e) => console.log(e));
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

  return (
    <ThemeProvider theme={isDark ? darkStyles : lightStyles}>
      <GlobalStyle />
      <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
        <NavermapsProvider ncpClientId={NAVER_MAP_CLIENT_ID}>
          <Meta></Meta>
          <NotificationCard></NotificationCard>
          <RouterProvider router={browserRouter} />
        </NavermapsProvider>
      </GoogleOAuthProvider>
    </ThemeProvider>
  );
}

export default App;
