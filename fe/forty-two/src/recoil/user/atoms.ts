import { atom } from "recoil";

export const isLoginState = atom<boolean | null>({
  key: "isLoginState",
  default: null,
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

export const userLoadedState = atom<boolean>({
  key: "userLoadedState",
  default: false,
});
