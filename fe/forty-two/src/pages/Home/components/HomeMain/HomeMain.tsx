import { HomeMyMessage } from "..";
import { homeInfoState } from "../../../../recoil/home/atoms";
import { isLoginState } from "../../../../recoil/user/atoms";
import HomeInfo from "../HomeInfo/HomeInfo";
import HomeTimeline from "../HomeTimeline/HomeTimeline";
import HomeMainPreview from "./HomeMainPreview";
import { useEffect } from "react";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

function HomeMain() {
  const isLogin = useRecoilValue(isLoginState);
  const [showHomeInfo, setShowHomeInfo] =
    useRecoilState<boolean>(homeInfoState);

  useEffect(() => {
    if (!localStorage.getItem("home-info")) {
      setShowHomeInfo(true);
    }
  }, []);

  return (
    <StyledHomeMain>
      {isLogin === false ? (
        <HomeMainPreview></HomeMainPreview>
      ) : (
        <>
          <HomeTimeline></HomeTimeline>
          <div className="timeline-bar"></div>
          {showHomeInfo ? <HomeInfo /> : null}
        </>
      )}
      <HomeMyMessage></HomeMyMessage>
    </StyledHomeMain>
  );
}

export default HomeMain;

const StyledHomeMain = styled.main`
  width: 100%;
  height: calc(100vh - 56px);
  max-width: 1024px;
  display: flex;
  flex-grow: 1;

  .timeline-bar {
    position: absolute;
    bottom: 0px;
    margin-left: 126px;
    width: 4px;
    height: 100%;
    z-index: -3;
    background-color: ${({ theme }) => theme.color.text.primary + "30"};
  }
`;
