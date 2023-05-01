import mapArrow from "../../../../assets/images/map/mapArrow.png";
import { NaverStaticMap } from "../../../../components";
import { placeState } from "../../../../recoil/place/atoms";
import { useEffect, useState } from "react";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type homeMapProps = {};

function HomeMap({}: homeMapProps) {
  const [isMapLoad, setIsMapLoad] = useState<boolean>(false);
  const [placeData, setPlaceData] = useRecoilState(placeState);
  return (
    <StyledHomeMap>
      {isMapLoad ? (
        <div className="map-point">
          <div className="map-point-circle"></div>
        </div>
      ) : null}
      <div className="map-mask">
        {isMapLoad ? (
          <div className="map-circle">
            <div className="map-circle-1"></div>
            <div className="map-circle-2"></div>
            <div className="map-circle-3"></div>
          </div>
        ) : null}
      </div>
      <NaverStaticMap
        setIsMapLoad={setIsMapLoad}
        location={
          placeData
            ? {
                latitude: placeData.placeWithTimeAndGpsInfo.placeLatitude,
                longitude: placeData.placeWithTimeAndGpsInfo.placeLongitude,
              }
            : null
        }
      ></NaverStaticMap>
    </StyledHomeMap>
  );
}

export default HomeMap;

const StyledHomeMap = styled.section`
  position: fixed;
  z-index: -10;
  bottom: -100px;
  width: 100vh;
  height: 100vh;
  .map-point {
    animation: popIn 0.3s;
    z-index: 10;
    position: absolute;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    &-circle {
      width: 24px;
      height: 24px;
      border-radius: 20px;
      background-color: ${({ theme }) => theme.color.monotone.gray};
      border: 4px solid ${({ theme }) => theme.color.monotone.light};
      ${({ theme }) => theme.shadow.iconShadow}
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
