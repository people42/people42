import { MyMessageCard, MyMessageListCard, NavBar } from "../../components";
import { HomeMain } from "./components";
import React from "react";
import styled from "styled-components";

function Home() {
  return (
    <StyledHome>
      <NavBar></NavBar>
      <HomeMain></HomeMain>
    </StyledHome>
  );
}

export default React.memo(Home);

const StyledHome = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
`;
