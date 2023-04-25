import { instance } from "..";

/**
 * [POST] "auth/check/google" Google 회원가입 여부 체크
 */
export async function postCheckGoogle(body: TAuth["check"]["google"]) {
  return instance.post("auth/check/google", body);
}

/**
 * [POST] "auth/signup/google" Google 회원가입
 */
export async function postSignupGoogle(body: TAuth["signup"]["google"]) {
  return instance.post("auth/signup/google", body);
}

/**
 * [GET] "auth/nickname" Google 회원가입
 */
export async function getNickname() {
  return instance.get("auth/nickname");
}
