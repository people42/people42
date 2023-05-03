import styled from "styled-components";

type placeMapEmojiMarkerProps = { emoji: string };

function PlaceMapEmojiMarker({ emoji }: placeMapEmojiMarkerProps) {
  const S3_URL = import.meta.env.VITE_S3_URL;

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
          backgroundImage: `url("${S3_URL}emoji/animate/${emoji}.gif")`,
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
