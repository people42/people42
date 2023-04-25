import { userRefreshTokenState } from "./selector";
import { useRecoilValue } from "recoil";

export default function useAccessToken() {
  return useRecoilValue(userRefreshTokenState);
}
