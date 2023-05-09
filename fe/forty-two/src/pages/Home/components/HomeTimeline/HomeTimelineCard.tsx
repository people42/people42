import { Card } from "../../../../components";
import styled from "styled-components";

type homeTimelineCardProps = {
  props: TFeed["new"];
  idx: number;
  onClick(): void;
};

function HomeTimelineCard({ props, idx, onClick }: homeTimelineCardProps) {
  const S3_URL = import.meta.env.VITE_S3_URL;
  return (
    <StyledHomeTimelineCard
      onClick={onClick}
      style={{
        animationDelay: `${0.1 * idx}s`,
      }}
    >
      <Card isShadowInner={false}>
        <>
          <div className="home-timeline-nickname">
            {props?.recentUsersInfo.nickname} 등
          </div>
          <div className="home-timeline-user-cnt">
            {props?.recentUsersInfo.userCnt}명과 스쳤습니다
          </div>

          {props?.recentUsersInfo.repeatUserEmojis.length > 0 ? (
            <>
              <div className="home-timeline-emoji-title">
                다시 만난 {props?.recentUsersInfo.repeatUserEmojis.length}명
              </div>
              <div className="home-timeline-emoji">
                {props.recentUsersInfo.repeatUserEmojis.map((value, idx) => {
                  if (idx < 3) {
                    return (
                      <div
                        className="home-timeline-emoji-icon"
                        key={`near-user-${idx}`}
                        style={{
                          backgroundImage: `url("${S3_URL}emoji/animate/${value}.gif")`,
                        }}
                      ></div>
                    );
                  } else if (idx < 5) {
                    return (
                      <div
                        className="home-timeline-emoji-icon"
                        key={`near-user-${idx}`}
                        style={{
                          backgroundImage: `url("${S3_URL}emoji/static/${value}.png")`,
                        }}
                      ></div>
                    );
                  } else if (idx == 5) {
                    return (
                      <div
                        className="home-timeline-emoji-icon"
                        key={`near-user-${idx}`}
                        style={{
                          backgroundImage: `url("${S3_URL}emoji/static/${value}.png")`,
                          filter: "opacity(0.6)",
                        }}
                      ></div>
                    );
                  } else if (idx == 6) {
                    return (
                      <div
                        className="home-timeline-emoji-icon"
                        key={`near-user-${idx}`}
                        style={{
                          backgroundImage: `url("${S3_URL}emoji/static/${value}.png")`,
                          filter: "opacity(0.2)",
                        }}
                      ></div>
                    );
                  }
                })}
              </div>
            </>
          ) : null}
          {props?.recentUsersInfo.firstTimeUserEmojis.length > 0 ? (
            <>
              <div className="home-timeline-emoji-title">
                처음 만난 {props?.recentUsersInfo.firstTimeUserEmojis.length}명
              </div>
              <div className="home-timeline-emoji">
                {props.recentUsersInfo.firstTimeUserEmojis.map((value, idx) => {
                  if (idx < 3) {
                    return (
                      <div
                        className="home-timeline-emoji-icon"
                        key={`near-user-${idx}`}
                        style={{
                          backgroundImage: `url("${S3_URL}emoji/animate/${value}.gif")`,
                        }}
                      ></div>
                    );
                  } else if (idx < 5) {
                    return (
                      <div
                        className="home-timeline-emoji-icon"
                        key={`near-user-${idx}`}
                        style={{
                          backgroundImage: `url("${S3_URL}emoji/static/${value}.png")`,
                        }}
                      ></div>
                    );
                  } else if (idx == 5) {
                    return (
                      <div
                        className="home-timeline-emoji-icon"
                        key={`near-user-${idx}`}
                        style={{
                          backgroundImage: `url("${S3_URL}emoji/static/${value}.png")`,
                          filter: "opacity(0.6)",
                        }}
                      ></div>
                    );
                  } else if (idx == 6) {
                    return (
                      <div
                        className="home-timeline-emoji-icon"
                        key={`near-user-${idx}`}
                        style={{
                          backgroundImage: `url("${S3_URL}emoji/static/${value}.png")`,
                          filter: "opacity(0.2)",
                        }}
                      ></div>
                    );
                  }
                })}
              </div>
            </>
          ) : null}
        </>
      </Card>
    </StyledHomeTimelineCard>
  );
}

export default HomeTimelineCard;

const StyledHomeTimelineCard = styled.div`
  cursor: pointer;
  animation: floatingRight 0.3s both;
  &:active {
    scale: 0.98;
  }
  & > div {
    &:hover {
      filter: ${({ theme }) =>
        theme.isDark == true ? "brightness(1.15)" : "brightness(0.95)"};
    }
    padding: 16px;
    & > div {
      ${({ theme }) => theme.text.subtitle2}
    }
  }
  .home-timeline-nickname {
    ${({ theme }) => theme.text.caption}
    font-weight: 300;
  }
  .home-timeline-user-cnt {
    ${({ theme }) => theme.text.subtitle2}
    margin-bottom: 8px;
  }
  .home-timeline-emoji {
    display: flex;
    margin-bottom: 8px;
    &-title {
      ${({ theme }) => theme.text.overline}
      color: ${({ theme }) => theme.color.text.secondary};
      margin-bottom: 4px;
    }
    &-icon {
      margin-right: -8px;
      width: 32px;
      height: 32px;
      background-size: cover;
      &:last-child {
        margin-right: 0px;
      }
    }
  }
`;
