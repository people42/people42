import Card from "../Card/Card";
import NavModalSetting from "./NavModalSetting";
import { TbBellFilled, TbSettingsFilled, TbX } from "react-icons/tb";
import styled from "styled-components";

type navModalProps = {
  closeModal(): void;
  type: string;
};

function NavModal({ closeModal, type }: navModalProps) {
  return (
    <StyledNavModal type={type}>
      <Card isShadowInner={false}>
        <div className="modal">
          <div className="modal-header">
            <p className="modal-header-title">
              {type == "notification"
                ? `${"ㅁㄴㅇ"}건의 새로운 알림`
                : `${"낙네임ㄴ"}`}
            </p>
            <TbX
              className="modal-header-close-icon"
              onClick={closeModal}
              size={24}
            />
          </div>
          <div className="modal-body">
            <NavModalSetting></NavModalSetting>
          </div>
        </div>
      </Card>
    </StyledNavModal>
  );
}

export default NavModal;

const StyledNavModal = styled.nav<{ type: string }>`
  animation: modalOn 0.1s;
  z-index: 10;
  position: absolute;
  top: 0px;
  right: ${({ type }) => (type == "notification" ? "52px" : "0px")};

  .modal {
    padding: 8px;
    width: 300px;
    height: 500px;

    &-header {
      width: 100%;
      display: flex;
      align-items: center;

      &-title {
        ${({ theme }) => theme.text.subtitle1}
        margin-inline: 8px;
        flex-grow: 1;
      }

      &-close-icon {
        cursor: pointer;

        :hover {
          transform: scale(1.1);
        }
      }
    }
    &-body {
      margin: 8px;
      ${({ theme }) => theme.text.body2}
    }
  }
`;
