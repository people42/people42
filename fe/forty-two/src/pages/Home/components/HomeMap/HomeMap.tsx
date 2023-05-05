import mapArrow from "../../../../assets/images/map/mapArrow.png";
import { NaverStaticMap } from "../../../../components";
import { isLocationPermittedState } from "../../../../recoil/location/atoms";
import { userLocationUpdateState } from "../../../../recoil/location/selectors";
import { isLoginState } from "../../../../recoil/user/atoms";
import HomeMapLocation from "./HomeMapLocation";
import HomeMapPermission from "./HomeMappermission";
import { useEffect, useState } from "react";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

type homeMapProps = {};

function HomeMap({}: homeMapProps) {
  const location = useRecoilValue<TLocation | null>(userLocationUpdateState);

  const [isMapLoad, setIsMapLoad] = useState<boolean>(false);
  const [mousePosition, setMousePosition] = useState<{
    x: number;
    y: number;
  }>();
  const [arrowStyle, setArrowAngleStyle] = useState<{
    angle: number;
    distance: number;
  }>({
    angle: 0,
    distance: 1,
  });

  useEffect(() => {
    const mouseMove = (e: any) =>
      setMousePosition({ x: e.clientX, y: e.clientY });
    window.addEventListener("mousemove", mouseMove);
    return window.addEventListener("mousemove", mouseMove);
  }, []);
  useEffect(() => {
    let target = document.querySelector(".map-arrow-img");

    if (target && mousePosition) {
      const mx = mousePosition.x;
      const my = mousePosition.y;
      const ax = target.getBoundingClientRect().left + 20;
      const ay = target.getBoundingClientRect().top + 20;

      let angle = Math.atan2(ay - my, ax - mx) * (180 / Math.PI) - 90; // 두 점 사이의 각도 (라디안을 각도로 변환)

      if (angle < 0) {
        angle += 360; // 각도를 0도를 기준으로 360도 단위로 변환
      }

      let distance = 1.7 - Math.sqrt((ax - mx) ** 2 + (ay - my) ** 2) / 500;
      if (distance < 1) {
        distance = 1;
      } else if (1.7 < distance) {
        distance = 1.7;
      }

      setArrowAngleStyle({ angle: angle, distance: distance }); // 결과 출력
    }
  }, [mousePosition]);

  const isLocationPermitted = useRecoilValue(isLocationPermittedState);
  const isLogin = useRecoilValue(isLoginState);

  return isLocationPermitted ? (
    <StyledHomeMap>
      {isMapLoad ? (
        <div className="map-arrow">
          <img
            className="map-arrow-img"
            src={mapArrow}
            style={{
              transform: `rotate(${arrowStyle.angle}deg) scale(${arrowStyle.distance})`,
            }}
          ></img>
        </div>
      ) : null}
      <div className="map-mask">
        {isMapLoad ? (
          <div className="map-circle">
            <div className="map-circle-rader"></div>
            <div className="map-circle-1"></div>
            <div className="map-circle-2"></div>
            <div className="map-circle-3"></div>
          </div>
        ) : null}
      </div>
      {isLogin ? <HomeMapLocation></HomeMapLocation> : null}
      <NaverStaticMap
        setIsMapLoad={setIsMapLoad}
        location={location}
      ></NaverStaticMap>
    </StyledHomeMap>
  ) : (
    <StyledHomeMap>
      <HomeMapPermission></HomeMapPermission>
    </StyledHomeMap>
  );
}

export default HomeMap;

const StyledHomeMap = styled.section`
  position: fixed;
  z-index: -1;
  bottom: -100px;
  width: 100vh;
  height: 100vh;
  .map-arrow {
    animation: popIn 0.3s;
    z-index: 10;
    position: absolute;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    &-img {
      width: 40px;
      height: 40px;
    }
  }

  .map-mask {
    z-index: 3;
    position: absolute;
    width: 100%;
    height: 100%;
    background: radial-gradient(
      50% 50% at 50% 50%,
      transparent 50%,
      ${({ theme }) => theme.color.background.primary} 100%
    );
  }
  .map-circle {
    position: absolute;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    &-rader {
      animation: circleSpread 5s infinite linear;
      animation-fill-mode: both;
      animation-delay: 0.2s;
      position: absolute;
      width: 100%;
      height: 100%;
      border-radius: 100%;
      background: radial-gradient(
        50% 50% at 50% 50%,
        transparent 70%,
        ${({ theme }) => theme.color.brand.blue + "40"} 100%
      );
    }
    &-1 {
      animation: circleIn30 0.5s;
      animation-fill-mode: both;
      animation-delay: 0.2s;
      position: absolute;
      width: 100%;
      height: 100%;
      border-radius: 100%;
      border: 1px solid ${({ theme }) => theme.color.text.secondary};
    }
    &-2 {
      animation: circleIn60 0.5s;
      animation-fill-mode: both;
      animation-delay: 0.4s;
      position: absolute;
      width: 70%;
      height: 70%;
      border-radius: 100%;
      border: 1px solid ${({ theme }) => theme.color.text.secondary};
    }
    &-3 {
      animation: circleIn60 0.5s;
      animation-fill-mode: both;
      animation-delay: 0.6s;
      position: absolute;
      width: 40%;
      height: 40%;
      border-radius: 100%;
      border: 1px solid ${({ theme }) => theme.color.text.secondary};
    }
  }
`;
