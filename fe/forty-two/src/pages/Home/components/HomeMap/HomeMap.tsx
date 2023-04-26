import mapArrow from "../../../../assets/images/map/mapArrow.png";
import { NaverStaticMap } from "../../../../components";
import { useState } from "react";
import styled from "styled-components";

type homeMapProps = {};

function HomeMap({}: homeMapProps) {
  const [isMapLoad, setIsMapLoad] = useState<boolean>(false);
  return (
    <StyledHomeMap>
      {isMapLoad ? (
        <div className="map-arrow">
          <img className="map-arrow-img" src={mapArrow}></img>
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
      <NaverStaticMap setIsMapLoad={setIsMapLoad}></NaverStaticMap>
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
