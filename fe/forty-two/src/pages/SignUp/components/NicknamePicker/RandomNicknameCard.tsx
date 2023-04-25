import { Card } from "../../../../components/index";
import { useState } from "react";
import styled from "styled-components";

type randomNicknameCardProps = {};

function RandomNicknameCard(props: {
  nickname: string;
  randomWordList: string[];
  randomWordAnimation: boolean;
}) {
  return (
    <StyledRandomNicknameCard randomWordAnimation={props.randomWordAnimation}>
      <Card isShadowInner={true}>
        <div key={props.nickname}>
          {props.randomWordList.map((word, idx) => (
            <p key={`random-word-${idx}`}>{word}</p>
          ))}
        </div>
      </Card>
    </StyledRandomNicknameCard>
  );
}

export default RandomNicknameCard;

const StyledRandomNicknameCard = styled.div<{ randomWordAnimation: boolean }>`
  width: 100%;
  height: 64px;
  margin-inline: 4px;
  & > div {
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 16px;
    & > div {
      width: 80%;
      clip-path: inset(46.5% 0% 46.5% 0%);
      overflow: hidden;
      & > p {
        ${({ randomWordAnimation }) =>
          randomWordAnimation
            ? "animation: randomWordOut 0.5s liner;"
            : "animation: randomWordIn 1s cubic-bezier(0.385, 1.050, 0.705, 0.995);"}
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
        height: 64px;
        margin-block: 32px;
        ${({ theme }) => theme.text.header6};
      }
    }
  }
`;
