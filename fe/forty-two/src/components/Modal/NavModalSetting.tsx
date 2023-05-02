import NavModalSettingRow from "./NavModalSettingRow";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router";
import styled from "styled-components";

type navModalSettingProps = {};

function NavModalSetting({}: navModalSettingProps) {
  const navigate = useNavigate();
  const [userPlatform, setUserPlatform] = useState<string | null>();
  useEffect(() => {
    setUserPlatform(localStorage.getItem("user_platform"));
  }, []);
  return (
    <StyledNavModalSetting>
      <NavModalSettingRow onClick={() => navigate("/policy")}>
        이용약관 및 개인정보처리방침
      </NavModalSettingRow>
      <NavModalSettingRow onClick={() => navigate("/logout")}>
        로그아웃
      </NavModalSettingRow>
      <NavModalSettingRow
        onClick={() => navigate(`/withdrawal/${userPlatform}`)}
      >
        회원탈퇴
      </NavModalSettingRow>
    </StyledNavModalSetting>
  );
}

export default NavModalSetting;

const StyledNavModalSetting = styled.ul``;
