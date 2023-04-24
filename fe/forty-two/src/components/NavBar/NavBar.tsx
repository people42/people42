import IconButton from "../Button/IconButton";
import ThemeButton from "./ThemeButton";
import { TbBellFilled, TbSettingsFilled } from "react-icons/tb";
import styled from "styled-components";

type navBarProps = {};

function NavBar() {
  return (
    <StyledNavBar>
      <div>
        <div className="logo">logo</div>
        <div className="nav-icons">
          <ThemeButton></ThemeButton>
          <IconButton onClick={() => {}}>
            <TbBellFilled size={24} aria-label={"알림"} />
          </IconButton>
          <IconButton onClick={() => {}}>
            <TbSettingsFilled size={24} aria-label={"설정"} />
          </IconButton>
        </div>
      </div>
    </StyledNavBar>
  );
}

export default NavBar;

const StyledNavBar = styled.nav`
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 16px 24px 0px 24px;
  box-sizing: border-box;
  & svg {
    color: ${({ theme }) => theme.color.monotone.gray};
    padding: 8px;
  }
  & > div {
    width: 100%;
    height: 36px;
    max-width: 1024px;
    display: flex;
    justify-content: space-between;
  }

  .nav-icons {
    display: flex;
  }
`;
