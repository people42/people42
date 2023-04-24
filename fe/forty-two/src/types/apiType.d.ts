type feed = {
  recent: {
    recentMessageInfo: {
      messageIdx: number;
      content: string;
      userIdx: number;
      nickname: string;
      emoji: string;
      color: colorType;
      brushCnt: number;
    };
    placeWithTimeInfo: {
      placeIdx: number;
      placeName: string;
      time: string;
    };
  };
};
