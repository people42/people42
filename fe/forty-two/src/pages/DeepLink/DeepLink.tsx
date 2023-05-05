import appStoreBadge from "../../assets/images/badge/Download_on_the_App_Store_Badge_KR_RGB_blk_100317.svg";
import appIcon from "../../assets/images/badge/appIcon.png";
import playStoreBadge from "../../assets/images/badge/google-play-badge.png";
import { useEffect } from "react";
import { isMobile, isAndroid } from "react-device-detect";
import { useNavigate } from "react-router";
import styled from "styled-components";

type deepLinkProps = {};

function DeepLink({}: deepLinkProps) {
  const navigate = useNavigate();

  useEffect(() => {
    if (isMobile) {
      if (isAndroid) {
        console.log("안드로이드");
      } else {
        console.log("아이폰");
      }
    } else {
      navigate("/");
    }
  }, []);

  return (
    <StyledDeepLink>
      <h2 className="deeplink-title">어쩌면</h2>
      <h2 className="deeplink-title">마주친</h2>
      <img className="deeplink-icon" src={appIcon}></img>
      <p className="deeplink-description">나도 모르게 스쳐간 인연과</p>
      <p className="deeplink-description">생각을 공유해보세요</p>
      <img
        className="deeplink-badge"
        src={isAndroid ? playStoreBadge : appStoreBadge}
      ></img>
      <p className="deeplink-info">곧 출시 예정</p>
    </StyledDeepLink>
  );
}

export default DeepLink;

const StyledDeepLink = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 24px;

  > .deeplink {
    &-title {
      ${({ theme }) => theme.text.header2}
      line-height: 72px;
    }
    &-icon {
      width: 210px;
      margin-block: 32px 48px;
    }
    &-description {
      ${({ theme }) => theme.text.header6}
    }
    &-badge {
      filter: opacity(0.2);
      width: 200px;
      margin-block: 80px 8px;
    }
    &-info {
      ${({ theme }) => theme.text.subtitle2}
      color: ${({ theme }) => theme.color.text.secondary};
    }
  }
`;
