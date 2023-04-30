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
