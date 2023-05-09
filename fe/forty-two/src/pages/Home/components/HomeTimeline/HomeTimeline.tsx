import {
  getAccessToken,
  getMyInfo,
  getNewFeed,
  getRecentFeed,
} from "../../../../api";
import { FloatIconBtn } from "../../../../components";
import Spinner from "../../../../components/Spinner/Spinner";
import { userState } from "../../../../recoil/user/atoms";
import {
  userAccessTokenState,
  userLogoutState,
} from "../../../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../../../utils";
import HomeTimelineGroup from "./HomeTimelineGroup";
import { useEffect, useState } from "react";
import { TbReload } from "react-icons/tb";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

function HomeTimeline() {
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);
  const [recentFeedList, setRecentFeedList] = useState<
    TFeed["new"][] | [null] | undefined
  >();
  const [reloadCnt, setReloadCnt] = useState<number>(0);
  const user = useRecoilValue(userLogoutState);

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
                console.log(res.data);
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
`;
