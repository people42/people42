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
      localStorage.setItem("apple_code", code);
    } else {
      alert("로그인 오류가 발생했습니다. 다시 시도해주세요.");
    }
    window.close();
  }, []);
  return (
    <StyledAppleAccountCheck>
      당신은 하필 애플로 로그인중입니다...ㅠ
    </StyledAppleAccountCheck>
  );
}

export default AppleAccountCheck;

const StyledAppleAccountCheck = styled.nav``;
