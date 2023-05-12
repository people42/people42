import { instance } from "..";

/**
 * [GET] "notification" 알림 개수 조회
 */
export async function getNotification(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("notification");
}

/**
 * [GET] "notification/history" 알림 기록 조회
 */
export async function getNotificationHistory(accessToken: string) {
  return instance({
    "ACCESS-TOKEN": accessToken,
  }).get("notification/history");
}
