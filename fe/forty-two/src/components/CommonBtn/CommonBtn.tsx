import { ReactElement } from "react";
import styled from "styled-components";

type commonBtnProps = {
  children: string;
  btnType: "primary" | "secondary" | "disable";
};

function CommonBtn({ children, btnType }: commonBtnProps) {
  return <StyledCommonBtn btnType={btnType}>{children}</StyledCommonBtn>;
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
  &:active {
    filter: brightness(0.95);
    scale: 0.99;
  }
`;
