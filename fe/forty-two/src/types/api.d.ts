type TFeed = {
  recent: {
    recentMessageInfo: {
      messageIdx: number;
      content: string;
      userIdx: number;
      nickname: string;
      emoji: string;
      emotion: TReaction;
      color: TColorType;
      brushCnt: number;
    };
    placeWithTimeInfo: {
      placeIdx: number;
      placeName: string;
      time: string;
    };
  } | null;
  new: {
    recentUsersInfo: {
      nickname: string; // 대표 유저 닉네임
      userCnt: number; // 대표 유저 포함 해당 장소 및 시간에서 스친 유저 수
      firstTimeUserEmojis: string[]; // 처음 만난 유저 이모지 리스트
      repeatUserEmojis: string[]; // 두 번 이상 만난 유저 이모지 리스트
    };
    placeWithTimeInfo: {
      placeIdx: number;
      placeName: string;
      time: string;
    };
  };
  emotion: {
    emotion: string;
    messageIdx: number;
  };
  place: { placeIdx: number; time: string; page: number; size: number };
  user: { userIdx: number };
  userPlace: { placeIdx: number; userIdx: number };
};

type TAuth = {
  check: {
    google: { o_auth_token: string };
    apple: { appleCode: string };
  };
  signup: {
    google: {
      email: string;
      nickname: string;
      o_auth_token: string;
      emoji: string;
    };
  };
};

type TAccount = {
  message: {
    message: string;
  };
  report: {
    messageIdx: number;
    content: string;
  };
};

type TLocation = {
  latitude: number;
  longitude: number;
};

type TNotificationHistory = {
  title: string;
  body: string;
  emoji: string;
  createdAt: string;
};
