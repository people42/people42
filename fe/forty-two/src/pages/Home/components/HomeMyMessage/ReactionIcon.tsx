import styled from "styled-components";

type reactionIconProps = { type: TReaction; count: number };

function ReactionIcon({ type, count }: reactionIconProps) {
  const S3_URL = import.meta.env.VITE_S3_URL;

  return (
    <StyledReactionIcon>
      <div
        className="reaction-icon"
        style={{
          backgroundImage: `url("${S3_URL}emoji/reaction/${type}.png")`,
          transform: `scale(${count});`,
        }}
      >
        {count}
      </div>
    </StyledReactionIcon>
  );
}

export default ReactionIcon;

const StyledReactionIcon = styled.div`
  & > div {
    width: 64px;
    height: 64px;
    background-size: cover;
  }
`;
