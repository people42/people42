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
    </Helmet>
  );
}

export default Meta;
