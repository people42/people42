import { ReactElement } from "react";
import styled from "styled-components";

type iconButtonProps = { onClick(): void; children: ReactElement };

function IconButton({ onClick, children }: iconButtonProps) {
  return <StyledIconButton onClick={onClick}>{children}</StyledIconButton>;
}

export default IconButton;

const StyledIconButton = styled.button`
  background: none;
  border: none;
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
