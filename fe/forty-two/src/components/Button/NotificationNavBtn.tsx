import { getAccessToken, getNotification } from "../../api";
import { isNotificationPermittedState } from "../../recoil/notification/atoms";
import { updateNotificationState } from "../../recoil/notification/selector";
import {
  userAccessTokenState,
  userLogoutState,
} from "../../recoil/user/selectors";
import IconBtn from "./IconBtn";
import { useEffect, useState } from "react";
import { TbBellFilled, TbBellOff } from "react-icons/tb";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

type notificationNavBtnProps = {
  isNotificationModalOn: boolean;
  setIsNotificationModalOn(value: boolean): void;
};

function NotificationNavBtn({
  isNotificationModalOn,
  setIsNotificationModalOn,
}: notificationNavBtnProps) {
  const isNotificationPermitted = useRecoilValue(isNotificationPermittedState);
  const accessToken = useRecoilValue(userAccessTokenState);
  const [notificationCnt, setNotificationCnt] = useState<number>();
  const newNotification = useRecoilValue(updateNotificationState);

  useEffect(() => {
    if (accessToken) {
      getNotification(accessToken)
        .then((res) => {
          setNotificationCnt(res.data.data.notificationCnt);
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getNotification(res.data.data.accessToken).then((res) => {
                setNotificationCnt(res.data.data.notificationCnt);
              });
            });
          }
        });
    }
  }, [accessToken, newNotification]);

  return (
    <StyledNotificationNavBtn
      onClick={() => {
        setIsNotificationModalOn(!isNotificationModalOn);
        setNotificationCnt(0);
      }}
    >
      <IconBtn onClick={() => {}}>
        {isNotificationPermitted ? (
          <TbBellFilled size={24} aria-label={"알림"} />
        ) : (
          <TbBellOff size={24} aria-label={"알림"} />
        )}
      </IconBtn>
      {isNotificationPermitted && notificationCnt && notificationCnt > 0 ? (
        <div className="notification-badge">{notificationCnt}</div>
      ) : null}
    </StyledNotificationNavBtn>
  );
}

export default NotificationNavBtn;

const StyledNotificationNavBtn = styled.div`
  cursor: pointer;
  padding: 0px;
  border: none;
  background: none;
  position: relative;
  .notification-badge {
    position: absolute;
    top: 2px;
    right: 2px;
    padding: 2px;
    ${({ theme }) => theme.text.overline}
    color: ${({ theme }) => theme.color.monotone.light};
    background-color: ${({ theme }) => theme.color.brand.red};
    min-width: 20px;
    height: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 16px;
    border: 2px solid ${({ theme }) => theme.color.background.secondary};
    ${({ theme }) => theme.shadow.iconShadow}
  }
`;
