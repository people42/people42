import HomeMap from "../HomeMap/HomeMap";
import HomeMyHistory from "../HomeMyHistory/HomeMyHistory";
import HomeMyMessageReaction from "./HomeMyMessageReaction";
import MyMessageCard from "./MyMessageCard";
import { ReactElement, useState } from "react";
import styled from "styled-components";

type homeMyMessageProps = {};

function HomeMyMessage({}: homeMyMessageProps) {
  const [isMessageEdit, setIsMessageEdit] = useState<boolean>(false);

  return (
    <StyledHomeMyMessage>
      <MyMessageCard
        isMessageEdit={isMessageEdit}
        setIsMessageEdit={setIsMessageEdit}
      ></MyMessageCard>
      {isMessageEdit ? <HomeMyHistory></HomeMyHistory> : <HomeMap></HomeMap>}
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
