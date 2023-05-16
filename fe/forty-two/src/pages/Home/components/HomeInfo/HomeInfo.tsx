import { IconBtn } from "../../../../components";
import { homeInfoState } from "../../../../recoil/home/atoms";
import { QRCodeSVG } from "qrcode.react";
import { useState } from "react";
import {
  TbArrowBigLeftFilled,
  TbArrowBigRightFilled,
  TbX,
} from "react-icons/tb";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type homeInfoProps = {};

const BASE_APP_URL = import.meta.env.VITE_BASE_APP_URL;

function HomeInfo({}: homeInfoProps) {
  const infoData = [
    {
      title: "내 주변에 스쳐 지나간 생각을 한눈에 확인하세요",
      description:
        "모바일 앱을 활용하여 일상 속 주변 사람들의 생각을 모을 수 있습니다.",
    },
    {
      title: "내 생각을 작성하세요",
      description: "내 생각은 익명으로 주변 사람들에게 공유됩니다.",
    },
    {
      title: "지금 내 주변 생각을 확인하세요",
      description: "새로운 생각을 실시간으로 미리 확인해 볼 수 있습니다.",
    },
    {
      title: "모바일 앱으로 주변 생각을 모아보세요",
      description:
        "모바일 앱을 통해 스친 인연의 생각을 PC에서 확인할 수 있습니다.",
    },
  ];

  const [infoIdx, setInfoIdx] = useState(0);
  const [showHomeInfo, setShowHomeInfo] =
    useRecoilState<boolean>(homeInfoState);

  return (
    <StyledHomeInfo>
      <TbX
        onClick={() => setShowHomeInfo(false)}
        color="white"
        className="home-info-close"
        size={32}
      />
      <div className="home-info">
        {infoIdx == 0 ? <div className="home-info-feed"></div> : null}
        {infoIdx == 1 ? <div className="home-info-message"></div> : null}
        {infoIdx == 2 ? <div className="home-info-map"></div> : null}
        {infoIdx == 3 ? (
          <div className="home-info-mobile">
            <QRCodeSVG value={`${BASE_APP_URL}mobile`}></QRCodeSVG>
          </div>
        ) : null}
      </div>
      <div className="home-info-content">
        <div
          key={`home-info-title-${infoIdx}`}
          className="home-info-content-title"
        >
          {infoData[infoIdx].title}
        </div>
        <div
          key={`home-info-description-${infoIdx}`}
          className="home-info-content-description"
        >
          {infoData[infoIdx].description}
        </div>
        <div className="home-info-content-control">
          {infoIdx > 0 ? (
            <IconBtn onClick={() => setInfoIdx(infoIdx - 1)}>
              <TbArrowBigLeftFilled size={24} />
            </IconBtn>
          ) : null}
          {infoIdx < 3 ? (
            <IconBtn onClick={() => setInfoIdx(infoIdx + 1)}>
              <TbArrowBigRightFilled size={24} />
            </IconBtn>
          ) : null}
        </div>
      </div>
    </StyledHomeInfo>
  );
}

export default HomeInfo;

const StyledHomeInfo = styled.div`
  z-index: 99;
  position: fixed;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;

  .home-info {
    position: relative;
    width: 100%;
    max-width: 1024px;

    &-close {
      z-index: 101;
      cursor: pointer;
      position: absolute;
      right: 24px;
      top: 24px;
    }

    &-feed {
      animation: focusZoom 0.3s;
      left: 24px;
      top: 48px;
      position: absolute;
      border-radius: 32px;
      width: 360px;
      height: 500px;
      box-shadow: rgba(0, 0, 0, 0.7) 0 0 0 9999px;
    }
    &-message {
      animation: focusZoom 0.3s;
      left: 400px;
      top: 48px;
      position: absolute;
      border-radius: 32px;
      width: 610px;
      height: 180px;
      box-shadow: rgba(0, 0, 0, 0.7) 0 0 0 9999px;
    }
    &-map {
      animation: focusZoom 0.3s;
      left: 400px;
      top: 200px;
      position: absolute;
      border-radius: 32px;
      width: 600px;
      height: 500px;
      box-shadow: rgba(0, 0, 0, 0.7) 0 0 0 9999px;
    }
    &-mobile {
      animation: focusZoom 0.3s;
      background-color: rgba(255, 255, 255, 0.311);
      position: relative;
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      & > svg {
        box-shadow: rgba(0, 0, 0, 0.7) 0 0 0 9999px;
        mix-blend-mode: darken;
        padding: 24px;
        border-radius: 24px;
        background-color: ${({ theme }) => theme.color.background.secondary};
        border: 4px solid ${({ theme }) => theme.color.brand.blue};
      }
    }
    &-content {
      bottom: 0px;
      position: absolute;
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding-block: 128px 32px;
      background: radial-gradient(
        100% 100% at 50% 100%,
        rgba(0, 0, 0, 1) 0%,
        rgba(0, 0, 0, 0) 100%
      );

      &-title {
        animation: floatingUp 0.3s both;
        ${({ theme }) => theme.text.header5}
        color: ${({ theme }) => theme.color.monotone.light};
      }
      &-description {
        animation: floatingUp 0.3s both;
        animation-delay: 0.2s;
        ${({ theme }) => theme.text.subtitle1}
        color: ${({ theme }) => theme.color.monotone.gray};
        margin-block: 8px;
      }
      &-control {
        display: flex;
        align-items: center;
        height: 40px;
        &-skip {
          cursor: pointer;
          margin-inline: 16px;
          ${({ theme }) => theme.text.subtitle2}
          color: ${({ theme }) => theme.color.monotone.gray};
        }
        & > button > svg {
          color: ${({ theme }) => theme.color.monotone.gray};
        }
      }
    }
  }
`;
