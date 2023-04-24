import { ReactElement } from "react";
import styled from "styled-components";

type floatIconBtnProps = {
  onClick(e: React.MouseEvent): void;
  children: ReactElement;
};

function FloatIconBtn({ onClick, children }: floatIconBtnProps) {
  return <StyledFloatIconBtn onClick={onClick}>{children}</StyledFloatIconBtn>;
}

export default FloatIconBtn;

const StyledFloatIconBtn = styled.button`
  background-color: ${({ theme }) => theme.color.background.secondary};
  width: 24px;
  height: 24px;
  border-radius: 12px;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 4px;
  ${({ theme }) => theme.shadow.cardShadow}
  & svg {
    color: ${({ theme }) => theme.color.text.primary};
  }

  transition: all 0.1s;
  cursor: pointer;
  &:hover {
    scale: 1.1;
    filter: ${({ theme }) =>
      theme.isDark == true ? "brightness(1.5)" : "brightness(1.2)"};
  }
  &:active {
    scale: 0.9;
  }
`;
