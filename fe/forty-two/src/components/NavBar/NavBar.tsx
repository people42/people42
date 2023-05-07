import animatedLogo from "../../assets/images/logo/animatedLogo_w120.gif";
import { isLoginState } from "../../recoil/user/atoms";
import IconBtn from "../Button/IconBtn";
import NotificationNavBtn from "../Button/NotificationNavBtn";
import ThemeBtn from "../Button/ThemeBtn";
import NavModal from "../Modal/NavModal";
import { useState } from "react";
import { TbBellFilled, TbSettingsFilled } from "react-icons/tb";
import { useNavigate } from "react-router";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type navBarProps = {};

function NavBar() {
  const [isSettingModalOn, setIsSettingModalOn] = useState(false);
  const [isNotificationModalOn, setIsNotificationModalOn] = useState(false);

  const closeModal = () => {
    setIsSettingModalOn(false);
    setIsNotificationModalOn(false);
  };

  const navigate = useNavigate();
  const isLogin = useRecoilValue(isLoginState);

  return (
    <StyledNavBar>
      <div>
        <div className="logo" onClick={() => navigate("/")}>
          <img src={animatedLogo} alt="42-logo" aria-label="home link button" />
        </div>
        <div className="nav-icons">
          {isSettingModalOn ? (
            <>
              <NavModal type="setting" closeModal={closeModal}></NavModal>
              <div
                onClick={() => closeModal()}
                className="modal-background"
              ></div>
            </>
          ) : null}
          {isNotificationModalOn ? (
            <>
              <NavModal type="notification" closeModal={closeModal}></NavModal>
              <div
                onClick={() => closeModal()}
                className="modal-background"
              ></div>
            </>
          ) : null}
          <ThemeBtn></ThemeBtn>
          {isLogin === true ? (
            <>
              <NotificationNavBtn
                isNotificationModalOn={isNotificationModalOn}
                setIsNotificationModalOn={setIsNotificationModalOn}
              ></NotificationNavBtn>
              <IconBtn onClick={() => setIsSettingModalOn(!isSettingModalOn)}>
                <TbSettingsFilled size={24} aria-label={"설정"} />
              </IconBtn>
            </>
          ) : null}
        </div>
      </div>
    </StyledNavBar>
  );
}

export default NavBar;

const StyledNavBar = styled.nav`
  position: sticky;
  top: 0px;
  z-index: 99;
  background-color: ${({ theme }) => theme.color.background.primary + "60"};
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 16px 24px 0px 24px;
  box-sizing: border-box;
  .logo {
    height: 40px;
    & > img {
      height: 100%;
      filter: opacity(0.8);
    }
    cursor: pointer;
    transition: all 0.1s;
    &:hover {
      filter: ${({ theme }) =>
        theme.isDark == true ? "brightness(1.5)" : "brightness(1.2)"};
    }
    &:active {
      transform: scale(0.98);
    }
  }
  & svg {
    color: ${({ theme }) => theme.color.monotone.gray};
    padding: 8px;
  }
  & > div {
    width: 100%;
    height: 40px;
    max-width: 1024px;
    display: flex;
    justify-content: space-between;
  }

  .nav-icons {
    display: flex;
    position: relative;
  }

  .modal-background {
    animation: fadeIn 0.5s;
    z-index: 4;
    position: fixed;
    top: 0px;
    left: 0px;
    background-color: #00000027;
    width: 100vw;
    height: 100vh;
  }
`;
