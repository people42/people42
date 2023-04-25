import { getNotionPage } from "../../api";
import { NavBar } from "../../components";
import "prismjs/themes/prism-tomorrow.css";
import React, { useEffect, useState } from "react";
import { NotionRenderer } from "react-notion";
import "react-notion/src/styles.css";
import styled from "styled-components";

function Policy() {
  const [blockMap, setBlockMap] = useState({});

  useEffect(() => {
    const NOTION_PAGE_ID = "0459c526e4bb42628cadca219bfd7a55";
    getNotionPage(NOTION_PAGE_ID).then((res) => {
      setBlockMap(res.data);
    });
  }, []);

  return (
    <StyledHome>
      <NavBar></NavBar>
      <NotionRenderer blockMap={blockMap} fullPage={true} />
    </StyledHome>
  );
}

export default React.memo(Policy);

const StyledHome = styled.div`
  .notion-page-header {
    display: none;
  }
  .notion {
    margin-bottom: 64px;
  }
`;
