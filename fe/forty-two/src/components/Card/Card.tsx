import { ReactElement } from "react";
import styled from "styled-components";

type cardProps = {
  children: ReactElement;
  isShadowInner: boolean;
  onClick?(): void;
};

function Card({ children, isShadowInner, onClick }: cardProps) {
  return (
    <StyledCard isShadowInner={isShadowInner} onClick={onClick}>
      {children}
    </StyledCard>
  );
}

export default Card;

const StyledCard = styled.div<{ isShadowInner: boolean }>`
  width: 100%;
  height: 100%;
  background-color: ${(props) =>
    props.isShadowInner
      ? props.theme.color.background.primary
      : props.theme.color.background.secondary};
  border-radius: 24px;
  ${(props) =>
    props.isShadowInner
      ? props.theme.shadow.innerShadow
      : props.theme.shadow.cardShadow};
  transition: all 0.3s;
`;
