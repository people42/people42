import Card from "./Card";
import { ReactElement } from "react";
import styled from "styled-components";

type messageBackgroundProps = {};

function MessageBackground({}: messageBackgroundProps) {
  return (
    <StyledMessageBackground>
      <Card isShadowInner={false}>
        <>asdf</>
      </Card>
    </StyledMessageBackground>
  );
}

export default MessageBackground;

const StyledMessageBackground = styled.div``;
