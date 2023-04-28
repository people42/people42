import { postLocation } from "../../api";
import { locationState } from "./atoms";
import { selector } from "recoil";

export const userLocationUpdateState = selector<TLocation | null>({
  key: "userLocationUpdateState",
  get: ({ get }) => {
    const location = get(locationState);
    return location;
  },
  set: ({ set }, newLocation) => {
    console.log("new", newLocation);
    set(locationState, newLocation);
  },
});
