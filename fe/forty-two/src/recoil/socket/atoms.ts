import { atom } from "recoil";

export const socketState = atom<WebSocket | null>({
  key: "socketState",
  default: null,
});

export const socketNearUserState = atom<Map<number, TSocketNearUser>>({
  key: "socketNearUserState",
  default: undefined,
});

export const socketGuestCntState = atom<number>({
  key: "socketGuestCntState",
  default: 0,
});
