import "swiper/css";
import styled from "styled-components";
import { CommonBtn } from "../../../../components";
import { Swiper, SwiperSlide } from "swiper/react";
import { Pagination } from "swiper";

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
  const staticEmojiList: any[] = emojiList.map((name) => {
    return (
      <SwiperSlide>
        <img
          className="staticEmoji"
          src={`src/assets/images/emoji/${name}.png`}
          alt={"emoji"}
        ></img>
      </SwiperSlide>
    );
  });

  return (
    <StyledEmojiSelector>
      <div>
        <Swiper
          slidesPerView={5}
          spaceBetween={30}
          loop={true}
          centeredSlides={true}
          pagination={{
            clickable: true,
          }}
          modules={[Pagination]}
          className="emojiSwiper"
        >
          {staticEmojiList}
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
    & > div > img {
      scale: 0.9;
    }
  }
`;
