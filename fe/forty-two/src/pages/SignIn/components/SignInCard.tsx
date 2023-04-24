import { postCheckGoogle } from "../../../api/auth";
import appleLogo from "../../../assets/images/logo/apple.png";
import googleLogo from "../../../assets/images/logo/google.png";
import { Card } from "../../../components/index";
import { signUpUserState } from "../../../recoil/auth/atoms";
import SocialLoginBtn from "./SocialLoginBtn";
import { useGoogleLogin } from "@react-oauth/google";
import React from "react";
import { useNavigate } from "react-router";
import { useSetRecoilState } from "recoil";
import styled from "styled-components";

function SignInCard() {
  const setSignUpUser = useSetRecoilState<TSignUpUser>(signUpUserState);
  let navigate = useNavigate();

  const loginWithGoogle = useGoogleLogin({
    onSuccess: (tokenRes) => {
      postCheckGoogle({ o_auth_token: tokenRes.access_token })
        .then((res) => {
          console.log(res.data);
          if (res.data.data.accessToken == null) {
            console.log("회원가입 필요");
            setSignUpUser({
              platform: "google",
              email: res.data.data.email,
              nickname: null,
              o_auth_token: null,
              color: null,
              emoji: null,
            });
            navigate("/signup");
          } else {
            console.log("이미 가입된 회원입니다.");
            navigate("/");
          }
        })
        .catch((error) => {
          console.log(error.data);
          alert("로그인 오류가 발생했습니다. 다시 시도해주세요.");
        });
    },
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
