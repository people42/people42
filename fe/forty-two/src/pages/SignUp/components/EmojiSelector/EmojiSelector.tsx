import { CommonBtn } from "../../../../components";
import { MouseEventHandler, useEffect, useState } from "react";
import { TbArrowBigRightFilled, TbArrowBigLeftFilled } from "react-icons/tb";
import styled from "styled-components";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import { Swiper, SwiperRef, SwiperSlide } from "swiper/react";
import { SwiperEvents } from "swiper/types";

type emojiSelectorProps = { onClick(e: React.MouseEvent): void };

function EmojiSelector({ onClick }: emojiSelectorProps) {
  let emojiList: string[] = [
    "alien",
    "angry-face",
    "anguished-face",
    "anxious-face-with-sweat",
    "beaming-face-with-smiling-eyes",
    "cat-with-tears-of-joy",
    "cat-with-wry-smile",
    "clown-face",
    "cold-face",
    "confounded-face",
    "confused-face",
    "cowboy-hat-face",
    "crying-cat",
    "crying-face",
    "disappointed-face",
    "disguised-face",
    "dizzy-face",
    "downcast-face-with-sweat",
    "drooling-face",
    "exploding-head",
    "face-blowing-a-kiss",
    "face-exhaling",
  ];
  const staticEmojiSlideList: any[] = emojiList.map((name) => {
    return (
      <SwiperSlide key={name} id={name}>
        <StaticEmojiIcon
          style={{
            backgroundImage: `url("src/assets/images/emoji/static/${name}.png")`,
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
  const [activeEmojiName, setActiveEmojiName] = useState("");
  const handleSlideChange = (swiper: any) => {
    setActiveEmojiIndex(swiper.activeIndex);
  };

  useEffect(() => {
    setActiveEmojiName(document.querySelector(".swiper-slide-active")!.id);
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
                backgroundImage: `url("src/assets/images/emoji/animate/${activeEmojiName}.gif")`,
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
