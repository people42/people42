import React from "react";
import { Helmet } from "react-helmet-async";

type metaProps = {
  title?: string;
};

function Meta(props: metaProps) {
  return (
    <Helmet>
      {/* Primary Meta Tags */}
      <title>{props.title}</title>
      <meta name="title" content={props.title} />

      {/* iOS
      <meta property="al:ios:url" content=" ios 앱 URL" />
      <meta property="al:ios:app_store_id" content="ios 앱스토어 ID" />
      <meta property="al:ios:app_name" content="ios 앱 이름" />
      Android
      <meta property="al:android:url" content="안드로이드 앱 URL" />
      <meta property="al:android:app_name" content="안드로이드 앱 이름" />
      <meta property="al:android:package" content="안드로이드 패키지 이름" />
      <meta property="al:web:url" content="안드로이드 앱 URL" /> */}
    </Helmet>
  );
}

export default Meta;
