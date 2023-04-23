import { MessageCard } from "../../../../components";
import HomeTimelineGroup from "./HomeTimelineGroup";
import styled from "styled-components";

function HomeTimeline() {
  return (
    <StyledHomeTimeline>
      <HomeTimelineGroup></HomeTimelineGroup>
    </StyledHomeTimeline>
  );
}

export default HomeTimeline;

const StyledHomeTimeline = styled.section`
  width: 400px;
`;
