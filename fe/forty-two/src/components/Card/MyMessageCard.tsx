import { getAccessToken, getMyInfo } from "../../api";
import { userState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../utils";
import MyMessageCardInput from "../Input/MyMessageCardInput";
import Card from "./Card";
import { useEffect, useState } from "react";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type myMessageCardProps = {
  isMessageEdit: boolean;
  setIsMessageEdit: Function;
};

function MyMessageCard({
  isMessageEdit,
  setIsMessageEdit,
}: myMessageCardProps) {
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);

  const [myMessage, setMyMessage] = useState<TMyMessage>();
  useEffect(() => {
    if (accessToken && !isMessageEdit) {
      getMyInfo(accessToken)
        .then((res) => setMyMessage(res.data.data))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
    }
  }, [accessToken, isMessageEdit]);

  return (
    <StyledMyMessageCard>
      {myMessage ? (
        <div
          className="my-emoji"
          style={{
            backgroundImage: `url("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${myMessage?.emoji}.gif")`,
          }}
        ></div>
      ) : null}
      <div
        onClick={() => {
          isMessageEdit ? null : setIsMessageEdit(true);
        }}
        className="my-message"
      >
        <Card isShadowInner={false}>
          <div>
            <p className="my-message-info">지금 나의 생각</p>
            {isMessageEdit ? (
              <MyMessageCardInput
                onClickCancel={() => setIsMessageEdit(false)}
              ></MyMessageCardInput>
            ) : myMessage ? (
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
  z-index: 2;
  animation: floatingDown 0.3s;
  position: sticky;
  top: 48px;
  width: 480px;
  padding: 32px 0px 0px 0px;
  .my-emoji {
    z-index: 3;
    position: absolute;
    margin-left: 24px;
    margin-top: -36px;
    width: 64px;
    height: 64px;
    animation: floatingUp 0.3s;
    background-size: 100%;
  }
  .my-message {
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
      word-wrap: break-word;
      word-break: keep-all;
    }
  }
`;
