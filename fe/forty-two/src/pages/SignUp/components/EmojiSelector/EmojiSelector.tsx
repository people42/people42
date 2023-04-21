import "swiper/css";
import styled from "styled-components";
import { CommonBtn } from "../../../../components";
import { Swiper, SwiperRef, SwiperSlide } from "swiper/react";
import "swiper/css/pagination";
import "swiper/css/navigation";
import { useEffect, useState } from "react";
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
        <StaticEmojiIcon name={name}></StaticEmojiIcon>
      </SwiperSlide>
    );
  });
  let swiper: any;
  let swiperMethod: any;
  useEffect(() => {
    swiper = document.querySelector(".swiper");
    swiperMethod = swiper.swiper;
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
        <div>
          <button
            onClick={() => {
              swiperMethod.slidePrev();
              handleSlideChange();
            }}
          ></button>
          <SelectedEmojiIcon name={activeEmojiName}></SelectedEmojiIcon>
          <button
            onClick={() => {
              swiperMethod.slideNext();
              handleSlideChange();
            }}
          ></button>
        </div>
        <Swiper
          onSlideChange={handleSlideChange}
          allowSlideNext={true}
          allowSlidePrev={true}
          slidesPerView={7}
          loop={true}
          centeredSlides={true}
          className="emojiSwiper"
          grabCursor={true}
        >
          {staticEmojiSlideList}
        </Swiper>
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
  }
  .swiper {
    padding-block: 16px;
    height: 48px;
  }
  .swiper-slide {
    display: flex;
    justify-content: center;
    height: 48px;
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
`;

const StaticEmojiIcon = styled.div<{ name: string }>`
  width: 36px;
  height: 36px;
  background-image: url(${({ name }) =>
    `"src/assets/images/emoji/${name}.png"`});
  background-size: 100%;
`;

const SelectedEmojiIcon = styled.div<{ name: string }>`
  width: 120px;
  height: 120px;
  background-image: url(${({ name }) =>
    `"src/assets/images/emoji/${name}.png"`});
  background-size: 100%;
`;
