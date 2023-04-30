import { deleteMessage, getAccessToken, getMyHistory } from "../../../../api";
import { userState } from "../../../../recoil/user/atoms";
import { userAccessTokenState } from "../../../../recoil/user/selectors";
import { getTodayDate, setSessionRefreshToken } from "../../../../utils";
import MyHistoryCard from "./MyHistoryCard";
import { useEffect, useState } from "react";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type homeMyHistoryProps = {};

function HomeMyHistory({}: homeMyHistoryProps) {
  const [historyList, setHistoryList] = useState<THistory[]>([]);
  const [needHistoryRefresh, setNeedHistoryRefresh] = useState<boolean>(true);

  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);
  useEffect(() => {
    if (accessToken && needHistoryRefresh) {
      getMyHistory(accessToken, getTodayDate())
        .then((res) => setHistoryList(res.data.data))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getMyHistory(res.data.data.accessToken, getTodayDate()).then(
                (res) => setHistoryList(res.data.data)
              );
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
      setNeedHistoryRefresh(false);
    }
  }, [needHistoryRefresh]);

  const deleteMyMessage = (idx: number) => {
    deleteMessage(accessToken, idx)
      .then((res) => setNeedHistoryRefresh(true))
      .catch((e) => {
        if (e.response.status == 401) {
          getAccessToken().then((res) => {
            deleteMessage(res.data.data.accessToken, idx).then(() =>
              setNeedHistoryRefresh(true)
            );
            setUserRefresh(res.data.data);
            setSessionRefreshToken(res.data.data.refreshToken);
          });
        }
      });
  };

  return (
    <StyledHomeMyHistory>
      <div className="history-title">
        오늘 남긴 메시지 {historyList.length}건
      </div>
      {historyList.map((history, idx) => (
        <MyHistoryCard
          key={`history-${idx}`}
          idx={idx}
          history={history}
          onClickDelete={deleteMyMessage}
        ></MyHistoryCard>
      ))}
    </StyledHomeMyHistory>
  );
}

export default HomeMyHistory;

const StyledHomeMyHistory = styled.article`
  z-index: 2;
  margin-top: -24px;
  padding-block: 48px 36px;
  padding-inline: 8px 8px;
  width: 480px;
  height: 100%;
  overflow: scroll;

  .history-title {
    ${({ theme }) => theme.text.subtitle1}
    color: ${({ theme }) => theme.color.text.secondary};
  }
`;
