import styled from "styled-components";

type placeMapEmojiMarkerProps = { emoji: string };

function PlaceMapEmojiMarker({ emoji }: placeMapEmojiMarkerProps) {
  return (
    <StyledPlaceMapEmojiMarker
      style={{
        right: `${Math.floor(Math.random() * 101)}%`,
        bottom: `${Math.floor(Math.random() * 101)}%`,
      }}
    >
      <div
        className="emoji"
        style={{
          width: 40,
          height: 40,
          backgroundSize: "cover",
          backgroundImage: `url("https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/animate/${emoji}.gif")`,
        }}
      ></div>
    </StyledPlaceMapEmojiMarker>
  );
}

export default PlaceMapEmojiMarker;

const StyledPlaceMapEmojiMarker = styled.div`
  z-index: 5;
  z-index: 20;
  width: 40px;
  height: 40px;
  position: absolute;
  .emoji {
    z-index: 100;
    cursor: pointer;
  }
`;
