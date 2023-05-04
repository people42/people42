import Meta from "../../Meta";
import { NavBar } from "../../components";
import { isLoginState } from "../../recoil/user/atoms";
import { userLoginState } from "../../recoil/user/selectors";
import { HomeMain } from "./components";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

function Home() {
  const [user, setUser] = useRecoilState<TUser | null>(userLoginState);
  const [userEmoji, setUserEmoji] = useState<string>("");
  const [isLogin, setIsLogin] = useRecoilState(isLoginState);

  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      axios
        .get(
          `https://emoji-api.com/emojis/${user?.emoji}?access_key=ec07ff80043b910ad20772b199f7bf256815e17a`
        )
        .then(function (res) {
          setUserEmoji(res.data[0].character);
        })
        .catch(function (e) {
          console.log(e);
        });
    }
  }, [user]);

  useEffect(() => {
    switch (isLogin) {
      case false:
        setIsLogin("check");
        navigate("/signin");
        break;
    }
  }, [isLogin]);

  return (
    <StyledHome>
      <Meta
        title={
          user ? `${userEmoji} ${user?.nickname}의 42 | Home` : "42 | Home"
        }
      ></Meta>
      <NavBar></NavBar>
      <HomeMain></HomeMain>
    </StyledHome>
  );
}

export default React.memo(Home);

const StyledHome = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
`;
