type TPlace = {
  messagesInfo: {
    messageIdx: number;
    content: string;
    userIdx: number;
    nickname: string;
    emoji: string;
    color: TColorType;
    brushCnt: number;
    emotion: TReaction;
  }[];
  placeWithTimeAndGpsInfo: {
    placeIdx: number;
    placeName: string;
    time: string;
    placeLatitude: number;
    placeLongitude: number;
  };
};
