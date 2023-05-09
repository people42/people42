import Meta from "../../Meta";
import { NavBar } from "../../components";
import { isLoginState } from "../../recoil/user/atoms";
import { userLoginState } from "../../recoil/user/selectors";
import { Banner, HomeMain } from "./components";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { isMobile } from "react-device-detect";
import { useNavigate } from "react-router";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

function Home() {
  const user = useRecoilValue<TUser | null>(userLoginState);
  const [userEmoji, setUserEmoji] = useState<string>("");

  const navigate = useNavigate();

  useEffect(() => {
    isMobile ? navigate("/mobile") : null;
  }, []);

  useEffect(() => {
    if (user) {
      axios
        .get(
          `https://emoji-api.com/emojis/${user?.emoji}?access_key=ec07ff80043b910ad20772b199f7bf256815e17a`
        )
        .then(function (res) {
          setUserEmoji(res.data[0].character);
        })
    }
  }, [user]);

  return (
    <StyledHome>
      <Meta
        title={
          user
            ? `${userEmoji} ${user?.nickname}의 42 | Home`
            : "42 | 어쩌면 마주친 사이"
        }
      ></Meta>
      <Banner></Banner>
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
