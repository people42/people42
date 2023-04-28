import { getAccessToken, postWithdrawal } from "../../api";
import { userState } from "../../recoil/user/atoms";
import { userLogoutState } from "../../recoil/user/selectors";
import { removeLocalIsLogin, removeSessionRefreshToken } from "../../utils";
import NavModalSettingRow from "./NavModalSettingRow";
import { useNavigate } from "react-router";
import { useRecoilState, useSetRecoilState } from "recoil";
import styled from "styled-components";

type navModalSettingProps = {};

function NavModalSetting({}: navModalSettingProps) {
  const navigate = useNavigate();
  const [user, userLogout] = useRecoilState(userLogoutState);
  const setUserRefresh = useSetRecoilState(userState);

  return (
    <StyledNavModalSetting>
      <NavModalSettingRow
        onClick={() => {
          navigate("/policy");
        }}
      >
        이용약관 및 개인정보처리방침
      </NavModalSettingRow>
      <NavModalSettingRow
        onClick={() => {
          userLogout(user);
          removeLocalIsLogin();
          removeSessionRefreshToken();
          alert("안전하게 로그아웃 되었습니다.");
          navigate("/signin");
        }}
      >
        로그아웃
      </NavModalSettingRow>
      <NavModalSettingRow
        onClick={() => {
          if (user?.accessToken) {
            postWithdrawal(user?.accessToken)
              .then((res) => {
                removeLocalIsLogin();
                removeSessionRefreshToken();
                alert("정상적으로 탈퇴 되었습니다.");
                navigate("/signin");
              })
              .catch((e) => {
                if (e.response.status == 401) {
                  getAccessToken()
                    .then((res) =>
                      postWithdrawal(res.data.data.accessToken).then((res) => {
                        removeLocalIsLogin();
                        removeSessionRefreshToken();
                        alert("정상적으로 탈퇴 되었습니다.");
                        navigate("/signin");
                      })
                    )
                    .catch((e) => {
                      alert(
                        "회원 탈퇴 중 문제가 발생했습니다. 다시 시도해주세요."
                      );
                    });
                }
              });
          }
        }}
      >
        회원탈퇴
      </NavModalSettingRow>
    </StyledNavModalSetting>
  );
}

export default NavModalSetting;

const StyledNavModalSetting = styled.ul``;
