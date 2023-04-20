import styled from "styled-components";
import Card from "../Card/Card";

type randomNicknameCardProps = {};

function RandomNicknameCard() {
  return (
    <StyledRandomNicknameCard>
      <Card isShadowInner={true}>
        <></>
      </Card>
    </StyledRandomNicknameCard>
  );
}

export default RandomNicknameCard;

const StyledRandomNicknameCard = styled.div`
  width: 100%;
  height: 52px;
  margin-inline: 4px;
`;
