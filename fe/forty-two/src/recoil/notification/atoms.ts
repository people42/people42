import { atom } from "recoil";

export const isFirebaseLoadState = atom<boolean>({
  key: "isFirebaseLoadState",
  default: false,
});

export const isNotificationPermittedState = atom<"check" | boolean>({
  key: "isNotificationPermittedState",
  default: "check",
});
