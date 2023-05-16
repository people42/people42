import { getAccessToken, putEmoji, putNickname } from "../../../../api";
import { Card, CommonBtn, LogoBg } from "../../../../components";
import { signUpUserState, userState } from "../../../../recoil/user/atoms";
import { userAccessTokenState } from "../../../../recoil/user/selectors";
import { EmojiSelector, NicknamePicker } from "../../../SignUp/components";
import { useLocation, useNavigate } from "react-router";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type accountSetProps = {};

function AccountSet({}: accountSetProps) {
  const location = useLocation().pathname.replace("/account/set/", "");
  const navigate = useNavigate();
  const [signUpUser, setSignUpUser] = useRecoilState(signUpUserState);
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);

  const changeEmoji = (emoji: string) => {
    putEmoji(accessToken, emoji)
      .then((res) => {
        alert("이모지를 성공적으로 변경했습니다.");
        window.location.replace("/");
      })
      .catch((e) => {
        if (e.response.status == 401) {
          getAccessToken().then((res) => {
            setUserRefresh(res.data.data);
            putEmoji(res.data.data.accessToken, emoji).then((res) => {
              alert("이모지를 성공적으로 변경했습니다.");
              window.location.replace("/");
            });
          });
        }
      });
  };
  const changeNickname = (nickname: string) => {
    putNickname(accessToken, nickname)
      .then((res) => {
        alert("닉네임을 성공적으로 변경했습니다.");
        window.location.replace("/");
      })
      .catch((e) => {
        if (e.response.status == 401) {
          getAccessToken().then((res) => {
            setUserRefresh(res.data.data);
            putNickname(res.data.data.accessToken, nickname).then((res) => {
              alert("닉네임을 성공적으로 변경했습니다.");
              window.location.replace("/");
            });
          });
        }
      });
  };
  return (
    <StyledAccountSet>
      {location == "emoji" ? (
        <>
          <h1>이모지 변경</h1>
          <Card isShadowInner={false}>
            <>
              <EmojiSelector
                onClick={() => {
                  if (signUpUser.emoji) changeEmoji(signUpUser.emoji);
                }}
              ></EmojiSelector>
              <CommonBtn btnType="secondary" onClick={() => navigate(-1)}>
                취소
              </CommonBtn>
              <LogoBg isBlue={false}></LogoBg>
            </>
          </Card>
        </>
      ) : null}
      {location == "nickname" ? (
        <>
          <h1>닉네임 변경</h1>
          <Card isShadowInner={false}>
            <>
              <NicknamePicker
                onClick={() => {
                  if (signUpUser.nickname) changeNickname(signUpUser.nickname);
                }}
              ></NicknamePicker>
              <CommonBtn btnType="secondary" onClick={() => navigate(-1)}>
                취소
              </CommonBtn>
              <LogoBg isBlue={false}></LogoBg>
            </>
          </Card>
        </>
      ) : null}
    </StyledAccountSet>
  );
}

export default AccountSet;

const StyledAccountSet = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  & > div {
    width: 90%;
    max-width: 400px;
    height: 350px;
    padding: 24px;
    display: flex;
    flex-direction: column;
  }
  & > h1 {
    ${({ theme }) => theme.text.header5}
    margin-bottom: 24px;
  }
`;
