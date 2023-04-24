import FloatIconBtn from "../Button/FloatIconBtn";
import Card from "./Card";
import React, { ReactElement } from "react";
import { TbX } from "react-icons/tb";
import styled from "styled-components";

type myMessageListCardProps = {};

function MyMessageListCard({}: myMessageListCardProps) {
  return (
    <StyledMyMessageListCard>
      <div className="my-message">
        <FloatIconBtn onClick={(e: React.MouseEvent) => console.log(1)}>
          <TbX></TbX>
        </FloatIconBtn>
        <Card isShadowInner={false}>
          <>
            <p className="my-message-created">2023년 4월 22일</p>
            <p>동해물과 백두산이 마르고 닳도록</p>
          </>
        </Card>
      </div>
    </StyledMyMessageListCard>
  );
}

export default MyMessageListCard;

const StyledMyMessageListCard = styled.div`
  max-width: 480px;
  transition: scale 0.3s;
  &:hover {
    scale: 1.02;
  }
  .my-message {
    margin-block: 16px;
    position: relative;
    & > button {
      display: none;
    }
    &:hover {
      & > button {
        display: flex;
        position: absolute;
        right: 0px;
      }
    }
    & > div {
      padding: 24px;
      box-sizing: border-box;
    }
    &-created {
      ${({ theme }) => theme.text.overline}
      color: ${({ theme }) => theme.color.text.secondary};
      margin-bottom: 8px;
    }
  }
`;
