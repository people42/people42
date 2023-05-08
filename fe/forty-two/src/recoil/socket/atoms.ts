import { atom } from "recoil";

export const socketState = atom<WebSocket | null>({
  key: "socketState",
  default: null,
});

export const socketNearUserState = atom<TSocketUserData[] | []>({
  key: "socketNearUserState",
  default: [],
});
