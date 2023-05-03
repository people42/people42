type TSignUpUser = {
  platform: "apple" | "google" | null;
  email: string | null;
  nickname: string | null;
  o_auth_token: string | null;
  emoji: string | null;
};

type TUser = {
  user_idx: number;
  email: string;
  nickname: string;
  emoji: string;
  color: string;
  accessToken: string;
  refreshToken: string;
};

type TMyMessage = {
  emoji: string;
  message: string | null;
  messageCnt: number;
  fire: number;
  heart: number;
  tear: number;
  thumbsUp: number;
};

type TUserDetail = {
  brushCnt: number;
  emoji: string;
  userIdx: number;
  nickname: string;
  placeResDtos: {
    placeIdx: number;
    placeName: string;
    placeLatitude: number;
    placeLongitude: number;
    brushCnt: number;
  }[];
  placeMessageInfo: {
    content: string;
    emotion?: string;
    messageIdx: number;
    time: string;
  };
};
