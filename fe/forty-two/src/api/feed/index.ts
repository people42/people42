import { instance } from "..";

/**
 * [GET] "feed/recent" 최근 피드 조회
 */
export async function getRecentFeed(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("feed/recent");
}
/**
 * [GET] "feed/new" 최근 피드 조회
 */
export async function getNewFeed(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("feed/new");
}

/**
 * [POST] "feed/emotion" 피드 리액션 추가/삭제
 */
export async function postFeedEmotion(
  accessToken: string,
  body: TFeed["emotion"]
) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).post("feed/emotion", body);
}

/**
 * [GET] "feed/place" 장소별 피드 조회
 */
export async function getPlace(accessToken: string, params: TFeed["place"]) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("feed/place", {
    params: params,
  });
}

/**
 * [GET] "feed/user" 사용자별 피드 조회
 */
export async function getUser(accessToken: string, params: TFeed["user"]) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("feed/user", {
    params: params,
  });
}

/**
 * [GET] "feed/user/place" 장소별 사용자 피드 조회
 */
export async function getUserPlace(
  accessToken: string,
  params: TFeed["userPlace"]
) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("feed/user/place", {
    params: params,
  });
}
