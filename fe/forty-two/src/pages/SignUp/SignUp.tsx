import styled from "styled-components";
import { LogoBg, NicknamePicker } from "../../components/index";
import React from "react";
import SignUpCard from "./components/SignUpCard";

function SignUp() {
  return (
    <StyledSignUp>
      <SignUpCard
        step={1}
        title={"닉네임을 골라주세요"}
        content={<NicknamePicker />}
      ></SignUpCard>
      <LogoBg isBlue={false}></LogoBg>
    </StyledSignUp>
  );
}

export default React.memo(SignUp);

const StyledSignUp = styled.main``;
