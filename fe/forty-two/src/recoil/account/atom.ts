import { atom } from "recoil";

export const userState = atom<TUser | null>({
  key: "userState",
  default: null,
});
