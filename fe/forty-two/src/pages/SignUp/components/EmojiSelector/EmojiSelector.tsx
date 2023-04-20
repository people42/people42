import styled from "styled-components";
import { CommonBtn } from "../../../../components";

type emojiSelectorProps = { onClick(e: React.MouseEvent): void };

function EmojiSelector({ onClick }: emojiSelectorProps) {
  return (
    <StyledEmojiSelector>
      <div>asdf</div>
      <CommonBtn onClick={onClick} btnType="primary">
        결정했어요
      </CommonBtn>
    </StyledEmojiSelector>
  );
}

export default EmojiSelector;

const StyledEmojiSelector = styled.div`
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-grow: 1;
  & > div {
    flex-grow: 1;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
`;
