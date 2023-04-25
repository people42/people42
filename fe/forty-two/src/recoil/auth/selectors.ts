import { userState } from "../account/atom";
import { DefaultValue, selector } from "recoil";

export const userLoginState = selector<TUser | null>({
  key: "userLoginState",
  get: ({ get }) => {
    const user = get(userState);
    return user;
  },
  set: ({ set }, userData) => {
    let refreshToken;
    if (userData instanceof DefaultValue) {
      sessionStorage.clear();
    } else {
      refreshToken = userData?.refreshToken;
    }
    refreshToken ? sessionStorage.setItem("refreshToken", refreshToken) : null;
    set(userState, userData);
  },
});
