import { ReactElement } from "react";
import styled from "styled-components";

type commonBtnProps = {
  children: string;
  onClick(e: React.MouseEvent): void;
  btnType: "primary" | "secondary" | "disable";
};

function CommonBtn({ children, onClick, btnType }: commonBtnProps) {
  return (
    <StyledCommonBtn onClick={onClick} btnType={btnType}>
      {children}
    </StyledCommonBtn>
  );
}

export default CommonBtn;

const StyledCommonBtn = styled.button<{ btnType: string }>`
  border: none;
  background-color: ${(props) =>
    props.btnType == "primary" ? props.theme.color.brand.blue : "transparent"};
  color: ${(props) =>
    props.btnType == "primary"
      ? props.theme.color.monotone.light
      : props.theme.color.brand.blue};
  border-radius: 32px;
  height: 36px;
  padding-inline: 16px;
  ${({ theme }) => theme.text.button}
  cursor: pointer;
  transition: all 0.1s;
  &:hover {
    filter: brightness(0.96);
  }

  &:active {
    filter: brightness(0.92);
    scale: 0.99;
  }
`;
