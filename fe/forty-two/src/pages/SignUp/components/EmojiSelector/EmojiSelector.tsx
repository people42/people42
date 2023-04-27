import { emojiNameList } from "../../../../assets/emojiList";
import { CommonBtn } from "../../../../components";
import { signUpUserState } from "../../../../recoil/user/atoms";
import _ from "lodash";
import { useEffect, useState } from "react";
import { TbArrowBigRightFilled, TbArrowBigLeftFilled } from "react-icons/tb";
import { useRecoilState } from "recoil";
import styled from "styled-components";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import { Swiper, SwiperSlide } from "swiper/react";

type emojiSelectorProps = { onClick(e: React.MouseEvent): void };
const emojiList: string[] = _.sampleSize(emojiNameList, 22);

function EmojiSelector({ onClick }: emojiSelectorProps) {
  const [signUpUser, setSignUpUser] = useRecoilState(signUpUserState);

  const setSignUpUserEmoji = (emoji: string) => {
    const newSignUpUser = Object.assign({}, signUpUser);
    newSignUpUser.emoji = emoji;
    setSignUpUser(newSignUpUser);
  };

  const staticEmojiSlideList: any[] = emojiList.map((name) => {
    return (
      <SwiperSlide key={name} id={name}>
        <StaticEmojiIcon
          style={{
            backgroundImage: `url("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/static/${name}.png")`,
          }}
        ></StaticEmojiIcon>
      </SwiperSlide>
    );
  });
  let swiperElement: any;
  const [swiperMethod, setSwiperMethod] = useState<any>();
  useEffect(() => {
    swiperElement = document.querySelector(".swiper");
    setSwiperMethod(swiperElement.swiper);
  }, []);

  const [activeEmojiIndex, setActiveEmojiIndex] = useState(0);

  const handleSlideChange = (swiper: any) => {
    setActiveEmojiIndex(swiper.activeIndex);
  };

  useEffect(() => {
    setSignUpUserEmoji(document.querySelector(".swiper-slide-active")!.id);
  }, [activeEmojiIndex]);

  return (
    <StyledEmojiSelector>
      <div>
        <div className="selected-emoji">
          <TbArrowBigLeftFilled
            onClick={(e) => {
              swiperMethod?.slidePrev();
              setActiveEmojiIndex(activeEmojiIndex - 1);
            }}
            size={36}
            color="#A8A8A8"
          ></TbArrowBigLeftFilled>
          <div>
            <SelectedEmojiIcon
              style={{
                backgroundImage: `url("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${signUpUser.emoji}.gif")`,
              }}
            ></SelectedEmojiIcon>
          </div>
          <TbArrowBigRightFilled
            onClick={(e) => {
              swiperMethod?.slideNext();
              setActiveEmojiIndex(activeEmojiIndex + 1);
            }}
            size={36}
            color="#A8A8A8"
          ></TbArrowBigRightFilled>
        </div>
        <div>
          <Swiper
            onSlideChange={handleSlideChange}
            allowSlideNext={true}
            allowSlidePrev={true}
            slidesPerView={9}
            loop={true}
            centeredSlides={true}
            className="emojiSwiper"
            grabCursor={true}
          >
            {staticEmojiSlideList}
          </Swiper>
        </div>
      </div>
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
    & > div:first-child {
      flex-grow: 1;
    }
  }
  .swiper {
    padding-block: 16px;
    height: 64px;
  }
  .swiper-slide {
    display: flex;
    justify-content: center;
    transition: 0.5s all;
  }
  .swiper-slide-active {
    transform: scale(1.7);
  }
  .swiper-slide-prev {
    transform: scale(1.2);
  }
  .swiper-slide-next {
    transform: scale(1.2);
  }
  .selected-emoji {
    display: flex;
    align-items: center;
    & > div {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-grow: 1;
    }
    & > svg {
      transition: all 0.1s;
      cursor: pointer;
      &:hover {
        scale: 1.1;
        filter: brightness(1.2);
      }
      &:active {
        scale: 0.9;
      }
    }
  }
`;

const StaticEmojiIcon = styled.div`
  width: 24px;
  height: 24px;
  background-size: 100%;
  transition: all 0.3s;
  &:hover {
    scale: 1.3;
  }
`;

const SelectedEmojiIcon = styled.div`
  animation: floatingUp 0.3s;
  width: 120px;
  height: 120px;
  background-size: 100%;
`;
