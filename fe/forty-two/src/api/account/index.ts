import { instance } from "..";

/**
 * [PUT] "account/withdrawal" 회원탈퇴
 */
export async function postWithdrawal(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).put("account/withdrawal");
}
