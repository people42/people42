import styled from "styled-components";

type reactionIconProps = { type: TReaction; count: number };

function ReactionIcon({ type, count }: reactionIconProps) {
  const S3_URL = import.meta.env.VITE_S3_URL;

  return (
    <StyledReactionIcon>
      {count > 0 ? (
        <img
          className="reaction-icon"
          src={`${S3_URL}emoji/reaction/${type}.png`}
          style={{
            width: 72 * count,
            height: 72 * count,
            marginTop: Math.floor(Math.random() * 24) + 8,
            transition: "all 0.3s",
          }}
          alt={`${type} resaction ${count}개`}
        ></img>
      ) : null}
    </StyledReactionIcon>
  );
}

export default ReactionIcon;

const StyledReactionIcon = styled.div`
  margin-inline: -4px;
  & > img {
    width: 100%;
    height: 100%;
    padding: 8px;
    border-radius: 100%;
    background-color: ${({ theme }) => theme.color.background.secondary};
    ${({ theme }) => theme.shadow.iconShadow}
  }
`;
