import appStoreBadge from "../../../../assets/images/badge/Download_on_the_App_Store_Badge_KR_RGB_blk_100317.svg";
import appIcon from "../../../../assets/images/badge/appIcon.png";
import playStoreBadge from "../../../../assets/images/badge/google-play-badge.png";
import { Card } from "../../../../components";
import React, { useEffect, useState } from "react";
import { TbX } from "react-icons/tb";
import styled from "styled-components";

type bannerProps = {};

function Banner({}: bannerProps) {
  const [showBanner, setShowBanner] = useState<boolean>(true);
  const bannerClose = () => {
    setShowBanner(false);
    sessionStorage.setItem("app_download_banner", "x");
  };

  useEffect(() => {
    switch (sessionStorage.getItem("app_download_banner")) {
      case "x":
        setShowBanner(false);
        break;
    }
  });

  return (
    <StyledBanner>
      {showBanner ? (
        <Card isShadowInner={false}>
          <>
            <TbX onClick={() => bannerClose()} size={24} />
            <div className="banner">
              <img className="banner-icon" src={appIcon}></img>
              <div className="banner-body">
                <h2>모바일 앱으로 더 많은 생각 만나기</h2>
                <div className="banner-body-badge">
                  <img src={playStoreBadge}></img>
                  <img src={appStoreBadge}></img>곧 출시 예정
                </div>
              </div>
            </div>
          </>
        </Card>
      ) : null}
    </StyledBanner>
  );
}

export default Banner;

const StyledBanner = styled.div`
  animation: floatingUp 0.5s both;
  animation-delay: 1s;
  position: fixed;

  height: 128px;
  bottom: 24px;
  left: 24px;

  & > div {
    padding: 16px;
    & > svg {
      position: absolute;
      top: 8px;
      right: 8px;
      color: ${({ theme }) => theme.color.text.secondary};
      cursor: pointer;
    }
  }
  .banner {
    z-index: 100;
    display: flex;
    align-items: center;
    overflow: hidden;
    &-icon {
      width: 96px;
      margin-right: 24px;
    }
    &-body {
      min-width: 360px;
      ${({ theme }) => theme.text.header5}
      &-badge {
        display: flex;
        align-items: center;
        height: 40px;
        margin-top: 8px;
        filter: opacity(0.2);
        & > img:first-child {
          height: 40px;
          margin-right: 8px;
        }
        & > img:last-child {
          height: 30px;
          margin-right: 8px;
        }
        ${({ theme }) => theme.text.caption}
      }
    }
  }
`;
