import { MyMessageCard, MyMessageListCard } from "../../../../components";
import { ReactElement } from "react";
import styled from "styled-components";

type myMessageListProps = {};

function MyMessageList({}: myMessageListProps) {
  return (
    <StyledMyMessageList>
      <MyMessageCard></MyMessageCard>
      {/* <header>지난 나의 메시지 목록</header>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard>
      <MyMessageListCard></MyMessageListCard> */}
    </StyledMyMessageList>
  );
}

export default MyMessageList;

const StyledMyMessageList = styled.div`
  height: 100%;
  padding: 8px;
  box-sizing: border-box;
  overflow-y: scroll;
  &::-webkit-scrollbar {
    display: none;
  }
  & > div {
  }
`;
