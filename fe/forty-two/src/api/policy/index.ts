import { instance } from "..";
import axios from "axios";

/**
 * [GET] "https://notion-api.splitbee.io/v1/page/${pageId}" Notion Page Load
 */
export async function getNotionPage(pageId: string) {
  return axios.get(`https://notion-api.splitbee.io/v1/page/${pageId}`);
}
