import Meta from "../../Meta";
import { NavBar } from "../../components";
import { userLoginState } from "../../recoil/user/selectors";
import { getLocalIsLogin } from "../../utils";
import { HomeMain } from "./components";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useRecoilState } from "recoil";
import styled from "styled-components";

function Home() {
  const [user, setUser] = useRecoilState<TUser | null>(userLoginState);
  const [userEmoji, setUserEmoji] = useState<string>("");

  useEffect(() => {
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
  }, [user]);
  const isLogin = getLocalIsLogin();

  return (
    <StyledHome>
      <Meta title={`${userEmoji} ${user?.nickname}의 42 | Home`}></Meta>
      <NavBar></NavBar>
      {!isLogin ? (
        <div>
          로그인해주삼 <Link to={"/signin"}>하러가기</Link>
        </div>
      ) : (
        <HomeMain></HomeMain>
      )}
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
