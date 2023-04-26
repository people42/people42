import { userState } from "./atoms";
import { selector } from "recoil";

export const userLoginState = selector<TUser | null>({
  key: "userLoginState",
  get: ({ get }) => {
    const user = get(userState);
    return user;
  },
  set: ({ set }, userData) => {
    set(userState, userData);
  },
});

export const userLogoutState = selector<TUser | null>({
  key: "userLogoutState",
  get: ({ get }) => {
    const user = get(userState);
    return user;
  },
  set: ({ reset }) => {
    reset(userState);
  },
});

export const userRefreshTokenState = selector<string>({
  key: "userRefreshTokenState",
  get: ({ get }) => {
    const user = get(userState);
    return user?.refreshToken ?? "";
  },
});

export const userAccessTokenState = selector<string>({
  key: "userAccessTokenState",
  get: ({ get }) => {
    const user = get(userState);
    return user?.accessToken ?? "";
  },
});

export const userNicknameState = selector<string>({
  key: "userNicknameState",
  get: ({ get }) => {
    const user = get(userState);
    return user?.nickname ?? "";
  },
});

export const signUpUserEmojiState = selector<string>({
  key: "signUpUserEmojiState",
  get: ({ get }) => {
    const user = get(userState);
    return user?.email ?? "";
  },
});
