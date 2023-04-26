import { MessageCard } from "../../../../components";
import HomeTimelineGroup from "./HomeTimelineGroup";
import styled from "styled-components";

const dataList = [
  {
    recentMessageInfo: {
      messageIdx: 102,
      content: "user3이 남긴 메시지1메시지1 메시지1메시지1메시지1 메시지1",
      userIdx: 102,
      nickname: "user3",
      emoji: "confused",
      color: "red",
      brushCnt: 1,
    },
    placeWithTimeInfo: {
      placeIdx: 62,
      placeName: "장소2",
      time: "오늘 14시쯤",
    },
  },
  {
    recentMessageInfo: {
      messageIdx: 102,
      content: "user3이 남긴 메시지asf1",
      userIdx: 102,
      nickname: "user3",
      emoji: "confused",
      color: "orange",
      brushCnt: 1,
    },
    placeWithTimeInfo: {
      placeIdx: 62,
      placeName: "장소2",
      time: "오늘 14시쯤",
    },
  },
  {
    recentMessageInfo: {
      messageIdx: 102,
      content: "user3이 남긴 메시지1메시지1 메시지1메시지1메시지1 메시지1",
      userIdx: 102,
      nickname: "user3",
      emoji: "confused",
      color: "yellow",
      brushCnt: 1,
    },
    placeWithTimeInfo: {
      placeIdx: 62,
      placeName: "장소2",
      time: "오늘 14시쯤",
    },
  },
  {
    recentMessageInfo: {
      messageIdx: 102,
      content: "user3이 남긴 메시지asf1",
      userIdx: 102,
      nickname: "user3",
      emoji: "confused",
      color: "green",
      brushCnt: 1,
    },
    placeWithTimeInfo: {
      placeIdx: 62,
      placeName: "장소2",
      time: "오늘 14시쯤",
    },
  },
  {
    recentMessageInfo: {
      messageIdx: 102,
      content: "user3이 남긴 메시지1메시지1 메시지1메시지1메시지1 메시지1",
      userIdx: 102,
      nickname: "user3",
      emoji: "confused",
      color: "sky",
      brushCnt: 1,
    },
    placeWithTimeInfo: {
      placeIdx: 62,
      placeName: "장소2",
      time: "오늘 14시쯤",
    },
  },
  {
    recentMessageInfo: {
      messageIdx: 102,
      content: "user3이 남긴 메시지1메시지1 메시지1메시지1메시지1 메시지1",
      userIdx: 102,
      nickname: "user3",
      emoji: "confused",
      color: "blue",
      brushCnt: 1,
    },
    placeWithTimeInfo: {
      placeIdx: 62,
      placeName: "장소2",
      time: "오늘 14시쯤",
    },
  },
  {
    recentMessageInfo: {
      messageIdx: 102,
      content: "user3이 남긴 메시지asf1",
      userIdx: 102,
      nickname: "user3",
      emoji: "confused",
      color: "purple",
      brushCnt: 1,
    },
    placeWithTimeInfo: {
      placeIdx: 62,
      placeName: "장소2",
      time: "오늘 14시쯤",
    },
  },
  {
    recentMessageInfo: {
      messageIdx: 102,
      content: "user3이 남긴 메시지asf1",
      userIdx: 102,
      nickname: "user3",
      emoji: "confused",
      color: "pink",
      brushCnt: 1,
    },
    placeWithTimeInfo: {
      placeIdx: 62,
      placeName: "장소2",
      time: "오늘 14시쯤",
    },
  },
];

function HomeTimeline() {
  return (
    <StyledHomeTimeline>
      {dataList.map((data: any, idx: number) => (
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
