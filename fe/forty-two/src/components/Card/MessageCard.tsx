import Card from "./Card";
import styled from "styled-components";

type messageCardProps = {};

function MessageCard({}: messageCardProps) {
  return (
    <StyledMessageCard>
      <div className="emoji"></div>
      <div className="reaction"></div>
      <div className="message">
        <Card isShadowInner={false}>
          <>
            <p className="message-nickname">다른사람닉네임</p>
            <p className="message-content">동해물과 백두산이 마르고 닳도록</p>
          </>
        </Card>
      </div>
      <div className="first-background">
        <Card isShadowInner={false}>
          <></>
        </Card>
      </div>
    </StyledMessageCard>
  );
}

export default MessageCard;

const StyledMessageCard = styled.div`
  max-width: 480px;
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
    position: absolute;
    margin-left: 24px;
    width: 36px;
    height: 36px;
    background-image: url("src/assets/images/emoji/animate/ghost.gif");
    background-size: 100%;
  }
  .reaction {
    position: absolute;
    bottom: -8px;
    right: 0px;
    margin-left: 24px;
    width: 36px;
    height: 36px;
    background-image: url("src/assets/images/emoji/animate/ghost.gif");
    background-size: 100%;
  }
  .message {
    padding-top: 18px;
    & > div {
      padding: 24px 24px 24px 24px;
      background-color: ${({ theme }) => theme.color.card.red};
      box-sizing: border-box;
    }
    &-nickname {
      ${({ theme }) => theme.text.overline}
      color: ${({ theme }) => theme.color.monotone.lightTranslucent};
      margin-bottom: 4px;
    }
    &-content {
      ${({ theme }) => theme.text.body}
      color: ${({ theme }) => theme.color.monotone.light};
    }
  }
  .first-background {
    padding-top: 18px;
    position: absolute;
    z-index: -1;
    top: 8px;
    left: 8px;
    width: 100px;
    height: 100px;
  }
`;
