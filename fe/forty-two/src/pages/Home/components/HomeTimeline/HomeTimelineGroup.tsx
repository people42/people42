import { MessageCard } from "../../../../components";
import styled from "styled-components";

function HomeTimelineGroup() {
  return (
    <StyledHomeTimelineGroup>
      <div className="location">
        <div className="location-info">
          <p className="location-info-name">삼성화재 유성연수원 asdf</p>
          <p className="location-info-time">시간12</p>
        </div>
        <div className="location-dot"></div>
      </div>
      <MessageCard color={"red"}></MessageCard>
    </StyledHomeTimelineGroup>
  );
}

export default HomeTimelineGroup;

const StyledHomeTimelineGroup = styled.article`
  display: flex;
  align-items: center;
  margin-left: 16px;

  .location {
    flex-shrink: 0;
    margin-top: 18px;
    width: 100px;
    display: flex;

    &-info {
      &-name {
        ${({ theme }) => theme.text.overline}
        width: 96px;
        flex-grow: 1;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
      &-time {
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
