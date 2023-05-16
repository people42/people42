import { ReactionBtn } from "..";
import Card from "./Card";
import styled from "styled-components";

type messageCardProps = {
  props: TFeed["recent"];
  idx: number;
  onClick(): void;
};

const S3_URL = import.meta.env.VITE_S3_URL;

function MessageCard({ props, idx, onClick }: messageCardProps) {
  return props ? (
    <StyledMessageCard
      count={props.recentMessageInfo.brushCnt}
      color={props.recentMessageInfo.color}
      style={{ animationDelay: `${0.1 * idx}s` }}
    >
      <div
        className="emoji"
        style={{
          backgroundImage: `url("${S3_URL}emoji/animate/${props.recentMessageInfo.emoji}.gif")`,
        }}
      ></div>
      <span className="message-nickname">
        {props.recentMessageInfo.brushCnt}번 스친{" "}
        {props.recentMessageInfo.nickname}
      </span>
      <ReactionBtn props={props}></ReactionBtn>
      <div className="message">
        <Card isShadowInner={false} onClick={onClick}>
          <div>
            <p className="message-content">{props.recentMessageInfo.content}</p>
          </div>
        </Card>
      </div>
    </StyledMessageCard>
  ) : (
    <></>
  );
}

export default MessageCard;

const StyledMessageCard = styled.div<{
  color: TColorType;
  count: number;
}>`
  animation: floatingRight 0.3s;
  animation-fill-mode: both;
  width: 100%;
  max-width: 300px;
  position: relative;

  transition: scale 0.3s;

  .emoji {
    z-index: 3;
    position: absolute;
    margin-left: 16px;
    width: 36px;
    height: 36px;
    background-size: 100%;
  }

  .message {
    transition: scale 0.1s;
    &:hover {
      filter: ${({ theme }) =>
        theme.isDark == true ? "brightness(1.5)" : "brightness(0.95)"};
    }
    &:active {
      scale: 0.98;
    }
    padding-top: 18px;
    & > div {
      cursor: pointer;
      padding: 24px 16px 16px 16px;
      background-color: ${(props) =>
        props.theme.color.card[props.color] + "a0"};
      filter: ${(props) =>
        props.count > 0
          ? props.count > 1
            ? `drop-shadow(4px 4px 0px ${
                props.theme.color.card[props.color] + "50"
              }) drop-shadow(8px 8px 0px ${
                props.theme.color.card[props.color] + "50"
              })`
            : `drop-shadow(4px 4px 0px ${
                props.theme.color.card[props.color] + "50"
              })`
          : "none"};
      &:hover {
        filter: ${(props) =>
          props.count > 0
            ? props.count > 1
              ? `drop-shadow(4px 8px 0px ${
                  props.theme.color.card[props.color] + "50"
                }) drop-shadow(8px 16px 0px ${
                  props.theme.color.card[props.color] + "50"
                })`
              : `drop-shadow(4px 8px 0px ${
                  props.theme.color.card[props.color] + "50"
                })`
            : "none"};
      }
      &:active {
        filter: none;
      }
    }
    &-nickname {
      z-index: 2;
      position: absolute;
      margin-top: 8px;
      margin-left: 40px;
      ${({ theme }) => theme.text.overline}
      color: ${(props) => props.theme.color.text[props.color]};
      padding: 4px;
      padding-inline: 24px;
      background-color: ${(props) => props.theme.color.background.secondary};
      border-radius: 8px;
      ${({ theme }) => theme.shadow.iconShadow}
    }
    &-content {
      margin-top: 8px;
      ${({ theme }) => theme.text.body2}
      color: ${({ theme }) => theme.color.text.primary};
    }
  }
`;
