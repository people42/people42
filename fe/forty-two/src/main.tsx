import App from "./App";
import React, { Suspense } from "react";
import ReactDOM from "react-dom/client";
import { HelmetProvider } from "react-helmet-async";
import { RecoilRoot } from "recoil";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <RecoilRoot>
      <Suspense fallback={<div>Loading...</div>}>
        <HelmetProvider>
          <App />
        </HelmetProvider>
      </Suspense>
    </RecoilRoot>
  </React.StrictMode>
);
