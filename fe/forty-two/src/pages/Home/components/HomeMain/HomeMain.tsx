import { HomeMyMessage } from "..";
import { isLoginState } from "../../../../recoil/user/atoms";
import HomeTimeline from "../HomeTimeline/HomeTimeline";
import HomeMainPreview from "./HomeMainPreview";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

function HomeMain() {
  const isLogin = useRecoilValue(isLoginState);

  return (
    <StyledHomeMain>
      {isLogin ? (
        <>
          <HomeTimeline></HomeTimeline>
          <div className="timeline-bar"></div>
        </>
      ) : (
        <HomeMainPreview></HomeMainPreview>
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
