import { Card, FloatIconBtn } from "../../../../components";
import { TbX } from "react-icons/tb";
import styled from "styled-components";

type myHistoryCardProps = {};

function MyHistoryCard({}: myHistoryCardProps) {
  return (
    <StyledMyHistoryCard>
      <FloatIconBtn onClick={() => {}}>
        <TbX />
      </FloatIconBtn>
      <Card isShadowInner={false}>
        <div className="history-card-box">
          <p>내 이전기록</p>
          <p>장소</p>
          <p>시간</p>
        </div>
      </Card>
    </StyledMyHistoryCard>
  );
}

export default MyHistoryCard;

const StyledMyHistoryCard = styled.div`
  width: 100%;
  height: 100px;
  margin-block: 16px;
  position: relative;

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
