import Card from "./Card";
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
              동해물과 백두산이 마르고 닳도asdfasd록동해물과 백두산이 마르고
              닳도asdfasd록
            </p>
          </>
        </Card>
      </div>
    </StyledMyMessageCard>
  );
}

export default MyMessageCard;

const StyledMyMessageCard = styled.div`
  width: 480px;
  .my-emoji {
    z-index: 3;
    position: absolute;
    margin-left: 40px;
    width: 64px;
    height: 64px;
    background-image: url("src/assets/images/emoji/animate/ghost.gif");
    background-size: 100%;
  }
  .my-message {
    padding: 32px 24px 24px 24px;
    cursor: pointer;
    transition: scale 0.3s;
    &:hover {
      scale: 1.02;
    }
    &:active {
      scale: 0.98;
    }
    & > div {
      padding: 40px 24px 24px 24px;
      background-color: ${({ theme }) => theme.color.brand.blue};
      box-sizing: border-box;
      filter: drop-shadow(
          4px 4px 0px ${(props) => props.theme.color.brand.blue + "50"}
        )
        drop-shadow(
          8px 8px 0px ${(props) => props.theme.color.brand.blue + "50"}
        );
      &:hover {
        filter: drop-shadow(
            4px 8px 0px ${(props) => props.theme.color.brand.blue + "50"}
          )
          drop-shadow(
            8px 16px 0px ${(props) => props.theme.color.brand.blue + "50"}
          );
      }
      &:active {
        filter: none;
      }
    }
    &-info {
      ${({ theme }) => theme.text.overline}
      color: ${({ theme }) => theme.color.monotone.lightTranslucent};
      margin-bottom: 4px;
    }
    &-content {
      ${({ theme }) => theme.text.subtitle1}
      color: ${({ theme }) => theme.color.monotone.light};
    }
  }
`;
