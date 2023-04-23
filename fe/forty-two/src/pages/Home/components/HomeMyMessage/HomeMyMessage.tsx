import { MyMessageCard } from "../../../../components";
import { ReactElement } from "react";
import styled from "styled-components";

type homeMyMessageProps = {};

function HomeMyMessage({}: homeMyMessageProps) {
  return (
    <StyledHomeMyMessage>
      <MyMessageCard></MyMessageCard>
    </StyledHomeMyMessage>
  );
}

export default HomeMyMessage;

const StyledHomeMyMessage = styled.section`
  width: 100%;
  display: flex;
  justify-content: center;
`;
