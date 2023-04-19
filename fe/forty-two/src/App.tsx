import SignIn from "./pages/SignIn";
import { createBrowserRouter, RouterProvider } from "react-router-dom";

import { lightStyles, darkStyles } from "./theme";
import { useState } from "react";
import { ThemeProvider } from "styled-components";

const router = createBrowserRouter([
  {
    path: "/",
    element: <SignIn />,
  },
]);

function App() {
  const [themeMode, setThemeMode] = useState(lightStyles);

  return (
    <ThemeProvider theme={themeMode}>
      <RouterProvider router={router} />
    </ThemeProvider>
  );
}

export default App;
