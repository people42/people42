import { postSignupGoogle } from "../../../../api/auth";
import { CommonBtn } from "../../../../components";
import { signUpUserState } from "../../../../recoil/user/atoms";
import { userLoginState } from "../../../../recoil/user/selectors";
import _ from "lodash";
import { useNavigate } from "react-router";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type conformUserSettingProps = { onClick(e: React.MouseEvent): void };

function ConformUserSetting({ onClick }: conformUserSettingProps) {
  const signUpUser = useRecoilValue(signUpUserState);

  const navigate = useNavigate();
  const userLogin = useSetRecoilState(userLoginState);

  const alertSignUpError = (status?: number) => {
    switch (status) {
      case 409:
        alert("이미 가입된 계정입니다. 로그인해주세요.");
        navigate("/signin");
        break;
      case 500:
        alert("서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.");
        navigate("/signin");
        break;
      default:
        alert("회원가입 오류가 발생했습니다. 다시 시도해주세요.");
        navigate("/signin");
        break;
    }
  };

  const signUp = () => {
    switch (signUpUser.platform) {
      case "google":
        signUpUser.email &&
        signUpUser.nickname &&
        signUpUser.o_auth_token &&
        signUpUser.emoji
          ? postSignupGoogle({
              email: signUpUser.email,
              nickname: signUpUser.nickname,
              o_auth_token: signUpUser.o_auth_token,
              emoji: signUpUser.emoji,
            })
              .then((res) => {
                userLogin(res.data.data);
                localStorage.setItem("isLogin", "true");
                sessionStorage.setItem(
                  "refreshToken",
                  res.data.data.refreshToken
                );
                navigate("/");
              })
              .catch((e) => {
                console.log(e);
                alertSignUpError(e.response.data.status);
              })
          : alertSignUpError();
        break;
      case "apple":
        console.log("apple login 시도");
        break;
      default:
        alertSignUpError();
        break;
    }
  };

  return (
    <StyledConformUserSetting>
      <div>
        <SelectedEmojiIcon
          style={{
            backgroundImage: `url("src/assets/images/emoji/animate/${signUpUser.emoji}.gif")`,
          }}
        ></SelectedEmojiIcon>
        <p>{signUpUser.nickname}</p>
      </div>
      <CommonBtn onClick={signUp} btnType="primary">
        이 프로필로 시작하기
      </CommonBtn>
      <CommonBtn onClick={onClick} btnType="secondary">
        다시 선택할래요
      </CommonBtn>
    </StyledConformUserSetting>
  );
}

export default ConformUserSetting;

const StyledConformUserSetting = styled.div`
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
    & > p {
      ${({ theme }) => theme.text.header6}
      margin-top: 8px;
      margin-bottom: 16px;
    }
  }
`;

const SelectedEmojiIcon = styled.div`
  animation: floatingUp 0.3s;
  width: 120px;
  height: 120px;
  background-size: 100%;
`;
