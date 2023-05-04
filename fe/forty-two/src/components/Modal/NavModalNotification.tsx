import { getAccessToken, getNotificationHistory } from "../../api";
import { isNotificationPermittedState } from "../../recoil/notification/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import NavModalNotificationRow from "./NavModalNotificationRow";
import { useState, useEffect } from "react";
import { TbBellOff } from "react-icons/tb";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type navModalNotificationProps = {};

function NavModalNotification({}: navModalNotificationProps) {
  const isNotificationPermitted = useRecoilValue(isNotificationPermittedState);

  const accessToken = useRecoilValue(userAccessTokenState);
  const [notificationList, setNotificationList] = useState([]);

  useEffect(() => {
    if (accessToken) {
      getNotificationHistory(accessToken)
        .then((res) => setNotificationList(res.data.data))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getNotificationHistory(res.data.data.accessToken).then((res) =>
                setNotificationList(res.data.data)
              );
            });
          }
        });
    }
  }, [accessToken]);

  return (
    <StyledNavModalNotification>
      {isNotificationPermitted ? (
        notificationList.length ? (
          <>
            <p>{notificationList.length}건의 새로운 알림</p>
            {notificationList.map((data, idx) => (
              <NavModalNotificationRow
                key={`notification-list-${idx}`}
                data={data}
              />
            ))}
          </>
        ) : (
          <div className="no-notification">새로운 알림이 없어요</div>
        )
      ) : (
        <div className="no-permission-notification">
          <TbBellOff size={48} />
          <p>알림을 불러올 수 없습니다</p>
          <p>알림 권한을 허용해주세요</p>
        </div>
      )}
    </StyledNavModalNotification>
  );
}

export default NavModalNotification;

const StyledNavModalNotification = styled.div`
  & > p {
    ${({ theme }) => theme.text.subtitle2}
    color: ${({ theme }) => theme.color.brand.blue};
    padding-inline: 8px;
    padding-bottom: 8px;
  }
  .no-notification {
    height: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
    ${({ theme }) => theme.text.subtitle2}
    color: ${({ theme }) => theme.color.text.secondary};
  }
  .no-permission-notification {
    width: 100%;
    height: 180px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    ${({ theme }) => theme.text.subtitle1}
    & > svg {
      margin-bottom: 0px;
      color: ${({ theme }) => theme.color.brand.red};
    }
    & > p:last-child {
      ${({ theme }) => theme.text.caption}
      color: ${({ theme }) => theme.color.text.secondary};
    }
  }
`;
