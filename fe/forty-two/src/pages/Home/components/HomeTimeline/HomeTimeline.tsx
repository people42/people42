import {
  getAccessToken,
  getMyInfo,
  getNewFeed,
  getRecentFeed,
} from "../../../../api";
import { FloatIconBtn } from "../../../../components";
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
  const [recentFeedList, setRecentFeedList] = useState<TFeed["new"][]>([]);
  const user = useRecoilValue(userLogoutState);

  const getFeed = () => {
    setRecentFeedList([]);
    if (accessToken) {
      getNewFeed(accessToken)
        .then((res) => {
          if (res.data.data && res.data.data.length > 0) {
            setRecentFeedList(res.data.data);
          }
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getNewFeed(res.data.data.accessToken).then((res) => {
                if (res.data.data && res.data.data.length > 0) {
                  setRecentFeedList(res.data.data);
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
      <div className="reload-btn">
        <FloatIconBtn onClick={getFeed}>
          <TbReload />
        </FloatIconBtn>
      </div>
      {recentFeedList.map((data: TFeed["new"], idx: number) => (
        <HomeTimelineGroup
          key={`timeline-${idx}`}
          idx={idx}
          props={data}
        ></HomeTimelineGroup>
      ))}
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
`;
