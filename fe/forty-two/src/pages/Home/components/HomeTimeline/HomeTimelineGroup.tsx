import { MessageCard } from "../../../../components";
import styled from "styled-components";

interface homeTimeLineGroupProps {
  props: TFeed["recent"];
  idx: number;
}

function HomeTimelineGroup({ props, idx }: homeTimeLineGroupProps) {
  return (
    <StyledHomeTimelineGroup>
      <div className="location">
        <div
          className="location-info"
          style={{ animationDelay: `${0.1 * idx}s` }}
        >
          <p className="location-info-name">
            {props ? props.placeWithTimeInfo.placeName : ""}
          </p>
          <p className="location-info-time">
            {props ? props.placeWithTimeInfo.time : ""}
          </p>
        </div>
        {
          <div
            className="location-dot"
            style={{
              animationDelay: `${0.1 * idx}s`,
              backgroundColor: `${props ? null : "gray"}`,
            }}
          ></div>
        }
      </div>
      {props ? (
        <MessageCard idx={idx} props={props}></MessageCard>
      ) : (
        <div className="not-message">아직 메시지가 없습니다.</div>
      )}
    </StyledHomeTimelineGroup>
  );
}

export default HomeTimelineGroup;

const StyledHomeTimelineGroup = styled.article`
  display: flex;
  align-items: start;
  margin-left: 16px;
  margin-block: 16px;

  .location {
    flex-shrink: 0;
    margin-top: 18px;
    display: flex;
    text-align: end;

    &-info {
      animation: floatingLeft 0.7s;
      animation-fill-mode: both;
      &-name {
        flex-shrink: 0;
        ${({ theme }) => theme.text.overline}
        width: 96px;
        flex-grow: 1;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
      &-time {
        width: 96px;
        flex-shrink: 0;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        ${({ theme }) => theme.text.overline}
      }
    }

    &-dot {
      animation: popIn 0.3s;
      animation-fill-mode: both;
      flex-shrink: 0;
      margin: 8px;
      width: 16px;
      height: 16px;
      border: white 2px solid;
      border-radius: 12px;
      background-color: ${({ theme }) => theme.color.brand.blue};
      ${({ theme }) => theme.shadow.iconShadow};
    }
  }

  .not-message {
    color: ${({ theme }) => theme.color.text.secondary};
    height: 64px;
    display: flex;
    align-items: center;
  }
`;
