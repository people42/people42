import { instance } from "..";

/**
 * [POST] "feedback" 피드백
 */
export async function postFeedback(content: string) {
  return instance().post("feedback", {
    content: content,
  });
}
