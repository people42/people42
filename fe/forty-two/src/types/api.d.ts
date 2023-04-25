type TFeed = {
  recent: {
    recentMessageInfo: {
      messageIdx: number;
      content: string;
      userIdx: number;
      nickname: string;
      emoji: string;
      color: TColorType;
      brushCnt: number;
    };
    placeWithTimeInfo: {
      placeIdx: number;
      placeName: string;
      time: string;
    };
  };
};

type TAuth = {
  check: {
    google: {
      o_auth_token: string;
    };
    apple: {};
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
