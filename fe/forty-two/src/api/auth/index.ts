import { instance } from "..";
import { getRefreshToken } from "../../utils/refreshToken";

/**
 * [POST] "auth/check/google" Google 회원가입 여부 체크
 */
export async function postCheckGoogle(body: TAuth["check"]) {
  return instance().post("auth/check/google", body);
}

/**
 * [POST] "auth/signup/google" Google 회원가입
 */
export async function postSignupGoogle(body: TAuth["signup"]["google"]) {
  return instance().post("auth/signup/google", body);
}

/**
 * [POST] "auth/check/apple/web" Apple 회원가입 여부 체크
 */
export async function postCheckApple(body: TAuth["check"]) {
  return instance().post("auth/check/apple/web", body);
}

/**
 * [GET] "auth/nickname" 랜덤 닉네임 생성
 */
export async function getNickname() {
  return instance().get("auth/nickname");
}

/**
 * [POST] "auth/token" access token 갱신
 */
export async function getAccessToken() {
  return instance({
    "REFRESH-TOKEN": getRefreshToken(),
  }).post("auth/token");
}
