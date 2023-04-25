import { NavBar } from "../../components";
import { userLoginState } from "../../recoil/user/selectors";
import { HomeMain } from "./components";
import React from "react";
import { Link } from "react-router-dom";
import { useRecoilState } from "recoil";
import styled from "styled-components";

function Home() {
  const [user, setUser] = useRecoilState<TUser | null>(userLoginState);

  return (
    <StyledHome>
      <NavBar></NavBar>
      {user ? (
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
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
`;
