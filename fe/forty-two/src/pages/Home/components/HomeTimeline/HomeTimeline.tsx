import { getAccessToken, getMyInfo, getRecentFeed } from "../../../../api";
import { userState } from "../../../../recoil/user/atoms";
import { userAccessTokenState } from "../../../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../../../utils";
import HomeTimelineGroup from "./HomeTimelineGroup";
import { useEffect, useState } from "react";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

function HomeTimeline() {
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);
  const [recentFeedList, setRecentFeedList] = useState<TFeed["recent"][]>([
    null,
  ]);

  useEffect(() => {
    if (accessToken) {
      getRecentFeed(accessToken)
        .then((res) => {
          if (res.data.data && res.data.data.length > 0) {
            setRecentFeedList(res.data.data);
          }
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
    }
  }, [accessToken]);

  return (
    <StyledHomeTimeline>
      {recentFeedList.map((data: any, idx: number) => (
        <HomeTimelineGroup
          key={`timeline-${idx}`}
          idx={idx}
          props={data}
        ></HomeTimelineGroup>
      ))}
      <div className="timeline-bar"></div>
    </StyledHomeTimeline>
  );
}

export default HomeTimeline;

const StyledHomeTimeline = styled.section`
  flex-shrink: 0;
  width: 360px;
  position: relative;

  .timeline-bar {
    position: absolute;
    top: 50px;
    left: 126px;
    width: 4px;
    height: 100%;
    z-index: -3;
    background-color: ${({ theme }) => theme.color.text.secondary};
  }
`;
