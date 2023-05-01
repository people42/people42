type TReaction = "heart" | "fire" | "tear" | "thumbsUp" | "delete";

type THistory = {
  content: string;
  createdAt: string;
  messageIdx: number;
  heart: number;
  fire: number;
  tear: number;
  thumbsUp: number;
};
