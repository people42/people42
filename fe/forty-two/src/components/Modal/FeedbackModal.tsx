import { postFeedback } from "../../api/feedback";
import CommonBtn from "../Button/CommonBtn";
import Card from "../Card/Card";
import { useState } from "react";
import { TbX } from "react-icons/tb";
import styled from "styled-components";

type navModalProps = {
  closeModal(): void;
};

function FeedbackModal({ closeModal }: navModalProps) {
  const [feedbackInputData, setFeedbackInputData] = useState<string>("");
  const onClickPostFeedback = () => {
    feedbackInputData == ""
      ? alert("내용을 입력해주세요.")
      : postFeedback(feedbackInputData)
          .then((res) => {
            alert("감사합니다. 빠른 검토 후 반영하도록 하겠습니다.");
            closeModal();
          })
          .catch((e) => {
            alert("전송 중 오류가 발생했습니다. 다시 시도해주세요.");
          });
  };
  return (
    <StyledNavModal>
      <Card isShadowInner={false}>
        <div className="modal">
          <div className="modal-header">
            <p className="modal-header-title">피드백</p>
            <TbX
              className="modal-header-close-icon"
              onClick={closeModal}
              size={24}
            />
          </div>
          <div className="modal-body">
            <textarea
              placeholder="내용은 익명으로 전송됩니다."
              className="modal-body-textarea"
              onChange={(e) => setFeedbackInputData(e.target.value)}
            ></textarea>
          </div>
          <CommonBtn
            onClick={() => {
              onClickPostFeedback();
            }}
            btnType="primary"
          >
            전송
          </CommonBtn>
        </div>
      </Card>
    </StyledNavModal>
  );
}

export default FeedbackModal;

const StyledNavModal = styled.div`
  animation: modalOn 0.1s;
  z-index: 10;
  position: absolute;
  top: 0px;
  right: 158px;

  .modal {
    padding: 16px;
    width: 300px;
    display: flex;
    flex-direction: column;

    &-header {
      width: 100%;
      display: flex;
      align-items: center;

      &-title {
        ${({ theme }) => theme.text.header6}
        margin-inline: 8px;
        flex-grow: 1;
        margin-bottom: 8px;
      }

      &-close-icon {
        margin: -8px -8px 0px 0px;
        cursor: pointer;

        :hover {
          filter: ${({ theme }) =>
            theme.isDark == true ? "brightness(1.5)" : "brightness(1.2)"};
        }
      }
    }
    &-body {
      ${({ theme }) => theme.text.body2}
      &-textarea {
        padding: 8px;
        box-sizing: border-box;
        width: 100%;
        height: 160px;
        border-radius: 16px;
        ${({ theme }) => theme.text.body2}
        color: ${({ theme }) => theme.color.text.primary};
        background-color: ${({ theme }) => theme.color.background.primary};
      }
    }
  }
`;
