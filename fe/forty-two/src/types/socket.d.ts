type TSocketUserData = {
  latitude: number;
  longitude: number;
  status: TSocketStatus;
};

type TSocketMethod = "INFO" | "NEAR" | "FAR";
type TSocketStatus = "watching" | "writing";

type TSocketNearUser = {
  type: string;
  userIdx: number;
  latitude: number;
  longitude: number;
  nickname: string;
  message: string;
  emoji: string;
  status: TSocketStatus;
};

type TSocketReceive = {
  method: TSocketMethod;
  data: {
    emoji: string;
    nearUsers: TSocketNearUser[];
    latitude: number;
    userIdx: number;
    nickname: string;
    type: string;
    message: string;
    longitude: number;
    status: TSocketStatus;
  };
};
