import { notificationDateTime } from "../../utils";
import styled from "styled-components";

type navModalNotificationRowProps = { data: TNotificationHistory };

function NavModalNotificationRow({ data }: navModalNotificationRowProps) {
  const S3_URL = import.meta.env.VITE_S3_URL;
  return (
    <StyledNavModalNotificationRow>
      <img src={`${S3_URL}emoji/reaction/${data.emoji}.png`}></img>
      <div>
        <p className="notification-title">{data.title}</p>
        <p className="notification-body">{data.body}</p>
        <p className="notification-time">
          {notificationDateTime(data.createdAt)}
        </p>
      </div>
    </StyledNavModalNotificationRow>
  );
}

export default NavModalNotificationRow;

const StyledNavModalNotificationRow = styled.div`
  padding-block: 8px;
  & > img {
    width: 40px;
    height: 40px;
    margin: 4px;
    margin-right: 8px;
  }
  display: flex;

  .notification-title {
    ${({ theme }) => theme.text.subtitle2};
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 210px;
  }
  .notification-body {
    ${({ theme }) => theme.text.caption};
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 210px;
  }
  .notification-time {
    ${({ theme }) => theme.text.overline};
    color: ${({ theme }) => theme.color.text.secondary};
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 210px;
  }
`;
