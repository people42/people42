import appStoreBadge from "../../../../assets/images/badge/Download_on_the_App_Store_Badge_KR_RGB_blk_100317.svg";
import testFlightBadge from "../../../../assets/images/badge/Pre-order_on_the_App_Store_Badge_KR_RGB_blk_121217.svg";
import appIcon from "../../../../assets/images/badge/appIcon.png";
import playStoreBadge from "../../../../assets/images/badge/google-play-badge.png";
import { Card } from "../../../../components";
import { QRCodeSVG } from "qrcode.react";
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

  const [isQrOpen, setIsQrOpen] = useState<"android" | "ios" | null>(null);

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
                  <img
                    style={{ filter: "opacity(0.2)" }}
                    src={playStoreBadge}
                  ></img>
                  <img
                    onClick={() => setIsQrOpen(isQrOpen ? null : "ios")}
                    src={testFlightBadge}
                  ></img>
                </div>

                <div
                  className="banner-body-qr"
                  style={{ height: `${isQrOpen ? "160px" : "0px"}` }}
                >
                  <QRCodeSVG value="https://testflight.apple.com/join/YP7D30sc"></QRCodeSVG>
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
  z-index: 100;
  animation: floatingUp 0.5s both;
  animation-delay: 1s;
  position: fixed;

  min-height: 128px;
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
    display: flex;
    align-items: start;
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
        & > img:first-child {
          height: 40px;
          margin-right: 8px;
        }
        & > img:last-child {
          cursor: pointer;
          height: 30px;
          margin-right: 8px;
        }
        ${({ theme }) => theme.text.caption}
      }
      &-qr {
        transition: all 0.3s;
        display: flex;
        justify-content: center;
        align-items: center;
        width: 160px;
        overflow: hidden;
      }
    }
  }
`;
