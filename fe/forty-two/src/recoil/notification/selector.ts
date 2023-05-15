import { notificationState } from "./atoms";
import { selector } from "recoil";

export const updateNotificationState = selector<TNotification | null>({
  key: "updateNotificationState",
  get: ({ get }) => {
    const Notification = get(notificationState);
    return Notification;
  },
  set: ({ get, set }, newNotification) => {
    const Notification = get(notificationState);
    if (Notification) {
      set(notificationState, null);
    }
    set(notificationState, newNotification);
  },
});
