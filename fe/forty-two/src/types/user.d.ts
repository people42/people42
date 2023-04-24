type TSignUpUser = {
  platform: "apple" | "google" | null;
  email: string | null;
  nickname?: string | null;
  o_auth_token: string | null;
  color?: TColorType | null;
  emoji?: string | null;
};

type TUser = {};
