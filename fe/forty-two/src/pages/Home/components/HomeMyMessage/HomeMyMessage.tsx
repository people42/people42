import { MyMessageCard } from "../../../../components";
import HomeMap from "../HomeMap/HomeMap";
import HomeMyHistory from "../HomeMyHistory/HomeMyHistory";
import { ReactElement } from "react";
import styled from "styled-components";

type homeMyMessageProps = {};

function HomeMyMessage({}: homeMyMessageProps) {
  return (
    <StyledHomeMyMessage>
      <MyMessageCard></MyMessageCard>
      <HomeMyHistory></HomeMyHistory>
      <HomeMap></HomeMap>
    </StyledHomeMyMessage>
  );
}

export default HomeMyMessage;

const StyledHomeMyMessage = styled.section`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
`;
