import fire from "../../assets/images/emoji/reaction/fire.png";
import heart from "../../assets/images/emoji/reaction/heart.png";
import tear from "../../assets/images/emoji/reaction/tear.png";
import thumbsUp from "../../assets/images/emoji/reaction/thumbsUp.png";
import { useState } from "react";
import { TbPlus } from "react-icons/tb";
import styled from "styled-components";

type reactionButtonProps = {};

function reactionButton({}: reactionButtonProps) {
  const [mouseUp, setMouseUp] = useState<boolean>(false);
  return (
    <StyledReactionButton
      onMouseOver={() => setMouseUp(true)}
      onMouseLeave={() => setMouseUp(false)}
    >
      <div>
        {mouseUp ? (
          <>
            <div
              className="reaction-icon"
              style={{
                backgroundImage: `url("${heart}")`,
              }}
            ></div>
            <div
              className="reaction-icon"
              style={{
                backgroundImage: `url("${tear}")`,
              }}
            ></div>
            <div
              className="reaction-icon"
              style={{
                backgroundImage: `url("${fire}")`,
              }}
            ></div>
            <div
              className="reaction-icon"
              style={{
                backgroundImage: `url("${thumbsUp}")`,
              }}
            ></div>
          </>
        ) : null}
        <TbPlus
          className="reaction-icon"
          size={24}
          style={{ rotate: `${mouseUp ? "45deg" : "0deg"}` }}
        />
      </div>
    </StyledReactionButton>
  );
}

export default reactionButton;

const StyledReactionButton = styled.button`
  z-index: 3;
  position: absolute;
  bottom: -16px;
  right: 0px;
  margin-right: 8px;
  border: none;
  border-radius: 32px;
  width: 32px;
  height: 32px;
  background: none;
  transition: all 0.3s;
  padding: 4px;
  background-color: ${({ theme }) => theme.color.background.secondary};
  &:hover {
    width: 140px;
    background-color: ${({ theme }) => theme.color.background.secondary};
  }

  ${({ theme }) => theme.shadow.cardShadow};
  display: flex;
  justify-content: end;
  align-items: center;
  & > div {
    display: flex;
    justify-content: center;
    align-items: center;
    .reaction-icon {
      animation: floatingRight 0.7s;
      cursor: pointer;
      width: 24px;
      height: 24px;
      background-size: cover;
      transition: all 0.3s;
      &:hover {
        transform: scale(1.1);
      }
      &:active {
        transform: scale(0.9);
      }
    }
  }
`;
