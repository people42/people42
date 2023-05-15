import { Card, MessageCard } from "../../../../components";
import UserMapMessageCard from "./UserMapMessageCard";
import { TbX } from "react-icons/tb";
import styled from "styled-components";

type userMapMessageListProps = {
  placeInfo: TUserDetail["placeResDtos"][0] | null;
  messagesInfo: TUserDetail["placeMessageInfo"][];
  setIsOpen(isOpen: boolean): void;
};

function UserMapMessageList({
  placeInfo,
  messagesInfo,
  setIsOpen,
}: userMapMessageListProps) {
  return (
    <StyledUserMapMessageList>
      <Card isShadowInner={false} onClick={() => {}}>
        <>
          <div className="close-btn">
            <TbX
              size={24}
              onClick={() => setIsOpen(false)}
              aria-label="장소별 메시지 확인 패널 닫기"
            />
          </div>
          <h2>{placeInfo?.placeName} 근처</h2>
          <h3>이 장소에서 {placeInfo?.brushCnt}번 스쳤습니다.</h3>
          {messagesInfo.map((data, idx) => (
            <UserMapMessageCard
              key={`message-info-card-${idx}`}
              data={data}
            ></UserMapMessageCard>
          ))}
        </>
      </Card>
    </StyledUserMapMessageList>
  );
}

export default UserMapMessageList;

const StyledUserMapMessageList = styled.aside`
  animation: floatingLeft 0.3s;
  padding: 24px;
  top: 0px;
  right: 0px;
  position: absolute;
  width: 300px;
  height: 100%;

  & > div {
    overflow-y: scroll;
    &::-webkit-scrollbar {
      background-color: none;
      width: 8px;
    }
    &::-webkit-scrollbar-thumb {
      background-color: ${({ theme }) => theme.color.text.primary + "10"};
      border-radius: 8px;
    }
    &::-webkit-scrollbar-track {
      background-color: none;
    }
    padding: 24px;

    & h2 {
      ${({ theme }) => theme.text.header6}
    }
    & h3 {
      ${({ theme }) => theme.text.caption}
    }
  }
  .close-btn {
    margin-top: -8px;
    margin-right: -16px;
    display: flex;
    justify-content: end;
    & > svg {
      cursor: pointer;
    }
  }
`;
