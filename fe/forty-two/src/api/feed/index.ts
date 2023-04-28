import { instance } from "..";

/**
 * [GET] "feed/recent" 최근 피드 조회
 */
export async function getRecentFeed(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("feed/recent");
}
