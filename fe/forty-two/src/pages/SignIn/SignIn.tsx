import Meta from "../../Meta";
import { LogoBg } from "../../components/index";
import { getLocalIsLogin } from "../../utils";
import SignInCard from "./components/SignInCard";
import React, { useEffect } from "react";
import { isMobile } from "react-device-detect";
import { useNavigate } from "react-router";
import styled from "styled-components";

function SignIn() {
  const navigate = useNavigate();
  useEffect(() => {
    const isLogin: boolean = getLocalIsLogin();
    if (isLogin || isMobile) {
      navigate("/");
    }
  }, []);

  return (
    <StyledSignIn>
      <Meta title={`42 | 로그인`}></Meta>
      <SignInCard></SignInCard>
      <LogoBg isBlue={true} />
    </StyledSignIn>
  );
}

export default React.memo(SignIn);

const StyledSignIn = styled.main``;
