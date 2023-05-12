import { atom } from "recoil";

export const placeState = atom<TPlace | null>({
  key: "placeState",
  default: null,
});
