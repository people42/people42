import { getAccessToken, postFeedEmotion } from "../../api";
import { userState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../utils";
import { useEffect, useState } from "react";
import { TbPlus } from "react-icons/tb";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type reactionButtonProps = {
  props: TFeed["recent"];
};

function reactionButton({ props }: reactionButtonProps) {
  const [mouseUp, setMouseUp] = useState<boolean>(false);
  const reactionList: TReaction[] = ["heart", "fire", "tear", "thumbsUp"];
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);
  const [emotion, setEmotion] = useState("delete");

  useEffect(() => {
    if (props?.recentMessageInfo.emotion) {
      setEmotion(props?.recentMessageInfo.emotion);
    }
  }, []);

  const onClickReaction = (emotion: TReaction) => {
    if (props?.recentMessageInfo.messageIdx && accessToken)
      postFeedEmotion(accessToken, {
        emotion: emotion,
        messageIdx: props?.recentMessageInfo.messageIdx,
      })
        .then((res) => setEmotion(emotion))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
              postFeedEmotion(res.data.data.accessToken, {
                emotion: emotion,
                messageIdx: props?.recentMessageInfo.messageIdx,
              }).then((res) => setEmotion(emotion));
            });
          }
        });
  };

  return (
    <StyledReactionButton
      onMouseOver={() => setMouseUp(true)}
      onMouseLeave={() => setMouseUp(false)}
    >
      <div>
        {mouseUp ? (
          <>
            {reactionList.map((reaction) => (
              <div
                key={`reaction-icon-${reaction}`}
                onClick={(e) => {
                  onClickReaction(reaction);
                }}
                className="reaction-icon"
                style={{
                  backgroundImage: `url("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/reaction/${reaction}.png")`,
                }}
              ></div>
            ))}
            <TbPlus
              onClick={(e) => {
                onClickReaction("delete");
              }}
              className="reaction-icon-close"
              size={24}
              style={{ rotate: `${mouseUp ? "45deg" : "0deg"}` }}
            />
          </>
        ) : emotion == "delete" ? (
          <TbPlus
            onClick={(e) => {
              onClickReaction("delete");
            }}
            className="reaction-icon-close"
            size={24}
            style={{ rotate: `${mouseUp ? "45deg" : "0deg"}` }}
          />
        ) : (
          <div
            className="reaction-icon"
            style={{
              backgroundImage: `url("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/reaction/${emotion}.png")`,
            }}
          ></div>
        )}
      </div>
    </StyledReactionButton>
  );
}

export default reactionButton;

const StyledReactionButton = styled.button`
  z-index: 3;
  position: absolute;
  bottom: -16px;
  right: -16px;
  margin-right: 8px;
  border: none;
  border-radius: 32px;
  width: 32px;
  height: 32px;
  background: none;
  transition: all 0.3s;
  padding: 4px;
  ${({ theme }) => theme.shadow.iconShadow};
  background-color: ${({ theme }) => theme.color.background.secondary};
  &:hover {
    width: 140px;
    background-color: ${({ theme }) => theme.color.background.secondary};
  }

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
        transform: scale(1.4);
      }
      &:active {
        transform: scale(0.9);
      }

      &-close {
        transition: all 0.3s;
        cursor: pointer;
        &:hover {
          transform: scale(1.1);
          color: red;
        }
        &:active {
          transform: scale(0.9);
        }
      }
    }
  }
`;
