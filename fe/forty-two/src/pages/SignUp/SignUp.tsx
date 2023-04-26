import { LogoBg } from "../../components/index";
import {
  SignUpCard,
  NicknamePicker,
  EmojiSelector,
  ConformUserSetting,
} from "./components/index";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import styled from "styled-components";

function SignUp() {
  const navigate = useNavigate();
  useEffect(() => {
    const isLogin: string | null = localStorage.getItem("isLogin") ?? null;
    if (isLogin) {
      navigate("/");
    }
  }, []);

  const [step, setStep] = useState<1 | 2 | 3>(1);

  const signUpContent = {
    1: {
      title: "마음에 드는 닉네임을 고르세요",
      content: <NicknamePicker onClick={(e) => setStep(2)} />,
    },
    2: {
      title: "프로필 이모지를 고르세요",
      content: <EmojiSelector onClick={(e) => setStep(3)} />,
    },
    3: {
      title: "환영합니다!",
      content: <ConformUserSetting onClick={(e) => setStep(1)} />,
    },
  };

  return (
    <StyledSignUp>
      <SignUpCard
        step={step}
        title={signUpContent[step].title}
        content={signUpContent[step].content}
      ></SignUpCard>
      <LogoBg isBlue={false}></LogoBg>
    </StyledSignUp>
  );
}

export default React.memo(SignUp);

const StyledSignUp = styled.main``;
