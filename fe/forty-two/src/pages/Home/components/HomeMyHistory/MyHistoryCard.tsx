import { deleteMessage } from "../../../../api";
import { Card, FloatIconBtn } from "../../../../components";
import { formatMessageDate } from "../../../../utils";
import { TbX } from "react-icons/tb";
import styled from "styled-components";

type myHistoryCardProps = {
  history: THistory;
  onClickDelete(idx: number): void;
};

function MyHistoryCard({ history, onClickDelete }: myHistoryCardProps) {
  return (
    <StyledMyHistoryCard>
      <FloatIconBtn
        onClick={() => {
          onClickDelete(history.messageIdx);
        }}
      >
        <TbX />
      </FloatIconBtn>
      <Card isShadowInner={false}>
        <div className="history-card-box">
          <p>{history.content}</p>
          <p>장소</p>
          <p>{formatMessageDate(history.createdAt)}</p>
        </div>
      </Card>
    </StyledMyHistoryCard>
  );
}

export default MyHistoryCard;

const StyledMyHistoryCard = styled.div`
  animation: floatingDown 0.3s;
  width: 100%;
  margin-block: 16px;
  position: relative;
  word-wrap: break-word;
  word-break: keep-all;

  & > Button {
    position: absolute;
    z-index: 3;
    background-color: ${({ theme }) => theme.color.brand.red};
    top: -4px;
    right: -4px;
  }

  .history-card-box {
    padding: 24px;
  }
`;
