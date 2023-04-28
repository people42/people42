import { ReactionBtn } from "..";
import Card from "./Card";
import styled from "styled-components";

type messageCardProps = {
  props: TFeed["recent"];
  idx: number;
};

function MessageCard({ props, idx }: messageCardProps) {
  return props ? (
    <StyledMessageCard
      color={props.recentMessageInfo.color}
      style={{ animationDelay: `${0.1 * idx}s` }}
    >
      <div className="emoji"></div>
      <ReactionBtn></ReactionBtn>
      <div className="message">
        <Card isShadowInner={false}>
          <>
            <p className="message-nickname">
              {props.recentMessageInfo.nickname}
            </p>
            <p className="message-content">{props.recentMessageInfo.content}</p>
          </>
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
}>`
  animation: floatingRight 0.7s;
  animation-fill-mode: both;
  max-width: 280px;
  position: relative;
  cursor: pointer;
  transition: scale 0.3s;
  &:hover {
    scale: 1.02;
  }
  &:active {
    scale: 0.98;
  }
  .emoji {
    z-index: 3;
    position: absolute;
    margin-left: 16px;
    width: 36px;
    height: 36px;
    background-image: url("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/hugging-face.gif");
    background-size: 100%;
  }

  .message {
    padding-top: 18px;
    & > div {
      padding: 24px 16px 16px 16px;
      background-color: ${(props) =>
        props.theme.color.card[props.color] + "a0"};
      filter: drop-shadow(
          4px 4px 0px ${(props) => props.theme.color.card[props.color] + "50"}
        )
        drop-shadow(
          8px 8px 0px ${(props) => props.theme.color.card[props.color] + "50"}
        );
      &:hover {
        filter: drop-shadow(
            4px 8px 0px ${(props) => props.theme.color.card[props.color] + "50"}
          )
          drop-shadow(
            8px 16px 0px
              ${(props) => props.theme.color.card[props.color] + "50"}
          );
      }
      &:active {
        filter: none;
      }
    }
    &-nickname {
      ${({ theme }) => theme.text.overline}
      color: ${({ theme }) => theme.color.text.secondary};
      margin-bottom: 4px;
    }
    &-content {
      ${({ theme }) => theme.text.body2}
      color: ${({ theme }) => theme.color.text.primary};
    }
  }
`;
