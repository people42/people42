import { getMyInfo } from "../../api";
import { userLoadedState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import Card from "./Card";
import { useEffect, useState } from "react";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type myMessageCardProps = {};

function MyMessageCard({}: myMessageCardProps) {
  const accessToken = useRecoilValue(userAccessTokenState);
  const isLoaded = useRecoilValue<boolean>(userLoadedState);

  const [myMessage, setMyMessage] = useState<TMyMessage>();
  useEffect(() => {
    getMyInfo(accessToken).then((res) => setMyMessage(res.data.data));
  }, [isLoaded]);

  return (
    <StyledMyMessageCard>
      {myMessage ? (
        <div
          className="my-emoji"
          style={{
            backgroundImage: `url(src/assets/images/emoji/animate/${myMessage?.emoji}.gif)`,
          }}
        ></div>
      ) : null}
      <div className="my-message">
        <Card isShadowInner={false}>
          <div>
            <p className="my-message-info">지금 나의 생각</p>
            {myMessage ? (
              myMessage?.message ? (
                <p className="my-message-content">{myMessage?.message}</p>
              ) : (
                <p className="my-message-content">{"내 생각을 작성해주세요"}</p>
              )
            ) : (
              <Skeleton
                baseColor="#ffffff2c"
                highlightColor="#ffffff44"
                height={23}
              ></Skeleton>
            )}
          </div>
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
    animation: floatingUp 0.3s;
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
