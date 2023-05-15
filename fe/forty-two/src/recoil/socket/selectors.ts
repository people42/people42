import { sendPong } from "../../utils";
import {
  socketGuestCntState,
  socketNearUserState,
  socketNewMessageState,
  socketState,
} from "./atoms";
import { DefaultValue, selector } from "recoil";

export const socketGuestAddState = selector<number>({
  key: "socketGuestChangeState",
  get: ({ get }) => {
    const socketGuest = get(socketGuestCntState);
    return socketGuest;
  },
  set: ({ get, set }) => {
    const socketGuest = get(socketGuestCntState);
    set(socketGuestCntState, socketGuest + 1);
  },
});

export const socketGuestRemoveState = selector<number>({
  key: "socketGuestRemoveState",
  get: ({ get }) => {
    const socketGuest = get(socketGuestCntState);
    return socketGuest;
  },
  set: ({ get, set }) => {
    const socketGuest = get(socketGuestCntState);
    set(socketGuestCntState, socketGuest - 1);
  },
});

export const socketPongSendState = selector<WebSocket | null>({
  key: "socketPongSendState",
  get: ({ get }) => {
    const socket = get(socketState);
    return socket;
  },
  set: ({ get, set }) => {
    const socket = get(socketState);
    if (socket) {
      sendPong(socket);
    }
    set(socketState, socket);
  },
});

export const socketUserChangeState = selector<Map<number, TSocketNearUser>>({
  key: "socketUserChangeState",
  get: ({ get }) => {
    const socketNearUser = get(socketNearUserState);
    return socketNearUser;
  },
  set: ({ get, set }, newUser) => {
    const socketNearUser = get(socketNearUserState);

    // 새 Map 객체 생성
    const newMap = new Map<number, TSocketNearUser>();

    // 기존 socketNearUser 객체를 깊은 복사하여 새 Map 객체에 추가
    socketNearUser.forEach((value, key) => {
      const copiedValue = JSON.parse(JSON.stringify(value));
      newMap.set(key, copiedValue);
    });

    // newUser가 Map 객체인 경우 새 Map 객체에 추가
    if (newUser instanceof Map) {
      newUser.forEach((value) => newMap.set(value.userIdx, value));
    }

    // 새 Map 객체를 socketNearUserState 변수에 할당
    set(socketNearUserState, newMap);
  },
});

export const socketUserRemoveState = selector<Map<number, TSocketNearUser>>({
  key: "socketUserRemoveState",
  get: ({ get }) => {
    const socketNearUser = get(socketNearUserState);
    return socketNearUser;
  },
  set: ({ get, set }, newUser) => {
    const socketNearUser = get(socketNearUserState);

    // 깊은 복사를 통해 새 Map 객체 생성
    const newMap = new Map<number, TSocketNearUser>();
    socketNearUser.forEach((value, key) => {
      const copiedValue = JSON.parse(JSON.stringify(value));
      newMap.set(key, copiedValue);
    });

    // newUser가 Map 객체인 경우 새 Map 객체에 엔트리 추가
    if (newUser instanceof Map) {
      newUser.forEach((value) => {
        const copiedValue = JSON.parse(JSON.stringify(value));
        newMap.delete(copiedValue.userIdx);
      });
    }

    // 변경된 새 Map 객체를 socketNearUserState 변수에 할당
    set(socketNearUserState, newMap);
  },
});

export const socketNewMessageChangeState = selector<TNewMessage | null>({
  key: "socketNewMessageChangeState",
  get: ({ get }) => {
    const newMessage = get(socketNewMessageState);
    return newMessage;
  },
  set: ({ set }, newMessage) => {
    set(socketNewMessageState, null);
    set(socketNewMessageState, newMessage);
  },
});
