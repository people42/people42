import { themeState } from "../../recoil/theme/atoms";
import { ReactElement } from "react";
import { TbSunFilled, TbMoonFilled } from "react-icons/tb";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type themeButtonProps = {};

function ThemeBtn({}: themeButtonProps) {
  const [isDark, setIsDark] = useRecoilState(themeState);

  return (
    <StyledThemeButton
      onClick={(e) => {
        setIsDark(isDark ? false : true);
        localStorage.setItem("isDark", (!isDark).toString());
      }}
    >
      {isDark ? (
        <TbSunFilled size={24} aria-label={"테마 주간 모드"} />
      ) : (
        <TbMoonFilled size={24} aria-label={"테마 야간 모드"} />
      )}
    </StyledThemeButton>
  );
}

export default ThemeBtn;

const StyledThemeButton = styled.button`
  background: none;
  border: none;
  transition: all 0.1s;
  cursor: pointer;
  &:hover {
    scale: 1.1;
    & > svg {
      color: ${({ theme }) => (theme.isDark ? "#FDFF9C" : "#FFD954")};
    }
  }
  &:active {
    scale: 0.9;
  }
`;
