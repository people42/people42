import { atom } from "recoil";

export const isLoginState = atom<boolean>({
  key: "isLoginState",
  default: false,
});

export const signUpUserState = atom<TSignUpUser>({
  key: "signUpUserState",
  default: {
    platform: null,
    email: null,
    nickname: null,
    o_auth_token: null,
    emoji: null,
  },
});

export const userState = atom<TUser | null>({
  key: "userState",
  default: null,
});
