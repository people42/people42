import styled from "styled-components";

type placeMapEmojiMarkerProps = { emoji: string; idx: number };

function PlaceMapEmojiMarker({ emoji, idx }: placeMapEmojiMarkerProps) {
  const S3_URL = import.meta.env.VITE_S3_URL;

  return (
    <StyledPlaceMapEmojiMarker
      style={{
        right: `${(idx * 12341) % 100}%`,
        bottom: `${(idx * 21432) % 100}%`,
      }}
    >
      <div
        className="emoji"
        style={{
          animation: `popIn 0.3s both`,
          animationDelay: `${0.1 * idx}s`,
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
