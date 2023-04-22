import "./assets/fonts/pretendard/pretendard-subset.css";
import "./assets/fonts/pretendard/pretendard.css";
import Home from "./pages/Home/Home";
import { SignIn, SignUp } from "./pages/index";
import { themeState } from "./recoil/theme/atoms";
import "./reset.css";
import { GlobalStyle } from "./style";
import { lightStyles, darkStyles } from "./styles/theme";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { useRecoilState } from "recoil";
import { ThemeProvider } from "styled-components";

const router = createBrowserRouter([
  {
    path: "/",
    element: <Home />,
  },
  {
    path: "/signin",
    element: <SignIn />,
  },
  {
    path: "/signup",
    element: <SignUp />,
  },
]);

function App() {
  const [isDark, setIsDark] = useRecoilState(themeState);

  return (
    <ThemeProvider theme={isDark ? darkStyles : lightStyles}>
      <GlobalStyle />
      <GoogleOAuthProvider clientId="630522923660-vjvl4kc0rh8eni5erbd9qb3a7tidshph.apps.googleusercontent.com">
        <RouterProvider router={router} />
      </GoogleOAuthProvider>
    </ThemeProvider>
  );
}

export default App;
