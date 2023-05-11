import { getAccessToken, instance } from "..";

/**
 * [DELETE] "account/withdrawal" Google 회원탈퇴
 */
export async function postWithdrawalGoogle(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).delete("account/withdrawal");
}

/**
 * [DELETE] "account/withdrawal/apple/web" Apple 회원탈퇴
 */
export async function postWithdrawalApple(
  accessToken: string,
  appleCode: string
) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).delete("account/withdrawal/apple/web", { data: { appleCode: appleCode } });
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

/**
 * [DELETE] "account/logout" 로그아웃
 */
export async function deleteLogout(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).delete("account/logout");
}

/**
 * [POST] "account/fcm_token" FCM 토큰 등록
 */
export async function postFCMToken(accessToken: string, token: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).post("account/fcm_token", { token: token });
}

/**
 * [PUT] "account/nickname" 닉네임 변경
 */
export async function putNickname(accessToken: string, nickname: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).put("account/nickname", { nickname: nickname });
}

/**
 * [PUT] "account/emoji" 이모지 변경
 */
export async function putEmoji(accessToken: string, emoji: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).put("account/emoji", { emoji: emoji });
}
