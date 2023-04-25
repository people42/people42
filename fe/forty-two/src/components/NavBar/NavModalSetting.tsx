import { postWithdrawal } from "../../api";
import { userLoginState } from "../../recoil/auth/selectors";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type navModalSettingProps = {};

function NavModalSetting({}: navModalSettingProps) {
  const [user, setUser] = useRecoilState<TUser | null>(userLoginState);
  return (
    <StyledNavModalSetting>
      <li
        onClick={() => {
          postWithdrawal().then(() => setUser(null));
        }}
      >
        회원탈퇴
      </li>
    </StyledNavModalSetting>
  );
}

export default NavModalSetting;

const StyledNavModalSetting = styled.ul``;
