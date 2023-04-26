import IconBtn from "../Button/IconBtn";
import ThemeBtn from "../Button/ThemeBtn";
import NavModal from "../Modal/NavModal";
import { useState } from "react";
import { TbBellFilled, TbSettingsFilled } from "react-icons/tb";
import { useNavigate } from "react-router";
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

  return (
    <StyledNavBar>
      <div>
        <div className="logo" onClick={() => navigate("/")}>
          logo
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
          <IconBtn
            onClick={() => setIsNotificationModalOn(!isNotificationModalOn)}
          >
            <TbBellFilled size={24} aria-label={"알림"} />
          </IconBtn>
          <IconBtn onClick={() => setIsSettingModalOn(!isSettingModalOn)}>
            <TbSettingsFilled size={24} aria-label={"설정"} />
          </IconBtn>
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
