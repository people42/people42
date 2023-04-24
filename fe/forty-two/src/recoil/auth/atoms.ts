import { atom } from "recoil";

export const signUpUserState = atom<TSignUpUser>({
  key: "signUpUserState",
  default: {
    platform: null,
    email: null,
    nickname: null,
    o_auth_token: null,
    color: null,
    emoji: null,
  },
});

export const userState = atom({
  key: "userState",
  default: {},
});
