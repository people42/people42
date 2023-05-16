import { getAccessToken, getMyInfo } from "../../../../api";
import { Card } from "../../../../components";
import MyMessageCardInput from "../../../../components/Input/MyMessageCardInput";
import { locationState } from "../../../../recoil/location/atoms";
import { updateNotificationState } from "../../../../recoil/notification/selector";
import { socketState } from "../../../../recoil/socket/atoms";
import { userState } from "../../../../recoil/user/atoms";
import { userAccessTokenState } from "../../../../recoil/user/selectors";
import { changeStatus } from "../../../../utils";
import HomeMyMessageReaction from "./HomeMyMessageReaction";
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
  const newNotification = useRecoilValue(updateNotificationState);

  const [myMessage, setMyMessage] = useState<TMyMessage>();

  const socket = useRecoilValue(socketState);
  const location = useRecoilValue(locationState);

  const isEdit = () => {
    setIsMessageEdit(true);
    if (socket && location) {
      changeStatus(socket, {
        latitude: location?.latitude,
        longitude: location?.longitude,
        status: "writing",
      });
    }
  };
  const isNotEdit = () => {
    setIsMessageEdit(false);
    if (socket && location) {
      changeStatus(socket, {
        latitude: location?.latitude,
        longitude: location?.longitude,
        status: "watching",
      });
    }
  };

  useEffect(() => {
    if (accessToken && !isMessageEdit) {
      getMyInfo(accessToken)
        .then((res) => setMyMessage(res.data.data))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              setUserRefresh(res.data.data);
            });
          }
        });
    }
  }, [accessToken, isMessageEdit, newNotification]);

  const S3_URL = import.meta.env.VITE_S3_URL;

  return (
    <StyledMyMessageCard
      count={myMessage?.messageCnt ?? 0}
      isMessageEdit={isMessageEdit}
    >
      {myMessage ? <HomeMyMessageReaction myMessage={myMessage} /> : null}
      {myMessage ? (
        <div
          className="my-emoji"
          style={{
            backgroundImage: `url("${S3_URL}emoji/animate/${myMessage?.emoji}.gif")`,
          }}
        ></div>
      ) : null}
      <div
        onClick={() => {
          isMessageEdit ? null : isEdit();
        }}
        className="my-message"
      >
        <Card isShadowInner={false}>
          <div>
            <p className="my-message-info">지금 나의 생각</p>
            {isMessageEdit ? (
              <MyMessageCardInput
                onClickCancel={() => isNotEdit()}
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

const StyledMyMessageCard = styled.div<{
  count: number;
  isMessageEdit: boolean;
}>`
  z-index: 3;
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
    transition: scale 0.1s;
    &:hover {
      filter: ${({ theme }) =>
        theme.isDark == true ? "brightness(1.05)" : "brightness(0.95)"};
    }
    &:active {
      scale: 0.98;
    }

    & > div {
      padding: 40px 24px 24px 24px;
      background-color: ${({ theme }) => theme.color.brand.blue};
      box-sizing: border-box;
      filter: ${(props) =>
        props.count > 0 && !props.isMessageEdit
          ? props.count > 1
            ? `drop-shadow(4px 4px 0px ${
                props.theme.color.brand.blue + "50"
              }) drop-shadow(8px 8px 0px ${
                props.theme.color.brand.blue + "50"
              })`
            : `drop-shadow(4px 4px 0px ${props.theme.color.brand.blue + "50"})`
          : "none"};
      &:hover {
        filter: ${(props) =>
          props.count > 0 && !props.isMessageEdit
            ? props.count > 1
              ? `drop-shadow(4px 8px 0px ${
                  props.theme.color.brand.blue + "50"
                }) drop-shadow(8px 16px 0px ${
                  props.theme.color.brand.blue + "50"
                })`
              : `drop-shadow(4px 8px 0px ${
                  props.theme.color.brand.blue + "50"
                })`
            : "none"};
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
