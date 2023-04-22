import { NavBar } from "../../components";
import React from "react";
import styled from "styled-components";

function Home() {
  return (
    <StyledHome>
      <NavBar></NavBar>
    </StyledHome>
  );
}

export default React.memo(Home);

const StyledHome = styled.main``;
