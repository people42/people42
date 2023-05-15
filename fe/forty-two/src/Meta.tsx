import React from "react";
import { Helmet } from "react-helmet-async";

type metaProps = {
  title?: string;
};

function Meta(props: metaProps) {
  const IOS_URL = import.meta.env.VITE_IOS_URL;
  return (
    <Helmet>
      {/* Primary Meta Tags */}
      <title>{props.title}</title>
      <meta name="title" content={props.title} />

      {/* iOS */}
      <meta property="al:ios:url" content={IOS_URL} />
      <meta property="al:ios:app_store_id" content="6448700604" />
      <meta property="al:ios:app_name" content="사이" />
      <meta name="apple-itunes-app" content="app-id=6448700604" />
      {/* Android */}
      <meta property="al:android:app_name" content="42" />
      <meta property="al:android:package" content="com.cider.fourtytwo" />
      {/* <meta property="al:android:url" content="안드로이드 앱 URL" />
      <meta property="al:web:url" content="안드로이드 앱 URL" /> */}
    </Helmet>
  );
}

export default Meta;
