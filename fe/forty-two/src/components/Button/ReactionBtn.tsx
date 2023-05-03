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
  const [isOpen, setIsOpen] = useState<boolean>(false);
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

  const S3_URL = import.meta.env.VITE_S3_URL;

  return (
    <StyledReactionButton
      onClick={() => setIsOpen(!isOpen)}
      onMouseLeave={(e) => setIsOpen(false)}
      isOpen={isOpen}
    >
      <div>
        {isOpen ? (
          <>
            {reactionList.map((reaction) => (
              <div
                key={`reaction-icon-${reaction}`}
                onClick={(e) => {
                  onClickReaction(reaction);
                  setEmotion(reaction);
                }}
                className="reaction-icon"
                style={{
                  backgroundImage: `url("${S3_URL}emoji/reaction/${reaction}.png")`,
                }}
              ></div>
            ))}
            <TbPlus
              onClick={(e) => {
                setEmotion("delete");
                onClickReaction("delete");
              }}
              className="reaction-icon-close"
              size={24}
              style={{ rotate: "45deg" }}
            />
          </>
        ) : emotion == "delete" ? (
          <TbPlus
            onClick={(e) => {
              onClickReaction("delete");
            }}
            className="reaction-icon-close"
            size={24}
          />
        ) : (
          <div
            className="reaction-icon"
            style={{
              backgroundImage: `url("${S3_URL}emoji/reaction/${emotion}.png")`,
            }}
          ></div>
        )}
      </div>
    </StyledReactionButton>
  );
}

export default reactionButton;

const StyledReactionButton = styled.button<{ isOpen: boolean }>`
  cursor: pointer;
  z-index: 5;
  position: absolute;
  bottom: 0px;
  right: -24px;
  margin-right: 8px;
  border: none;
  border-radius: 32px;
  width: ${({ isOpen }) => (isOpen ? "136px" : "32px")};
  height: 32px;
  background: none;
  transition: all 0.1s;
  padding: 4px;
  ${({ theme }) => theme.shadow.iconShadow};
  background-color: ${({ theme }) => theme.color.background.secondary};

  display: flex;
  justify-content: end;
  align-items: center;
  & > div {
    display: flex;
    justify-content: center;
    align-items: center;
    .reaction-icon {
      cursor: pointer;
      width: 24px;
      height: 24px;
      background-size: cover;
      transition: all 0.3s;
      &:hover {
        filter: ${({ theme }) =>
          theme.isDark == true ? "brightness(1.5)" : "brightness(1.2)"};
      }
      &:active {
        transform: scale(0.9);
      }
    }
  }
`;
