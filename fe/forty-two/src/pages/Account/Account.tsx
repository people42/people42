import { Card, CommonBtn, LogoBg, NavBar } from "../../components";
import { signUpUserState, userState } from "../../recoil/user/atoms";
import { EmojiSelector, NicknamePicker } from "../SignUp/components";
import { AccountSet } from "./components";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type accountProps = {};

function Account({}: accountProps) {
  const [User, setUser] = useRecoilState(userState);
  const S3_URL = import.meta.env.VITE_S3_URL;
  const navigate = useNavigate();
  const [userPlatform, setUserPlatform] = useState<string | null>();
  useEffect(() => {
    setUserPlatform(localStorage.getItem("user_platform"));
  }, []);
  return (
    <>
      <NavBar></NavBar>
      <StyledAccount>
        <Card isShadowInner={false}>
          <>
            <img src={`${S3_URL}emoji/animate/${User?.emoji}.gif`}></img>
            <h1>{User?.nickname}</h1>
            <CommonBtn
              btnType="secondary"
              onClick={() => navigate("/account/set/nickname")}
            >
              닉네임 변경
            </CommonBtn>
            <CommonBtn
              btnType="secondary"
              onClick={() => navigate("/account/set/emoji")}
            >
              이모지 변경
            </CommonBtn>
            <CommonBtn
              btnType="secondary"
              onClick={() => {
                const confirmed = window.confirm("정말로 탈퇴하시겠습니까?");
                if (confirmed) {
                  navigate(`/withdrawal/${userPlatform}`);
                }
              }}
            >
              회원탈퇴
            </CommonBtn>
            <LogoBg isBlue={false}></LogoBg>
          </>
        </Card>
      </StyledAccount>
    </>
  );
}

export default Account;

const StyledAccount = styled.main`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 56px);
  & > div {
    width: 90%;
    max-width: 400px;
    height: 300px;
    padding: 24px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    & > img {
      height: 96px;
      margin-bottom: 8px;
    }
    & > h1 {
      ${({ theme }) => theme.text.header6}
      margin-bottom: 24px;
    }
  }
`;
