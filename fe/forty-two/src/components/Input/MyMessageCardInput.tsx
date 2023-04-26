import { postMessage } from "../../api";
import { userAccessTokenState } from "../../recoil/user/selectors";
import CommonBtn from "../Button/CommonBtn";
import Input from "./Input";
import { useRef, useState } from "react";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type inputProps = {
  onClickCancel(): void;
};

function MyMessageCardInput({ onClickCancel }: inputProps) {
  const accessToken = useRecoilValue(userAccessTokenState);
  const [myMessageInputData, setMyMessageInputData] = useState<string>("");

  return (
    <StyledMyMessageCardInput>
      <Input
        onChange={(e) => setMyMessageInputData(e.target.value)}
        onKeyUp={(e) => {
          switch (e.key) {
            case "Enter":
              myMessageInputData == ""
                ? alert("내용을 입력해주세요.")
                : postMessage(accessToken, {
                    message: myMessageInputData,
                  }).then((res) => onClickCancel());
              break;
            case "Escape":
              onClickCancel();
              setMyMessageInputData("");
              break;
          }
        }}
        placeholder={"지금 무슨 생각하세요?"}
      ></Input>
      <div>
        <CommonBtn
          btnType={"primary"}
          onClick={() => {
            myMessageInputData == ""
              ? alert("내용을 입력해주세요.")
              : postMessage(accessToken, { message: myMessageInputData })
                  .then((res) => onClickCancel())
                  .catch((e) => {
                    console.log(e);
                    alert("글 작성이 실패했습니다. 잠시 후 다시 시도해주세요.");
                    onClickCancel();
                    setMyMessageInputData("");
                  });
          }}
        >
          작성
        </CommonBtn>
        <CommonBtn
          btnType={"primary"}
          onClick={() => {
            onClickCancel();
            setMyMessageInputData("");
          }}
        >
          취소
        </CommonBtn>
      </div>
    </StyledMyMessageCardInput>
  );
}

export default MyMessageCardInput;

const StyledMyMessageCardInput = styled.div`
  height: 32px;
  animation: floatingDown 0.3s;

  & > input {
    margin-bottom: 8px;
  }
  & > div {
    display: flex;
    justify-content: end;
  }
`;
