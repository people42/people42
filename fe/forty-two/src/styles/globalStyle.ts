import "../assets/fonts/pretendard/pretendard-subset.css";
import "../assets/fonts/pretendard/pretendard.css";
import { createGlobalStyle } from "styled-components";

export const GlobalStyle = createGlobalStyle<any>`
    @keyframes springOut {
      0% {
        filter: opacity(1);
        transform: translateY(100%) scale(100%, 100%);
      }
      50% {
        filter: opacity(1);
        transform: translateY(120%) scale(100%, 60%);
      }
      100% {
        filter: opacity(0);
        transform: translateY(100%) scale(0%, 110%);
      }
    }
    @keyframes newMessageBubble {
      0% {
        filter: opacity(0);
        transform: translate(50%, 50%) scale(0%, 0%);
        box-shadow: none;
      }
      5% {
        filter: opacity(1);
        transform: translate(0%, 0%) scale(100%, 100%);
        box-shadow: 0px 0px 48px #3644FC;
      }
      80% {
        filter: opacity(1);
        transform: translate(0%, 0%) scale(100%, 100%);
        box-shadow: 0px 0px 0px #3644FC;
      }
      100% {
        filter: opacity(0);
        transform: translate(0%, 0%) scale(100%, 100%);
        box-shadow: 0px 0px 0px #3644FC;
        /* box-shadow: none; */
      }
    }
    @keyframes writingWave {
      0% {
        filter: opacity(0.5);
      }
      20% {
        filter: opacity(1);
      }
      40% {
        filter: opacity(0.5);
      }
      100%{
        filter: opacity(0.5);
      }
    }
    @keyframes notificationIn {
      0% {
        width: 96px;
        height: 96px;
        transform: translate(-50%, -160%);
      }
      60% {
        width: 96px;
        height: 96px;
        transform: translate(-50%, 0%);
      }
      100% {
        width: 400px;
        height: 96px;
        transform: translate(-50%, 0%);
      }
    }
    @keyframes notificationOut {
      from {
        width: 400px;
        height: 64px;
        transform: translate(-50%, 0%);
      }
      to {
        width: 400px;
        height: 64px;
        transform: translate(-50%, -160%);
      }
    }
    @keyframes circleIn30 {
      from {
        filter: opacity(0);
        transform: scale(0);
      }
      to {
        filter: opacity(0.3);
        transform: scale(1);
      }
    }
    @keyframes circleIn60 {
      from {
        filter: opacity(0);
        transform: scale(0);
      }
      to {
        filter: opacity(0.6);
        transform: scale(1);
      }
    }
    @keyframes circleSpread {
      0% {
        filter: opacity(0);
        transform: scale(0);
      }
      50% {
        filter: opacity(1);
        transform: scale(0.5);
      }
      100% {
        filter: opacity(0);
        transform: scale(1);
      }
    }
    @keyframes mapInDark {
      from {
        filter: opacity(0) invert(95%) sepia(60%) hue-rotate(180deg) saturate(100%) brightness(1.2);
      }
      to {
        filter: opacity(1) invert(95%) sepia(60%) hue-rotate(180deg) saturate(100%) brightness(1.2);
      }
    }
    @keyframes reloadIn {
      0% {
        transform: scale(0);
        height: 0px;
      }
      80% {
        transform: scale(1.2);
        height: 28px;
      }
      100% {
        transform: scale(1);
        height: 24px;
      }
    }
    @keyframes popIn {
      0% {
        transform: scale(0);
      }
      80% {
        transform: scale(1.2);
      }
      100% {
        transform: scale(1);
      }
    }
    @keyframes fadeIn {
      from {
        filter: opacity(0);
      }
      to {
        filter: opacity(1);
      }
    }
    @keyframes modalOn {
      from {
        filter: opacity(0);
        transform: 
        translate(50%, -50%) scale(0%, 0%);
      }
      to {
        filter: opacity(1);
        transform: translate(0%, 0%) scale(100%, 100%);
      }
    }
    @keyframes randomWordOut {
      from {
        transform: translateY(0);
      }
      to {
        transform: translateY(500%);
      }
    }
    @keyframes randomWordIn {
      from {
        transform: translateY(-500%);
      }
      to {
        transform: translateY(0px);
      }
    }
    @keyframes randomWordOut {
      from {
        transform: translateY(0);
      }
      to {
        transform: translateY(500%);
      }
    }
  @keyframes floatingUp {
    from {
      filter: opacity(0);
      transform: translateY(100%);
    }
    to {
      filter: opacity(1);
      transform: translateY(0px);
    }
  }
  @keyframes floatingDown {
    from {
      filter: opacity(0);
      transform: translateY(-100%);
    }
    to {
      filter: opacity(1);
      transform: translateY(0px);
    }
  }
  @keyframes floatingLeft {
    from {
      filter: opacity(0);
      transform: translateX(10%);
    }
    to {
      filter: opacity(1);
      transform: translateX(0px);
    }
  }
  @keyframes floatingRight {
    from {
      filter: opacity(0);
      transform: translateX(-10%);
    }
    to {
      filter: opacity(1);
      transform: translateX(0px);
    }
  }
  body,
  div,
  span,
  applet,
  object,
  iframe,
  h1,
  h2,
  h3,
  h4,
  h5,
  h6,
  p,
  blockquote,
  pre,
  a,
  abbr,
  acronym,
  address,
  big,
  cite,
  code,
  del,
  dfn,
  em,
  img,
  ins,
  kbd,
  q,
  s,
  samp,
  small,
  strike,
  strong,
  sub,
  sup,
  tt,
  var,
  b,
  u,
  i,
  center,
  dl,
  dt,
  dd,
  ol,
  ul,
  li,
  fieldset,
  form,
  label,
  legend,
  table,
  caption,
  tbody,
  tfoot,
  thead,
  tr,
  th,
  td,
  article,
  aside,
  canvas,
  details,
  embed,
  figure,
  figcaption,
  footer,
  header,
  hgroup,
  menu,
  nav,
  output,
  ruby,
  section,
  summary,
  time,
  mark,
  audio,
  video {
    font-family: 'Pretendard';
    color: ${({ theme }) => theme.color.text.primary};
    box-sizing: border-box;
  }
  body {
    background-color: ${({ theme }) => theme.color.background.primary};
  }
 
`;
