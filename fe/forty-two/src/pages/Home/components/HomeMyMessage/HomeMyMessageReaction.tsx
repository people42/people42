import { relativeReactionObject } from "../../../../utils";
import ReactionIcon from "./ReactionIcon";
import styled from "styled-components";

type homeMyMessageReactionProps = {
  myMessage: TMyMessage;
};

function HomeMyMessageReaction({ myMessage }: homeMyMessageReactionProps) {
  const reactionCount = relativeReactionObject({
    fire: myMessage.fire,
    heart: myMessage.heart,
    thumbsUp: myMessage.thumbsUp,
    tear: myMessage.tear,
  });

  return (
    <StyledHomeMyMessageReaction>
      <ReactionIcon type="heart" count={reactionCount.heart} />
      <ReactionIcon type="fire" count={reactionCount.fire} />
      <ReactionIcon type="tear" count={reactionCount.tear} />
      <ReactionIcon type="thumbsUp" count={reactionCount.thumbsUp} />
    </StyledHomeMyMessageReaction>
  );
}

export default HomeMyMessageReaction;

const StyledHomeMyMessageReaction = styled.div`
  z-index: 10;
  display: flex;
  align-items: start;
  position: absolute;
  right: 24px;
  top: 0px;
  height: 72px;
`;
