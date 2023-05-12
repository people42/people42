import { instance } from "..";

/**
 * [POST] "background" 위치 갱신 & 스침 생성
 */
export async function postLocation(accessToken: string, body: TLocation) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).post("background", body);
}
