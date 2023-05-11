import { getAccessToken, getNewFeed } from "../../../../api";
import { FloatIconBtn } from "../../../../components";
import Spinner from "../../../../components/Spinner/Spinner";
import { userState } from "../../../../recoil/user/atoms";
import {
  userAccessTokenState,
  userLogoutState,
} from "../../../../recoil/user/selectors";
import HomeTimelineGroup from "./HomeTimelineGroup";
import { useEffect, useState } from "react";
import { TbReload } from "react-icons/tb";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

function HomeTimeline() {
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);
  const [recentFeedList, setRecentFeedList] = useState<
    TFeed["new"][] | [null] | undefined
  >();
  const [reloadCnt, setReloadCnt] = useState<number>(0);
  const [sumMessageCnt, setSumMessageCnt] = useState<number>(0);
  const user = useRecoilValue(userLogoutState);

  useEffect(() => {
    if (recentFeedList) {
      let total = 0;
      for (let idx = 0; idx < recentFeedList.length; idx++) {
        const firstTimeUserEmojis =
          recentFeedList[idx]?.recentUsersInfo?.firstTimeUserEmojis;
        if (Array.isArray(firstTimeUserEmojis)) {
          total += firstTimeUserEmojis.length;
        }
      }
      setSumMessageCnt(total);
    }
  }, [recentFeedList]);

  const getFeed = () => {
    setRecentFeedList(undefined);
    if (accessToken) {
      getNewFeed(accessToken)
        .then((res) => {
          if (res.data.data && res.data.data.length > 0) {
            setRecentFeedList(res.data.data);
          } else {
            setRecentFeedList([null]);
          }
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getNewFeed(res.data.data.accessToken).then((res) => {
                if (res.data.data && res.data.data.length > 0) {
                  setRecentFeedList(res.data.data);
                } else {
                  setRecentFeedList([null]);
                }
              });
              setUserRefresh(res.data.data);
            });
          }
        });
    }
  };

  useEffect(() => {
    getFeed();
  }, [user?.accessToken]);

  return (
    <StyledHomeTimeline>
      <div key={reloadCnt} className="reload-btn">
        <FloatIconBtn
          onClick={() => {
            getFeed();
            setReloadCnt(reloadCnt + 1);
          }}
        >
          <TbReload />
        </FloatIconBtn>
      </div>
      {recentFeedList !== undefined ? (
        recentFeedList.map((data: TFeed["new"] | null, idx: number) => (
          <HomeTimelineGroup
            key={`timeline-${idx}`}
            idx={idx}
            props={data}
          ></HomeTimelineGroup>
        ))
      ) : (
        <div className="load-feed-list">
          <Spinner></Spinner>
        </div>
      )}
      {recentFeedList ? (
        <>
          <div className="feed-report">최근 24시간 동안</div>
          <div className="feed-report">
            {recentFeedList.length}개의 장소에서
          </div>
          <div className="feed-report">
            {sumMessageCnt}명의 생각과 스쳤습니다.
          </div>
        </>
      ) : null}
    </StyledHomeTimeline>
  );
}

export default HomeTimeline;

const StyledHomeTimeline = styled.section`
  flex-shrink: 0;
  width: 380px;
  padding-right: 30px;
  position: relative;
  padding-bottom: 40px;
  height: calc(100vh - 56px);
  overflow-y: scroll;
  overflow-x: hidden;
  &::-webkit-scrollbar {
    background-color: none;
    width: 8px;
  }
  &::-webkit-scrollbar-thumb {
    background-color: ${({ theme }) => theme.color.text.primary + "10"};
    border-radius: 8px;
  }
  &::-webkit-scrollbar-track {
    background-color: none;
  }
  .reload-btn {
    margin-left: 32px;
    animation: reloadIn 0.3s both;
    animation-delay: 5s;
    display: flex;
    justify-content: center;
    width: 100%;
  }
  .load-feed-list {
    margin-left: 191px;
    width: 24px;
    height: 72px;
  }
  .feed-report {
    ${({ theme }) => theme.text.subtitle2}
    color: ${({ theme }) => theme.color.text.secondary};
    margin-left: 156px;
    animation: floatingRight 0.3s both;
    display: flex;
    justify-content: start;
    width: 100%;
  }
`;
