import { atom } from "recoil";

export const locationState = atom<TLocation | null>({
  key: "locationState",
  default: null,
});
