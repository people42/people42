import { MessageCard } from "../../../../components";
import styled from "styled-components";

interface homeTimeLineGroupProps {
  props: TFeed["recent"];
}

function HomeTimelineGroup({ props }: homeTimeLineGroupProps) {
  return (
    <StyledHomeTimelineGroup>
      <div className="location">
        <div className="location-info">
          <p className="location-info-name">
            {props.placeWithTimeInfo.placeName}
          </p>
          <p className="location-info-time">{props.placeWithTimeInfo.time}</p>
        </div>
        <div className="location-dot"></div>
      </div>
      <MessageCard props={props.recentMessageInfo}></MessageCard>
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
      flex-shrink: 0;
      margin: 8px;
      width: 16px;
      height: 16px;
      border-radius: 12px;
      background-color: ${({ theme }) => theme.color.brand.blue};
    }
  }
`;
