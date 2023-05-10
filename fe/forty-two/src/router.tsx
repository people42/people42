import {
  AppleAccountCheck,
  DeepLink,
  Home,
  Logout,
  Place,
  Policy,
  SignIn,
  SignUp,
  User,
  Withdrawal,
} from "./pages";
import { createBrowserRouter } from "react-router-dom";

const browserRouter = createBrowserRouter([
  {
    path: "/",
    element: <Home />,
  },
  {
    path: "/place",
    element: <Place />,
  },
  {
    path: "/user/:user_id",
    element: <User />,
  },
  {
    path: "/policy",
    element: <Policy />,
  },
  {
    path: "/signin/apple",
    element: <AppleAccountCheck />,
  },
  {
    path: "/signin",
    element: <SignIn />,
  },
  {
    path: "/signup",
    element: <SignUp />,
  },
  {
    path: "/logout",
    element: <Logout />,
  },
  {
    path: "/withdrawal/:platform",
    element: <Withdrawal />,
  },
  {
    path: "/mobile",
    element: <DeepLink />,
  },
]);

export default browserRouter;
