import { instance } from "..";

/**
 * [GET] "feed/recent" 최근 피드 조회
 */
export async function postLocation(accessToken: string, body: TLocation) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("background");
}
