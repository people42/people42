import { GoogleMap } from "../../../../components";
import styled from "styled-components";

type homeMapProps = {};

function HomeMap({}: homeMapProps) {
  return (
    <StyledHomeMap>
      <div className="map-mask">
        <div className="map-circle-1"></div>
        <div className="map-circle-2"></div>
        <div className="map-circle-3"></div>
      </div>
      <GoogleMap></GoogleMap>
    </StyledHomeMap>
  );
}

export default HomeMap;

const StyledHomeMap = styled.nav`
  position: fixed;
  z-index: -10;
  bottom: -100px;
  width: 100vh;
  height: 100vh;
  .map-mask {
    position: absolute;
    width: 100%;
    height: 100%;
    background: radial-gradient(
      50% 50% at 50% 50%,
      transparent 50%,
      ${({ theme }) => theme.color.background.primary} 100%
    );
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .map-circle-1 {
    position: absolute;
    width: 100%;
    height: 100%;
    border-radius: 100%;
    border: 1px solid ${({ theme }) => theme.color.text.secondary};
    filter: opacity(30%);
  }
  .map-circle-2 {
    position: absolute;
    width: 70%;
    height: 70%;
    border-radius: 100%;
    border: 1px solid ${({ theme }) => theme.color.text.secondary};
    filter: opacity(60%);
  }
  .map-circle-3 {
    position: absolute;
    width: 40%;
    height: 40%;
    border-radius: 100%;
    border: 1px solid ${({ theme }) => theme.color.text.secondary};
    filter: opacity(90%);
  }
`;
