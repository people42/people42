import { atom } from "recoil";

export const homeInfoState = atom<boolean>({
  key: "homeInfoState",
  default: true,
});
