import React from "react";
import { Helmet } from "react-helmet-async";

type metaProps = {
  title?: string;
  description?: string;
  keywords?: string;
  imgsrc?: string;
  url?: string;
};

function Meta(props: metaProps) {
  const BASE_APP_URL = import.meta.env.VITE_BASE_APP_URL;
  return (
    <Helmet>
      {/* Primary Meta Tags */}
      <title>{props.title ?? "42 | 어쩌면 마주친 사이"}</title>
      <meta name="title" content={props.title ?? "42 | 어쩌면 마주친 사이"} />
      <meta
        name="description"
        content={props.description ?? "무심코 스쳐간 인연과 생각을 나눠보세요."}
      />
      <meta
        name="keywords"
        content={props.keywords ?? "SNS, 생각, 지도, 공유, 낭만, 익명"}
      />

      {/* Open Graph / Facebook */}
      <meta property="og:type" content="website" />
      <meta property="og:url" content={BASE_APP_URL} />
      <meta
        property="og:title"
        content={props.title ?? "42 | 어쩌면 마주친 사이"}
      />
      <meta
        property="og:description"
        content={props.description ?? "무심코 스쳐간 인연과 생각을 나눠보세요."}
      />
      <meta
        property="og:image"
        content={
          props.imgsrc ??
          "https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/etc/OG_image.png"
        }
      />

      {/* Twitter */}
      <meta property="twitter:card" content="summary_large_image" />
      <meta property="twitter:url" content={BASE_APP_URL} />
      <meta
        property="twitter:title"
        content={props.title ?? "42 | 어쩌면 마주친 사이"}
      />
      <meta
        property="twitter:description"
        content={props.description ?? "무심코 스쳐간 인연과 생각을 나눠보세요."}
      />
      <meta
        property="twitter:image"
        content={
          props.imgsrc ??
          "https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/etc/OG_image.png"
        }
      ></meta>

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
