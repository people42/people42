import {
  Account,
  AppleAccountCheck,
  DeepLink,
  Home,
  LocationManage,
  Logout,
  NotFound,
  Place,
  Policy,
  SignIn,
  SignUp,
  User,
  Withdrawal,
} from "./pages";
import { AccountSet } from "./pages/Account/components";
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
  {
    path: "/account",
    element: <Account />,
  },
  {
    path: "/account/set/:type",
    element: <AccountSet />,
  },
  {
    path: "/xxx",
    element: <LocationManage />,
  },
  {
    path: "/*",
    element: <NotFound />,
  },
]);

export default browserRouter;
