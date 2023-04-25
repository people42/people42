import { instance } from "..";

/**
 * [PUT] "account/withdrawal" 회원탈퇴
 */
export async function postWithdrawal() {
  return instance.put("account/withdrawal");
}
