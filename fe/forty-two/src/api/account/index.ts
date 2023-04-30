import { getAccessToken, instance } from "..";

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

/**
 * [GET] "account/history?date=${date}" 내 상태메시지 히스토리 조회
 */
export async function getMyHistory(accessToken: string, date: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get(`account/history`, { params: { date: date } });
}

/**
 * [POST] "account/message" 내 메시지 등록
 */
export async function postMessage(
  accessToken: string,
  body: TAccount["message"]
) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).post("account/message", body);
}

/**
 * [PUT] "account/message" 내 메시지 삭제
 */
export async function deleteMessage(accessToken: string, messageIdx: number) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).put("account/message", { messageIdx: messageIdx });
}
