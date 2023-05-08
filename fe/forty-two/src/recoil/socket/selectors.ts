import { socketGuestCntState, socketNearUserState } from "./atoms";
import { selector } from "recoil";

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
