import Card from "./Card";
import { ReactElement } from "react";
import styled from "styled-components";

type myMessageCardProps = {};

function MyMessageCard({}: myMessageCardProps) {
  return (
    <StyledMyMessageCard>
      <div className="my-emoji"></div>
      <div className="my-message">
        <Card isShadowInner={false}>
          <>
            <p className="my-message-info">지금 나의 생각</p>
            <p className="my-message-content">
              동해물과 백두산이 마르고 닳도록
            </p>
          </>
        </Card>
      </div>
    </StyledMyMessageCard>
  );
}

export default MyMessageCard;

const StyledMyMessageCard = styled.div`
  max-width: 480px;
  cursor: pointer;
  transition: scale 0.3s;
  &:hover {
    scale: 1.02;
  }
  &:active {
    scale: 0.98;
  }
  .my-emoji {
    position: absolute;
    margin-left: 24px;
    width: 64px;
    height: 64px;
    background-image: url("src/assets/images/emoji/animate/ghost.gif");
    background-size: 100%;
  }
  .my-message {
    padding-top: 32px;
    & > div {
      padding: 40px 24px 24px 24px;
      background-color: ${({ theme }) => theme.color.brand.blue};
      box-sizing: border-box;
    }
    &-info {
      ${({ theme }) => theme.text.overline}
      color: ${({ theme }) => theme.color.monotone.lightTranslucent};
      margin-bottom: 4px;
    }
    &-content {
      ${({ theme }) => theme.text.header6}
      color: ${({ theme }) => theme.color.monotone.light};
    }
  }
`;
