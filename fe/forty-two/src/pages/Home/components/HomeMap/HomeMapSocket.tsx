import { emojiNameList } from "../../../../assets/emojiList";
import {
  socketGuestCntState,
  socketNearUserState,
  socketNewMessageState,
} from "../../../../recoil/socket/atoms";
import { socketNewMessageChangeState } from "../../../../recoil/socket/selectors";
import HomeMapSocketGuest from "./HomeMapSocketGuest";
import HomeMapSocketWriting from "./HomeMapSocketWriting";
import _ from "lodash";
import { useEffect, useState } from "react";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type homeMapSocketProps = {};

function HomeMapSocket({}: homeMapSocketProps) {
  const [nearUser, setNearUser] = useRecoilState(socketNearUserState);
  const [guestCnt, setGuestCnt] = useRecoilState(socketGuestCntState);

  const [nearUserList, setNearUserList] = useState<any[]>();
  const newMessage = useRecoilValue(socketNewMessageState);
  const [key, setKey] = useState<number>(0);
  const S3_URL = import.meta.env.VITE_S3_URL;
  const setNewMessage = useSetRecoilState(socketNewMessageChangeState);

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

  const [guestList, setGuestList] = useState<JSX.Element[]>();

  useEffect(() => {
    if (guestCnt && !guestList) {
      const initialGuestList = [];
      for (let i = 0; i < guestCnt; i++) {
        initialGuestList.push(
          <HomeMapSocketGuest
            emoji={_.sample(emojiNameList) ?? "ghost"}
            right={Math.floor(Math.random() * 101)}
            bottom={Math.floor(Math.random() * 101)}
            key={`home-map-guest-${Math.floor(Math.random() * 90000) + 10000}`}
          />
        );
      }
      setGuestList(initialGuestList);
    } else if (guestCnt && guestList) {
      if (guestList.length > guestCnt) {
        handleDecrementGuest();
      } else if (guestList.length < guestCnt) {
        handleIncrementGuest();
      }
    } else if (guestCnt == 0) {
      setGuestList(undefined);
    }
  }, [guestCnt]);

  // guestCnt가 1 증가하면 guestList에 HomeMapSocketGuest를 하나 추가합니다.
  const handleIncrementGuest = () => {
    if (guestList) {
      const newGuestList = [
        ...guestList,
        <HomeMapSocketGuest
          emoji={_.sample(emojiNameList) ?? "ghost"}
          right={Math.floor(Math.random() * 101)}
          bottom={Math.floor(Math.random() * 101)}
          key={`home-map-guest-${Math.floor(Math.random() * 90000) + 10000}`}
        />,
      ];
      setGuestList(newGuestList);
    }
  };

  // guestCnt가 1 감소하면 guestList에서 맨 앞에 있는 HomeMapSocketGuest를 하나 제거합니다.
  const handleDecrementGuest = () => {
    if (guestList) {
      const newGuestList = [...guestList];
      newGuestList.shift();
      setGuestList(newGuestList);
    }
  };

  useEffect(() => {
    setKey(key + 1);
  }, [newMessage]);

  useEffect(() => {
    setNewMessage(null);
  }, []);

  return (
    <StyledHomeMapSocket>
      <div className="home-map-near">
        {nearUserList}
        {guestList}
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
