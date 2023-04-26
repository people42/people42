const text = {
  header1:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 700; font-size: 96px; line-height: 139px;",
  header2:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 700; font-size: 60px; line-height: 87px;",
  header3:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 700; font-size: 48px; line-height: 70px;",
  header4:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 700; font-size: 34px; line-height: 49px; letter-spacing: 0.0025em;",
  header5:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 700; font-size: 24px; line-height: 35px;",
  header6:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 700; font-size: 20px; line-height: 29px;",
  subtitle1:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 500; font-size: 16px; line-height: 23px; letter-spacing: 0.0015em;",
  subtitle2:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 500; font-size: 14px; line-height: 20px; letter-spacing: 0.001em;",
  body1:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 400; font-size: 16px; line-height: 23px; letter-spacing: 0.001em;",
  body2:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 400; font-size: 14px; line-height: 20px; letter-spacing: 0.005em;",
  button:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 700; font-size: 14px; line-height: 20px; letter-spacing: 0.0125em; text-transform: uppercase;",
  caption:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 400; font-size: 12px; line-height: 17px; letter-spacing: 0.004em;",
  overline:
    "font-family: 'Pretendard'; font-style: normal; font-weight: 700; font-size: 10px; line-height: 14px; letter-spacing: 0.015em; text-transform: uppercase;",
};

const lightColor = {
  background: {
    primary: "#F5F5F5",
    secondary: "#FFFFFF",
  },
  brand: {
    blue: "#3644FC",
    red: "#FF375B",
    yellow: "#FBFF32",
  },
  monotone: {
    lightTranslucent: "rgba(255, 255, 255, 0.5)",
    light: "#FFFFFF",
    lightGray: "#EFEFEF",
    gray: "#A8A8A8",
    darkGray: "#6E6E6E",
    dark: "#151515",
    darkTranslucent: "rgba(21, 21, 21, 0.35)",
  },
  card: {
    red: "#FB4C4C",
    orange: "#F59626",
    yellow: "#FFF500",
    green: "#00C637",
    sky: "#0EC1CC",
    blue: "#167BD8",
    purple: "#A344DE",
    pink: "#DE44A0",
  },
  text: {
    primary: "#151515",
    secondary: "rgba(21, 21, 21, 0.35)",
    red: "#FF0000",
    orange: "#FFA300",
    yellow: "#FFF500",
    green: "#00FF75",
    sky: "#00C2FF",
    blue: "#0057FF",
    purple: "#9E00FF",
    pink: "#DE44A0",
  },
};

const darkColor = {
  background: {
    primary: "#22232E",
    secondary: "#404151",
  },
  brand: {
    blue: "#3644FC",
    red: "#FF375B",
    yellow: "#FBFF32",
  },
  monotone: {
    lightTranslucent: "rgba(255, 255, 255, 0.5)",
    light: "#FFFFFF",
    lightGray: "#EFEFEF",
    gray: "#A8A8A8",
    darkGray: "#6E6E6E",
    dark: "#151515",
    darkTranslucent: "rgba(21, 21, 21, 0.35)",
  },
  card: {
    red: "#4E1C25",
    orange: "#4E3D25",
    yellow: "#4E4D25",
    green: "#1B4F3C",
    sky: "#1B4364",
    blue: "#1B2D58",
    purple: "#3B1C58",
    pink: "#4E1C47",
  },
  text: {
    primary: "#FFFFFF",
    secondary: "rgba(255, 255, 255, 0.5)",
  },
};

const shadow = {
  cardShadow: "box-shadow: 4px 4px 16px rgba(0, 0, 0, 0.08);",
  iconShadow: "box-shadow: 4px 4px 12px rgba(0, 0, 0, 0.15);",
  innerShadow: "box-shadow: inset 2px 2px 4px rgba(0, 0, 0, 0.04);",
};

export const lightStyles = {
  isDark: false,
  text: text,
  color: lightColor,
  shadow: shadow,
};

export const darkStyles = {
  isDark: true,
  text: text,
  color: darkColor,
  shadow: shadow,
};
