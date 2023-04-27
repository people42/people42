import { postWithdrawal } from "../../api";
import { userLogoutState } from "../../recoil/user/selectors";
import { removeRefreshToken } from "../../utils/refreshToken";
import NavModalSettingRow from "./NavModalSettingRow";
import { useNavigate } from "react-router";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type navModalSettingProps = {};

function NavModalSetting({}: navModalSettingProps) {
  const navigate = useNavigate();
  const [user, userLogout] = useRecoilState(userLogoutState);

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
          localStorage.removeItem("isLogin");
          removeRefreshToken();
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
                localStorage.removeItem("isLogin");
                removeRefreshToken();
                alert("정상적으로 탈퇴 되었습니다.");
                navigate("/signin");
              })
              .catch((e) => {
                alert("회원 탈퇴 중 문제가 발생했습니다. 다시 시도해주세요.");
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
