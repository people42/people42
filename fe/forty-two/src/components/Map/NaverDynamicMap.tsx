import { userLocationUpdateState } from "../../recoil/location/selectors";
import { useNavermaps } from "react-naver-maps";
import { Container as MapDiv, NaverMap } from "react-naver-maps";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type naverMapProps = {};

function NaverDynamicMap({}: naverMapProps) {
  const location = useRecoilValue<TLocation | null>(userLocationUpdateState);
  const navermaps = useNavermaps();
  const center = new navermaps.LatLng(location?.latitude, location?.longitude);

  return (
    <StyledNaverMap>
      <MapDiv
        style={{
          width: "100%",
          height: "100%",
        }}
      >
        <NaverMap defaultCenter={center} />
      </MapDiv>
    </StyledNaverMap>
  );
}

export default NaverDynamicMap;

const StyledNaverMap = styled.div`
  width: 100%;
  height: 100%;
  img {
    ${({ theme }) =>
      theme.isDark
        ? "filter: invert(95%) sepia(60%) hue-rotate(180deg) saturate(100%) brightness(0.8);"
        : "filter: grayscale(20%);"};
  }
`;
