import { postCheckGoogle } from "../../../api/auth";
import appleLogo from "../../../assets/images/logo/apple.png";
import googleLogo from "../../../assets/images/logo/google.png";
import { Card } from "../../../components/index";
import { signUpUserState } from "../../../recoil/user/atoms";
import { userLoginState } from "../../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../../utils";
import SocialLoginBtn from "./SocialLoginBtn";
import { useGoogleLogin } from "@react-oauth/google";
import React from "react";
import { useNavigate } from "react-router";
import { useSetRecoilState } from "recoil";
import styled from "styled-components";

function SignInCard() {
  const setSignUpUser = useSetRecoilState<TSignUpUser>(signUpUserState);
  const navigate = useNavigate();
  const userLogin = useSetRecoilState(userLoginState);

  const signInWithGoogle = useGoogleLogin({
    onSuccess: (tokenRes) => {
      postCheckGoogle({ o_auth_token: tokenRes.access_token })
        .then((res) => {
          if (res.data.data.accessToken == null) {
            setSignUpUser({
              platform: "google",
              email: res.data.data.email,
              nickname: null,
              o_auth_token: tokenRes.access_token,
              emoji: null,
            });
            navigate("/signup");
          } else {
            userLogin(res.data.data);
            setSessionRefreshToken(res.data.data.refreshToken);
            navigate("/");
          }
        })
        .catch((e) => {
          alert("로그인 오류가 발생했습니다. 다시 시도해주세요.");
        });
    },
  });

  const API_URL = import.meta.env.VITE_API_URL;

  const getAppleSignInCode = () => {
    const config = {
      client_id: "com.cider.fortytwo", // This is the service ID we created.
      redirect_uri: `${API_URL}auth/check/apple/web`, // As registered along with our service ID
      response_type: "code id_token",
      state: "origin:web", // Any string of your choice that you may use for some logic. It's optional and you may omit it.
      scope: "email", // To tell apple we want the user name and emails fields in the response it sends us.
      response_mode: "form_post",
      m: 11,
      v: "1.5.4",
    };

    const queryString = Object.entries(config)
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join("&");

    location.href = `https://appleid.apple.com/auth/authorize?${queryString}`;
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
              onClick={(e) => {
                localStorage.setItem("user_platform", "google");
                signInWithGoogle();
              }}
            ></SocialLoginBtn>
            <SocialLoginBtn
              logoImg={appleLogo}
              bgColor={"#000000"}
              textColor={"#ffffff"}
              label={"애플로 계속하기"}
              onClick={() => {
                localStorage.setItem("user_platform", "apple");
                getAppleSignInCode();
              }}
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
