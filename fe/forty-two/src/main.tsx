import App from "./App";
import React, { Suspense } from "react";
import { CookiesProvider } from "react-cookie";
import ReactDOM from "react-dom/client";
import { HelmetProvider } from "react-helmet-async";
import { RecoilRoot } from "recoil";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <RecoilRoot>
    <Suspense fallback={<div>Loading...</div>}>
      <HelmetProvider>
        <CookiesProvider>
          <App />
        </CookiesProvider>
      </HelmetProvider>
    </Suspense>
  </RecoilRoot>
);
