import MyHistoryCard from "./MyHistoryCard";
import styled from "styled-components";

type homeMyHistoryProps = {};

function HomeMyHistory({}: homeMyHistoryProps) {
  return (
    <StyledHomeMyHistory>
      <div>내가 오늘 남긴 메시지 0건 여기에 달력</div>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
      <MyHistoryCard></MyHistoryCard>
    </StyledHomeMyHistory>
  );
}

export default HomeMyHistory;

const StyledHomeMyHistory = styled.article`
  z-index: 1;
  margin-top: -24px;
  padding-block: 48px 36px;
  width: 480px;
  height: 100%;
  padding-inline: 8px;

  overflow: scroll;
`;
