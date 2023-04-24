import { Card } from "../../../../components/index";
import styled from "styled-components";

type randomNicknameCardProps = {};

function RandomNicknameCard() {
  return (
    <StyledRandomNicknameCard>
      <Card isShadowInner={true}>
        <p>강아지</p>
      </Card>
    </StyledRandomNicknameCard>
  );
}

export default RandomNicknameCard;

const StyledRandomNicknameCard = styled.div`
  @keyframes wordIn {
    0% {
      transform: translateY(-20px);
    }
    100% {
      transform: translateY(0px);
    }
  }

  width: 100%;
  height: 52px;
  margin-inline: 4px;
  & > div {
    display: flex;
    justify-content: center;
    align-items: center;
    & > p {
      ${({ theme }) => theme.text.subtitle2}
      animation: wordIn 0.3s ease-out
    }
  }
`;
