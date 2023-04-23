import Card from "./Card";
import styled from "styled-components";

type messageCardProps = {
  color:
    | "red"
    | "orange"
    | "yellow"
    | "green"
    | "sky"
    | "blue"
    | "purple"
    | "pink";
};

function MessageCard({ color }: messageCardProps) {
  return (
    <StyledMessageCard color={color}>
      <div className="emoji"></div>
      <div className="reaction"></div>
      <div className="message">
        <Card isShadowInner={false}>
          <>
            <p className="message-nickname">다른사람닉네임</p>
            <p className="message-content">
              동해물과 백두산이 마르고 닳도asdfasdf록
            </p>
          </>
        </Card>
      </div>
    </StyledMessageCard>
  );
}

export default MessageCard;

const StyledMessageCard = styled.div<messageCardProps>`
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
    margin-left: 24px;
    width: 36px;
    height: 36px;
    background-image: url("src/assets/images/emoji/animate/hugging-face.gif");
    background-size: 100%;
  }
  .reaction {
    z-index: 3;
    position: absolute;
    bottom: -8px;
    right: 0px;
    margin-right: 16px;
    width: 24px;
    height: 24px;
    background-image: url("src/assets/images/emoji/reaction/heart.png");
    background-size: 100%;
  }
  .message {
    padding-top: 18px;
    & > div {
      padding: 24px 24px 24px 24px;
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
      ${({ theme }) => theme.text.body}
      color: ${({ theme }) => theme.color.text.primary};
    }
  }
`;
