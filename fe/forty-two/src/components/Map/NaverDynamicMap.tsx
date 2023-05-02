import { Container as MapDiv, NaverMap } from "react-naver-maps";
import styled from "styled-components";

type naverMapProps = {
  children: JSX.Element[] | null;
  center?: any;
  bound?: any;
};

function NaverDynamicMap({ children, center, bound }: naverMapProps) {
  return (
    <StyledNaverMap>
      <MapDiv
        style={{
          width: "100%",
          height: "100%",
        }}
      >
        <NaverMap defaultCenter={center} bounds={bound}>
          {children}
        </NaverMap>
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
