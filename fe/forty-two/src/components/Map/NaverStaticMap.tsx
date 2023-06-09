import koreaMap from "../../assets/images/map/koreaMap.png";
import { useEffect } from "react";
import styled from "styled-components";

type naverStaticMapProps = {
  setIsMapLoad: Function;
  location: TLocation | null;
};

function NaverStaticMap({ location, setIsMapLoad }: naverStaticMapProps) {
  const NAVER_MAP_CLIENT_ID = import.meta.env.VITE_NAVER_MAP_CLIENT_ID;

  useEffect(() => {
    if (location) {
      setIsMapLoad(true);
    }
  }, [location]);

  return (
    <StyledNaverStaticMap>
      {location ? (
        <img
          className="map-img"
          src={`https://naveropenapi.apigw.ntruss.com/map-static/v2/raster-cors?w=1024&h=1024&center=${location?.longitude},${location?.latitude}&level=16&X-NCP-APIGW-API-KEY-ID=${NAVER_MAP_CLIENT_ID}`}
        />
      ) : null}
      <div className="map-load">
        {!location ? (
          <>
            <div className="map-load-circle"></div>
            <div>
              <p>지도 로드중...</p>
            </div>
          </>
        ) : null}
        <img src={koreaMap}></img>
      </div>
    </StyledNaverStaticMap>
  );
}

export default NaverStaticMap;

const StyledNaverStaticMap = styled.div`
  width: 100%;
  height: 100%;
  background-color: ${({ theme }) => theme.color.background.secondary};

  .map-img {
    z-index: 1;
    position: absolute;
    animation: ${({ theme }) => (theme.isDark ? "mapInDark" : "fadeIn")} 1s;
    width: 100%;
    ${({ theme }) =>
      theme.isDark
        ? "filter: invert(95%) sepia(60%) hue-rotate(180deg) saturate(100%) brightness(1.2);"
        : ""};
  }
  .map-load {
    z-index: -1;
    width: 100%;
    height: 100%;
    &-circle {
      animation: circleSpread 1s linear infinite;
      z-index: 10;
      position: absolute;
      width: 100%;
      height: 100%;
      border-radius: 100%;
      border: 1px solid ${({ theme }) => theme.color.text.secondary};
      filter: opacity(90%);
    }
    & > div {
      ${({ theme }) => theme.text.subtitle1};
      width: 100%;
      height: 100%;
      position: absolute;
      display: flex;
      justify-content: center;
      align-items: center;
      & > p {
        z-index: 3;
      }
    }
    & > img {
      position: absolute;
      width: 100%;
      ${({ theme }) =>
        theme.isDark
          ? "filter: invert(95%) sepia(60%) hue-rotate(180deg) saturate(100%) brightness(0.8);"
          : "filter: opacity(0.5) grayscale(80%);"};
    }
  }
`;
