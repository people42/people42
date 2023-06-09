import { ReactElement } from "react";
import styled from "styled-components";

type iconButtonProps = { onClick(): void; children: ReactElement };

function IconBtn({ onClick, children }: iconButtonProps) {
  return <StyledIconButton onClick={onClick}>{children}</StyledIconButton>;
}

export default IconBtn;

const StyledIconButton = styled.button`
  background: none;
  border: none;
  transition: all 0.1s;
  cursor: pointer;
  &:hover {
    filter: ${({ theme }) =>
      theme.isDark == true ? "brightness(1.5)" : "brightness(1.2)"};
  }
  &:active {
    scale: 0.9;
  }
`;
