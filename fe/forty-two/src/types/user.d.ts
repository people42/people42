type TSignUpUser = {
  platform: "apple" | "google" | null;
  email: string | null;
  nickname: string | null;
  o_auth_token: string | null;
  emoji: string | null;
};

type TUser = {
  user_idx: number | null;
  email: string | null;
  nickname: string | null;
  accessToken: string | null;
};
