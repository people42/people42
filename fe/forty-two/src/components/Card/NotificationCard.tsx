import { updateNotificationState } from "../../recoil/notification/selector";
import Card from "./Card";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type notificationCardProps = {};

function NotificationCard({}: notificationCardProps) {
  const newNotification = useRecoilValue(updateNotificationState);
  return (
    <StyledNotificationCard
      style={
        newNotification
          ? {
              animation: newNotification?.isShow
                ? "notificationIn 1.6s both"
                : "notificationOut 0.3s both",
            }
          : { transform: "translate(-50%, -160%)" }
      }
    >
      <Card isShadowInner={false} onClick={() => {}}>
        <>
          <img className="noti-icon" src={newNotification?.icon}></img>
          <div>
            <p className="noti-title">{newNotification?.title}</p>
            <p className="noti-body">{newNotification?.body}</p>
          </div>
        </>
      </Card>
    </StyledNotificationCard>
  );
}

export default NotificationCard;

const StyledNotificationCard = styled.div`
  z-index: 100;
  position: fixed;
  top: 8px;
  left: 50%;
  padding: 16px;

  & > div {
    min-height: 64px;
    padding: 8px;
    display: flex;
    align-items: center;
    overflow: hidden;
  }
  .noti-icon {
    width: 40px;
    height: 40px;
    margin: 4px;
    margin-right: 16px;
    flex-shrink: 0;
  }
  .noti-title {
    ${({ theme }) => theme.text.subtitle1}
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 100%;
  }
  .noti-body {
    color: ${({ theme }) => theme.text.caption};
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 100%;
  }
`;
