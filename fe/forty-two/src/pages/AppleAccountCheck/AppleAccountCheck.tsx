import { postCheckApple } from "../../api";
import { signUpUserState } from "../../recoil/user/atoms";
import { isLoginState } from "../../recoil/user/atoms";
import { userLoginState } from "../../recoil/user/selectors";
import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useRecoilState, useSetRecoilState } from "recoil";
import styled from "styled-components";

type appleAccountCheckProps = {};

function AppleAccountCheck({}: appleAccountCheckProps) {
  const [searchParams, setSeratchParams] = useSearchParams();
  const setSignUpUser = useSetRecoilState<TSignUpUser>(signUpUserState);

  const navigate = useNavigate();
  const userLogin = useSetRecoilState(userLoginState);

  const [isLogin, setIsLogin] = useRecoilState(isLoginState);

  useEffect(() => {
    const code = searchParams.get("code");
    if (code) {
      console.log("코드는 잘 있단다.", code);
      postCheckApple({ o_auth_token: code })
        .then((res) => {
          console.log(res);
          // if (res.data.data.accessToken == null) {
          //   setSignUpUser({
          //     platform: "apple",
          //     email: res.data.data.email,
          //     nickname: null,
          //     o_auth_token: res.data.data.access_token,
          //     emoji: null,
          //   });
          //   navigate("/signup");
          // } else {
          //   userLogin(res.data.data);
          //   localStorage.setItem("isLogin", "true");
          //   sessionStorage.setItem("refreshToken", res.data.data.refreshToken);
          //   navigate("/");
          //   setIsLogin(true);
          // }
        })
        .catch((e) => console.log(e));
    } else {
      alert("로그인 오류가 발생했습니다. 다시 시도해주세요.");
      navigate("/signin");
    }
  }, []);
  return (
    <StyledAppleAccountCheck>
      당신은 하필 애플로 로그인중입니다...ㅠ
    </StyledAppleAccountCheck>
  );
}

export default AppleAccountCheck;

const StyledAppleAccountCheck = styled.nav``;
