import { themeState } from "../../recoil/theme/atoms";
import { ReactElement } from "react";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type themeButtonProps = {};

function ThemeButton({}: themeButtonProps) {
  const [isDark, setIsDark] = useRecoilState(themeState);

  return (
    <StyledThemeButton onClick={(e) => setIsDark(!isDark)}>
      <div>{isDark ? "dark" : "light"}</div>
    </StyledThemeButton>
  );
}

export default ThemeButton;

const StyledThemeButton = styled.button``;
