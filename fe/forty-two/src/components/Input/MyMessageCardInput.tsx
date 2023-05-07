import { getAccessToken, postMessage } from "../../api";
import { userState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../utils";
import CommonBtn from "../Button/CommonBtn";
import Input from "./Input";
import { useState } from "react";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type inputProps = {
  onClickCancel(): void;
};

function MyMessageCardInput({ onClickCancel }: inputProps) {
  const accessToken = useRecoilValue(userAccessTokenState);
  const [myMessageInputData, setMyMessageInputData] = useState<string>("");
  const setUserRefresh = useSetRecoilState(userState);
  const onClickPostMessage = () => {
    myMessageInputData == ""
      ? alert("내용을 입력해주세요.")
      : postMessage(accessToken, { message: myMessageInputData })
          .then((res) => {
            onClickCancel();
          })
          .catch((e) => {
            if (e.response.status == 401) {
              getAccessToken().then((res) => {
                setUserRefresh(res.data.data);
                setSessionRefreshToken(res.data.data.refreshToken);
                postMessage(res.data.data.accessToken, {
                  message: myMessageInputData,
                }).then(() => onClickCancel());
              });
            }
          });
  };

  return (
    <StyledMyMessageCardInput>
      <Input
        onChange={(e) => setMyMessageInputData(e.target.value)}
        onKeyUp={(e) => {
          switch (e.key) {
            case "Enter":
              onClickPostMessage();
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
        <CommonBtn btnType={"primary"} onClick={onClickPostMessage}>
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
