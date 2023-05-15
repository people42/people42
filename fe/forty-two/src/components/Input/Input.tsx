import { useEffect, useRef } from "react";
import styled from "styled-components";

type inputProps = {
  onChange(e: React.ChangeEvent<HTMLInputElement>): void;
  onKeyUp(e: React.KeyboardEvent<HTMLInputElement>): void;
  placeholder: string;
};

function Input({ onChange, onKeyUp, placeholder }: inputProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  useEffect(() => {
    if (inputRef.current !== null) inputRef.current.focus();
  });

  return (
    <StyledCard
      ref={inputRef}
      onChange={(e) => onChange(e)}
      onKeyUp={(e) => onKeyUp(e)}
      placeholder={placeholder}
    ></StyledCard>
  );
}

export default Input;

const StyledCard = styled.input`
  width: 100%;
  height: 32px;
  border: none;
  border-radius: 24px;
  padding-inline: 8px;
  box-sizing: border-box;
  ${({ theme }) => theme.shadow.innerShadow};
  ${({ theme }) => theme.text.subtitle1};
  transition: all 0.3s;
  :focus {
    outline: 3px;
    outline-color: ${({ theme }) => theme.color.monotone.lightTranslucent};
    outline-style: solid;
  }
`;
