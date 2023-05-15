import { getNotionPage } from "../../api";
import { NavBar } from "../../components";
import "prismjs/themes/prism-tomorrow.css";
import React, { useEffect, useState } from "react";
import { NotionRenderer } from "react-notion";
import "react-notion/src/styles.css";
import { useSearchParams } from "react-router-dom";
import styled from "styled-components";

function Policy() {
  const [blockMap, setBlockMap] = useState({});

  const [searchParams, setSeratchParams] = useSearchParams();
  useEffect(() => {
    const NOTION_PAGE_ID = "0459c526e4bb42628cadca219bfd7a55";
    getNotionPage(NOTION_PAGE_ID).then((res) => {
      setBlockMap(res.data);
    });
  }, []);

  return (
    <StyledHome isNav={searchParams.get("nav")}>
      {searchParams.get("nav") == "false" ? null : <NavBar></NavBar>}
      <NotionRenderer blockMap={blockMap} fullPage={true} />
    </StyledHome>
  );
}

export default React.memo(Policy);

const StyledHome = styled.div<{ isNav: string | null }>`
  nav {
    background-color: ${({ theme }) => theme.color.background.primary};
  }
  .notion {
    margin-bottom: 64px;
    display: flex;
    justify-content: center;

    &-page {
      margin: 0px;
      padding: 0px;
      & > * {
        margin-inline: 24px;
      }

      &-header {
        display: none;
      }
    }
    &-title {
      position: sticky;
      ${({ isNav }) => (isNav == "false" ? "top: 0px" : "top: 48px")};
      word-break: keep-all;
      ${({ theme }) => theme.text.header4};
      background-color: ${({ theme }) => theme.color.background.primary};
      margin: 0px;
      padding: 16px 36px;
    }
  }
`;
