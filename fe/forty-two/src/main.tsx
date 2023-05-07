import App from "./App";
import Spinner from "./components/Spinner/Spinner";
import { Suspense } from "react";
import ReactDOM from "react-dom/client";
import { HelmetProvider } from "react-helmet-async";
import { RecoilRoot } from "recoil";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <RecoilRoot>
    <Suspense fallback={<Spinner></Spinner>}>
      <HelmetProvider>
        <App />
      </HelmetProvider>
    </Suspense>
  </RecoilRoot>
);
