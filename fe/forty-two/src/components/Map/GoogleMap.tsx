import { useEffect, useState } from "react";
import styled from "styled-components";

type googleMapProps = {};

function GoogleMap({}: googleMapProps) {
  const NAVER_MAP_CLIENT_ID = import.meta.env.VITE_NAVER_MAP_CLIENT_ID;
  const [myLocation, setMyLocation] = useState<{
    lat: number;
    lng: number;
  }>();
  useEffect(() => {
    navigator.geolocation.getCurrentPosition((position) => {
      setMyLocation({
        lat: position.coords.latitude,
        lng: position.coords.longitude,
      });
    });
  }, []);
  useEffect(() => {
    console.log(myLocation);
  }, [myLocation]);

  return (
    <StyledGoogleMap>
      <img
        src={`https://naveropenapi.apigw.ntruss.com/map-static/v2/raster-cors?w=1024&h=1024&center=${myLocation?.lng},${myLocation?.lat}&level=16&X-NCP-APIGW-API-KEY-ID=${NAVER_MAP_CLIENT_ID}`}
      />
    </StyledGoogleMap>
  );
}

export default GoogleMap;

const StyledGoogleMap = styled.nav`
  width: 100%;
  height: 100%;
  background-color: ${({ theme }) => theme.color.text.secondary};

  & > img {
    width: 100%;
    ${({ theme }) =>
      theme.isDark
        ? "filter: invert(100%) sepia(65%) hue-rotate(190deg) saturate(80%) brightness(2);"
        : ""};
  }
`;
