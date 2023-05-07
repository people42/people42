import { instance } from "..";

/**
 * [GET] "socket/total_message_cnt" 전체 메시지 개수 조회
 */
export async function getMessageCnt() {
  return instance().get("socket/total_message_cnt");
}
