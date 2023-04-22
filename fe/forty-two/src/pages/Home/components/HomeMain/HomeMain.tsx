import { HomeMyMessage } from "..";
import { MyMessageCard } from "../../../../components";
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
  display: flex;
  flex-grow: 1;
`;
