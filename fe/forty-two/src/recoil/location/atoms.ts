import { atom } from "recoil";

export const locationState = atom<TLocation | null>({
  key: "locationState",
  default: null,
});

export const locationInfoState = atom<TLocationInfo | null>({
  key: "locationInfoState",
  default: null,
});

export const isLocationPermittedState = atom<"check" | boolean>({
  key: "isLocationPermittedState",
  default: "check",
});
