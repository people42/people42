import { atom } from "recoil";

export const themeState = atom<boolean>({
  key: "themeState",
  default: false,
});
