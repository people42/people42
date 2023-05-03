import { atom } from "recoil";

export const isFirebaseLoadState = atom<boolean>({
  key: "isFirebaseLoadState",
  default: false,
});
