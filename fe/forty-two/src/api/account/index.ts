import { instance } from "..";

/**
 * [PUT] "account/withdrawal" 회원탈퇴
 */
export async function postWithdrawal(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).put("account/withdrawal");
}

/**
 * [GET] "account/myinfo" 내 정보 조회
 */
export async function getMyInfo(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("account/myinfo");
}
