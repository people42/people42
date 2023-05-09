import {
  socketGuestCntState,
  socketNearUserState,
  socketNewMessageState,
} from "../../../../recoil/socket/atoms";
import HomeMapSocketWriting from "./HomeMapSocketWriting";
import { useEffect, useState } from "react";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

type homeMapSocketProps = {};

function HomeMapSocket({}: homeMapSocketProps) {
  const [nearUser, setNearUser] = useRecoilState(socketNearUserState);
  const [guestCnt, setGuestCnt] = useRecoilState(socketGuestCntState);

  const [nearUserList, setNearUserList] = useState<any[]>();
  const newMessage = useRecoilValue(socketNewMessageState);
  const [key, setKey] = useState<number>(0);
  const S3_URL = import.meta.env.VITE_S3_URL;

  useEffect(() => {
    let userList: any[] = [];
    if (nearUser) {
      nearUser.forEach((value, key) => {
        userList.push(
          <div
            className="home-map-near-user"
            key={`near-user-${key}`}
            style={{
              backgroundImage: `url("${S3_URL}emoji/animate/${value.emoji}.gif")`,
              right: `${(value.userIdx * 12341) % 100}%`,
              bottom: `${(value.userIdx * 21432) % 100}%`,
            }}
          >
            {value.status === "watching" ? null : <HomeMapSocketWriting />}
          </div>
        );
      });
    }
    setNearUserList(userList);
  }, [nearUser]);

  useEffect(() => {
    setKey(key + 1);
  }, [newMessage]);

  return (
    <StyledHomeMapSocket>
      <div className="home-map-near">
        {nearUserList}
        {newMessage ? (
          <div
            key={key}
            className="home-map-near-message"
            style={{
              right: `${(newMessage.userIdx * 12341) % 100}%`,
              bottom: `${(newMessage.userIdx * 21432) % 100}%`,
            }}
          >
            <div>
              {newMessage.message}
              <div>{newMessage.nickname}</div>
            </div>
          </div>
        ) : null}
      </div>
    </StyledHomeMapSocket>
  );
}

export default HomeMapSocket;

const StyledHomeMapSocket = styled.div`
  z-index: 15;
  position: absolute;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  .home-map-near {
    width: 50%;
    height: 50%;
    position: relative;
    &-user {
      animation: popIn 0.3s both;
      position: absolute;
      width: 40px;
      height: 40px;
      background-size: cover;
    }
    &-message {
      position: absolute;
      width: 200px;
      margin-right: 40px;
      margin-bottom: 40px;
      overflow: visible;
      & > div {
        animation: newMessageBubble 10s both;
        padding: 8px;
        ${({ theme }) => theme.text.subtitle2};
        background-color: ${({ theme }) => theme.color.background.secondary};
        border-radius: 16px 16px 0px 16px;
        color: ${({ theme }) => theme.color.text.primary};
        text-align: end;
        position: absolute;
        right: 0px;
        bottom: 0px;
        word-break: break-all;
        & > div {
          ${({ theme }) => theme.text.overline};
          color: ${({ theme }) => theme.color.text.secondary};
        }
      }
    }
  }
`;
