import "./reset.css";
import { useState } from "react";
import { GlobalStyle } from "./style";
import { ThemeProvider } from "styled-components";
import { lightStyles, darkStyles } from "./theme";
import "./assets/fonts/pretendard/pretendard.css";
import "./assets/fonts/pretendard/pretendard-subset.css";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { createBrowserRouter, RouterProvider } from "react-router-dom";

import { SignIn, SignUp } from "./pages/index";

const router = createBrowserRouter([
  {
    path: "/",
    element: <SignIn />,
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
  const [themeMode, setThemeMode] = useState(lightStyles);

  return (
    <ThemeProvider theme={themeMode}>
      <GlobalStyle />
      <GoogleOAuthProvider clientId="630522923660-vjvl4kc0rh8eni5erbd9qb3a7tidshph.apps.googleusercontent.com">
        <RouterProvider router={router} />
      </GoogleOAuthProvider>
    </ThemeProvider>
  );
}

export default App;
