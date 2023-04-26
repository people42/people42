import Meta from "../../Meta";
import { LogoBg } from "../../components/index";
import { signUpUserState } from "../../recoil/user/atoms";
import {
  SignUpCard,
  NicknamePicker,
  EmojiSelector,
  ConformUserSetting,
} from "./components/index";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useRecoilState } from "recoil";
import styled from "styled-components";

function SignUp() {
  const navigate = useNavigate();
  useEffect(() => {
    const isLogin: string | null = localStorage.getItem("isLogin") ?? null;
    if (isLogin) {
      navigate("/");
    }
  }, []);

  const [titleEmoji, setTitleEmoji] = useState<string>();
  const [titleNickname, setTitleNickname] = useState<string>();

  const [signUpUser, setSignUpUser] =
    useRecoilState<TSignUpUser>(signUpUserState);

  useEffect(() => {
    signUpUser.nickname ? setTitleNickname(signUpUser.nickname) : null;
    axios
      .get(
        `https://emoji-api.com/emojis/${signUpUser?.emoji}?access_key=ec07ff80043b910ad20772b199f7bf256815e17a`
      )
      .then(function (res) {
        setTitleEmoji(res.data[0].character);
      })
      .catch(function (e) {
        console.log(e);
      });
  }, [signUpUser]);

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
      <Meta
        title={`42 | ${titleEmoji ?? ""} ${
          titleNickname ?? "회원가입"
        } (${step}/3)`}
      ></Meta>
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
