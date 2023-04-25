import { userState } from "./atom";
import { selector } from "recoil";

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
