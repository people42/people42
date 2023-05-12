import appStoreBadge from "../../assets/images/badge/Download_on_the_App_Store_Badge_KR_RGB_blk_100317.svg";
import appIcon from "../../assets/images/badge/appIcon.png";
import playStoreBadge from "../../assets/images/badge/google-play-badge.png";
import { LogoBg } from "../../components";
import Spinner from "../../components/Spinner/Spinner";
import { useEffect, useState } from "react";
import { isMobile, isAndroid } from "react-device-detect";
import { useNavigate } from "react-router";
import styled from "styled-components";

type deepLinkProps = {};

function DeepLink({}: deepLinkProps) {
  const navigate = useNavigate();
  const APP_SCHEME = import.meta.env.VITE_APP_SCHEME;
  const ANDROID_URL = import.meta.env.VITE_ANDROID_URL;
  const IOS_URL = import.meta.env.VITE_IOS_URL;

  const [isAppLoad, setIsAppLoad] = useState(true);

  useEffect(() => {
    if (isMobile) {
      if (isAndroid) {
      } else {
        exeDeepLink();
        checkInstallApp();
      }
    } else {
      navigate("/");
    }
  }, []);

  function exeDeepLink() {
    location.href = APP_SCHEME;
  }

  function checkInstallApp() {
    function clearTimers() {
      clearInterval(check);
      clearTimeout(timer);
    }

    function isHideWeb() {
      // @ts-ignore
      if (document.webkitHidden || document.hidden) {
        clearTimers();
      }
    }
    const check = setInterval(isHideWeb, 200);

    const timer = setTimeout(function () {
      redirectStore();
    }, 500);
  }

  const redirectStore = () => {
    const ua = navigator.userAgent.toLowerCase();

    if (
      window.confirm(
        isAndroid
          ? "앱 다운로드를 위해 Google Play Store로 이동합니다."
          : "앱 다운로드를 위해 App Store로 이동합니다."
      )
    ) {
      location.href = isAndroid ? ANDROID_URL : IOS_URL;
    } else {
      setIsAppLoad(false);
    }
  };

  return (
    <StyledDeepLink>
      <h2 className="deeplink-title">어쩌면</h2>
      <h2 className="deeplink-title">마주친</h2>
      <img className="deeplink-icon" src={appIcon}></img>
      {isAppLoad ? (
        <div className="deeplink-loading">앱 불러오는 중</div>
      ) : (
        <>
          <p className="deeplink-description">나도 모르게 스쳐간 인연과</p>
          <p className="deeplink-description">생각을 공유해보세요</p>
          {isAndroid ? (
            <>
              <img
                className="deeplink-badge"
                src={playStoreBadge}
                style={{ filter: "opacity(0.2)" }}
              ></img>
              <p className="deeplink-info">곧 출시 예정</p>
              <p className="deeplink-info">지금은 PC에서 이용할 수 있습니다</p>
            </>
          ) : (
            <>
              <img
                onClick={() => {
                  setIsAppLoad(true);
                  exeDeepLink();
                  checkInstallApp();
                }}
                className="deeplink-badge"
                src={appStoreBadge}
              ></img>
            </>
          )}
        </>
      )}
      <LogoBg isBlue={false}></LogoBg>
    </StyledDeepLink>
  );
}

export default DeepLink;

const StyledDeepLink = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  padding: 24px;
  margin-top: 24px;

  > .deeplink {
    &-title {
      animation: floatingUp 0.3s both;
      animation-delay: 0.2s;
      ${({ theme }) => theme.text.header2}
      line-height: 72px;
    }
    &-icon {
      width: 210px;
      margin-block: 24px 36px;
    }
    &-loading {
      width: 100%;
      height: 210px;
      display: flex;
      justify-content: center;
      ${({ theme }) => theme.text.header6}
    }
    &-description {
      animation: floatingUp 0.3s both;
      ${({ theme }) => theme.text.header6}
    }
    &-badge {
      animation: floatingUp 0.3s both;
      animation-delay: 0.3s;
      width: 200px;
      margin-block: 48px 8px;
    }
    &-info {
      animation: floatingUp 0.3s both;
      animation-delay: 1s;
      ${({ theme }) => theme.text.subtitle2}
      color: ${({ theme }) => theme.color.text.secondary};
    }
  }
`;
