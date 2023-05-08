import { userLocationUpdateState } from "../../../../recoil/location/selectors";
import {
  socketGuestCntState,
  socketNearUserState,
} from "../../../../recoil/socket/atoms";
import { useEffect, useState } from "react";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

type homeMapSocketProps = {};

function HomeMapSocket({}: homeMapSocketProps) {
  const [nearUser, setNearUser] = useRecoilState(socketNearUserState);
  const [guestCnt, setGuestCnt] = useRecoilState(socketGuestCntState);

  const [nearUserList, setNearUserList] = useState<any[]>();

  useEffect(() => {
    let userList: any[] = [];
    if (nearUser) {
      nearUser.forEach((value, key) => {
        userList.push(<div key={`near-user-${key}`}>{value.nickname}</div>);
      });
    }
    setNearUserList(userList);
  }, [nearUser]);

  return (
    <StyledHomeMapSocket>
      <div
        onClick={() => {
          console.log(1);
        }}
      >
        {guestCnt}
        {nearUserList}
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
