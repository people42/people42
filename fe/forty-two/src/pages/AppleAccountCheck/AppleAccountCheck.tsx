import { postAppleUserInfo } from "../../api";
import Spinner from "../../components/Spinner/Spinner";
import { themeState } from "../../recoil/theme/atoms";
import { signUpUserState } from "../../recoil/user/atoms";
import { userLoginState } from "../../recoil/user/selectors";
import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useSetRecoilState } from "recoil";
import styled from "styled-components";

type appleAccountCheckProps = {};

function AppleAccountCheck({}: appleAccountCheckProps) {
  const setSignUpUser = useSetRecoilState<TSignUpUser>(signUpUserState);

  const navigate = useNavigate();
  const userLogin = useSetRecoilState(userLoginState);
  const [searchParams, setSeratchParams] = useSearchParams();

  const code: string | null = searchParams.get("apple_code");
  const is_signup: string = searchParams.get("is_signup") ?? "error";
  useEffect(() => {
    if (code && is_signup == "false") {
      setSignUpUser({
        platform: "apple",
        email: "null",
        nickname: null,
        o_auth_token: code,
        emoji: null,
      });
      navigate("/signup");
    } else if (code && is_signup == "true") {
      postAppleUserInfo({ appleCode: code })
        .then((res) => {
          userLogin(res.data.data);
          navigate("/");
        })
        .catch((e) => {
          alert("로그인 오류가 발생했습니다. 다시 시도해주세요.");
          navigate("/signin");
        });
    } else {
      alert("로그인 오류가 발생했습니다. 다시 시도해주세요.");
      navigate("/signin");
    }
  }, []);

  return (
    <StyledAppleAccountCheck>
      <Spinner></Spinner>
    </StyledAppleAccountCheck>
  );
}

export default AppleAccountCheck;

const StyledAppleAccountCheck = styled.div`
  width: 100vw;
  height: 100vh;
`;
