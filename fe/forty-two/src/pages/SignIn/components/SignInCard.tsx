import appleLogo from "../../../assets/images/logo/apple.png";
import googleLogo from "../../../assets/images/logo/google.png";
import { Card } from "../../../components/index";
import SocialLoginBtn from "./SocialLoginBtn";
import { useGoogleLogin } from "@react-oauth/google";
import React from "react";
import styled from "styled-components";

function SignInCard() {
  const loginWithGoogle = useGoogleLogin({
    onSuccess: (tokenResponse) => console.log(tokenResponse),
  });
  const loginWithApple = (e: React.MouseEvent) => {
    console.log("loginWithApple");
  };
  return (
    <StyledSignInCard>
      <section>
        <Card isShadowInner={false}>
          <div>
            <h1>로그인 또는 회원가입</h1>
            <SocialLoginBtn
              logoImg={googleLogo}
              bgColor={"#ffffff"}
              textColor={"#000000"}
              label={"구글로 계속하기"}
              onClick={(e) => loginWithGoogle()}
            ></SocialLoginBtn>
            <SocialLoginBtn
              logoImg={appleLogo}
              bgColor={"#000000"}
              textColor={"#ffffff"}
              label={"애플로 계속하기"}
              onClick={loginWithApple}
            ></SocialLoginBtn>
          </div>
        </Card>
      </section>
    </StyledSignInCard>
  );
}

export default React.memo(SignInCard);

const StyledSignInCard = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  & > section {
    width: 420px;
    height: 280px;
    display: flex;
    justify-content: center;
    align-items: center;
    & div {
      width: 100%;
      height: 100%;
      margin: 24px;
      padding: 24px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      & > h1 {
        ${({ theme }) => theme.text.header6};
        margin-bottom: 16px;
      }
    }
  }
`;
