import { postSignupApple, postSignupGoogle } from "../../../../api/auth";
import { CommonBtn } from "../../../../components";
import { signUpUserState } from "../../../../recoil/user/atoms";
import { userLoginState } from "../../../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../../../utils";
import _ from "lodash";
import { useState } from "react";
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
                setSessionRefreshToken(res.data.data.refreshToken);
                navigate("/");
              })
              .catch((e) => {
                alertSignUpError(e.response.data.status);
              })
          : alertSignUpError();
        break;
      case "apple":
        signUpUser.nickname && signUpUser.o_auth_token && signUpUser.emoji
          ? postSignupApple({
              email: "null",
              nickname: signUpUser.nickname,
              o_auth_token: signUpUser.o_auth_token,
              emoji: signUpUser.emoji,
            })
              .then((res) => {
                userLogin(res.data.data);
                setSessionRefreshToken(res.data.data.refreshToken);
                navigate("/");
              })
              .catch((e) => {
                alertSignUpError(e.response.data.status);
              })
          : alertSignUpError();
        break;
      default:
        alertSignUpError();
        break;
    }
  };

  const S3_URL = import.meta.env.VITE_S3_URL;
  const BASE_APP_URL = import.meta.env.VITE_BASE_APP_URL;

  const [isAgree, setIsAgree] = useState<boolean>(false);

  return (
    <StyledConformUserSetting isAgree={isAgree}>
      <div>
        <SelectedEmojiIcon
          style={{
            backgroundImage: `url("${S3_URL}emoji/animate/${signUpUser.emoji}.gif")`,
          }}
        ></SelectedEmojiIcon>
        <p>{signUpUser.nickname}</p>
        <div>
          <input
            onChange={(e) => setIsAgree(e.target.checked)}
            type="checkbox"
            id="agree"
          />
          <a
            className="agree-link"
            onClick={() =>
              window.open(
                `${BASE_APP_URL}policy?nav=false`,
                "",
                "'top=10, left=10, width=500, height=600, status=no, menubar=no, toolbar=no, resizable=no'"
              )
            }
          >
            이용약관 및 개인정보처리방침
          </a>
          <label htmlFor="agree">에 동의합니다</label>
        </div>
      </div>
      <div className="agree-btn">
        <CommonBtn
          onClick={
            isAgree
              ? signUp
              : () => {
                  alert("이용약관 및 개인정보처리방침에 동의해주세요.");
                }
          }
          btnType="primary"
        >
          이 프로필로 시작하기
        </CommonBtn>
      </div>
      <CommonBtn onClick={onClick} btnType="secondary">
        다시 선택할래요
      </CommonBtn>
    </StyledConformUserSetting>
  );
}

export default ConformUserSetting;

const StyledConformUserSetting = styled.div<{ isAgree: boolean }>`
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
  .agree-link {
    cursor: pointer;
    text-decoration: underline;
  }
  .agree-btn {
    & > button {
      ${({ isAgree }) =>
        isAgree
          ? ""
          : "filter: opacity(0.3) grayscale(100); cursor: not-allowed;"}
    }
  }
`;

const SelectedEmojiIcon = styled.div`
  animation: floatingUp 0.3s;
  width: 120px;
  height: 120px;
  background-size: 100%;
`;
