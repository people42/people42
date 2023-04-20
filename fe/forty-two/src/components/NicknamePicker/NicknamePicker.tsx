import styled from "styled-components";
import CommonBtn from "../CommonBtn/CommonBtn";
import Card from "../Card/Card";
import RandomNicknameCard from "./RandomNicknameCard";

type nicknamePickerProps = {};

function NicknamePicker() {
  return (
    <StyledNicknamePicker>
      <div>
        <div>
          <RandomNicknameCard></RandomNicknameCard>
          <RandomNicknameCard></RandomNicknameCard>
          <RandomNicknameCard></RandomNicknameCard>
        </div>
        <p>다시 고를래요</p>
        <button>리트</button>
      </div>
      <CommonBtn btnType="primary">결정했어요</CommonBtn>
    </StyledNicknamePicker>
  );
}

export default NicknamePicker;

const StyledNicknamePicker = styled.div`
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-grow: 1;
  & > div {
    flex-grow: 1;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    & > div {
      display: flex;
      width: 100%;
      height: 52px;
      margin-bottom: 40px;
    }
    & > p {
      color: ${({ theme }) => theme.color.text.secondary};
      ${({ theme }) => theme.text.caption};
    }
  }
`;
