import { deleteMessage } from "../../../../api";
import { Card, FloatIconBtn } from "../../../../components";
import { formatMessageDate } from "../../../../utils";
import { TbX } from "react-icons/tb";
import styled from "styled-components";

type myHistoryCardProps = {
  idx: number;
  history: THistory;
  onClickDelete(idx: number): void;
};

function MyHistoryCard({ idx, history, onClickDelete }: myHistoryCardProps) {
  return (
    <StyledMyHistoryCard idx={idx}>
      <FloatIconBtn
        onClick={() => {
          onClickDelete(history.messageIdx);
        }}
      >
        <TbX />
      </FloatIconBtn>
      <Card isShadowInner={false}>
        <div className="history-card">
          <p className="history-card-content">{history.content}</p>
          <p className="history-card-time">
            {formatMessageDate(history.createdAt)} {"장소"}에서
          </p>
        </div>
      </Card>
    </StyledMyHistoryCard>
  );
}

export default MyHistoryCard;

const StyledMyHistoryCard = styled.div<{ idx: number }>`
  display: inline-block;
  animation: floatingDown 0.3s;
  animation-fill-mode: both;
  animation-delay: ${({ idx }) => idx / 10}s;
  width: 100%;
  padding: 4px;
  position: relative;
  word-wrap: break-word;
  word-break: keep-all;

  & > Button {
    position: absolute;
    z-index: 3;
    background-color: ${({ theme }) => theme.color.brand.red};
    top: 0px;
    right: 0px;
    & > svg {
      color: ${({ theme }) => theme.color.monotone.light};
    }
  }

  .history-card {
    padding: 16px 20px;
    &-content {
      ${({ theme }) => theme.text.body2}
      margin-bottom: 4px;
    }
    &-time {
      ${({ theme }) => theme.text.caption}
      color: ${({ theme }) => theme.color.text.secondary};
    }
  }
`;
