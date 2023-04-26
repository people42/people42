import { HomeMyMessage } from "..";
import HomeTimeline from "../HomeTimeline/HomeTimeline";
import styled from "styled-components";

function HomeMain() {
  return (
    <StyledHomeMain>
      <HomeTimeline></HomeTimeline>
      <HomeMyMessage></HomeMyMessage>
    </StyledHomeMain>
  );
}

export default HomeMain;

const StyledHomeMain = styled.main`
  width: 100%;
  height: 100%;
  max-width: 1024px;
  display: flex;
  flex-grow: 1;
`;
