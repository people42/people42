import { ReactElement } from "react";
import styled from "styled-components";

type cardProps = { children: ReactElement; isShadowInner: boolean };

function Card({ children, isShadowInner }: cardProps) {
  return <StyledCard isShadowInner={isShadowInner}>{children}</StyledCard>;
}

export default Card;

const StyledCard = styled.div<{ isShadowInner: boolean }>`
  width: 100%;
  height: 100%;
  background-color: ${(props) =>
    props.isShadowInner
      ? props.theme.color.background.primary
      : props.theme.color.background.secondary};
  border-radius: 32px;
  ${(props) =>
    props.isShadowInner
      ? props.theme.shadow.innerShadow
      : props.theme.shadow.cardShadow};
  transition: all 0.3s;
`;
