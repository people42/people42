import { locationState } from "./atoms";
import { selector } from "recoil";

export const userLocationUpdateState = selector<TLocation | null>({
  key: "userLocationUpdateState",
  get: ({ get }) => {
    const location = get(locationState);
    return location;
  },
  set: async ({ set }, newLocation) => {
    set(locationState, newLocation);
  },
});
