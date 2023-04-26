import { NavBar } from "../../components";
import { userLoginState } from "../../recoil/user/selectors";
import { HomeMain } from "./components";
import React, { useEffect } from "react";
import { Link } from "react-router-dom";
import { useRecoilState } from "recoil";
import styled from "styled-components";

function Home() {
  const isLogin = localStorage.getItem("isLogin");
  const [user, setUser] = useRecoilState<TUser | null>(userLoginState);

  return (
    <StyledHome>
      <NavBar></NavBar>
      {isLogin ? (
        <HomeMain></HomeMain>
      ) : (
        <div>
          로그인해주삼 <Link to={"/signin"}>하러가기</Link>
        </div>
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
