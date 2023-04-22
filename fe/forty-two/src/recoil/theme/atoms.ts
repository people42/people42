import { atom } from "recoil";

let isUserDark: string | null = localStorage.getItem("isDark");

if (isUserDark == null) {
  isUserDark = (
    window.matchMedia &&
    window.matchMedia("(prefers-color-scheme: dark)").matches
  ).toString();
}

export const themeState = atom<boolean>({
  key: "themeState",
  default: isUserDark == "true" ? true : false,
});
