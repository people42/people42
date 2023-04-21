import styled from "styled-components";
import { LogoBg } from "../../components/index";

import SignInCard from "./components/SignInCard";
import React from "react";

function SignIn() {
  return (
    <StyledSignIn>
      <SignInCard></SignInCard>
      <LogoBg isBlue={true} />
    </StyledSignIn>
  );
}

export default React.memo(SignIn);

const StyledSignIn = styled.main``;
