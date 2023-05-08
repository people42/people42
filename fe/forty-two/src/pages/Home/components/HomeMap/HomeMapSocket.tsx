import { userLocationUpdateState } from "../../../../recoil/location/selectors";
import { useEffect, useState } from "react";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type homeMapSocketProps = {};

function HomeMapSocket({}: homeMapSocketProps) {
  const location = useRecoilValue<TLocation | null>(userLocationUpdateState);
  const userData = {
    latitude: 36.354946759143,
    longitude: 127.29980994578,
    nickname: "임희상",
    message: "윤성운 바보",
    status: "watching",
  };

  
  return (
    <StyledHomeMapSocket>
      <div
        onClick={() => {
          console.log(1);
        }}
      >
        asdfasdfasdfasfda
      </div>
    </StyledHomeMapSocket>
  );
}

export default HomeMapSocket;

const StyledHomeMapSocket = styled.div`
  z-index: 10;
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
`;
