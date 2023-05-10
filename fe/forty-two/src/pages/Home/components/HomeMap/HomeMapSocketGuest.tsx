import styled from "styled-components";

type homeMapSocketGuestProps = {
  right: number;
  bottom: number;
  emoji: string;
};

function HomeMapSocketGuest({ right, bottom, emoji }: homeMapSocketGuestProps) {
  const S3_URL = import.meta.env.VITE_S3_URL;

  return (
    <StyledHomeMapSocketGuest
      style={{ right: `${right}%`, bottom: `${bottom}%` }}
    >
      <div
        className="home-map-near-user"
        style={{
          backgroundImage: `url("${S3_URL}emoji/animate/${emoji}.gif")`,
        }}
      >
        <div>Guest</div>
      </div>
    </StyledHomeMapSocketGuest>
  );
}

export default HomeMapSocketGuest;

const StyledHomeMapSocketGuest = styled.div`
  animation: popIn 0.3s;
  position: absolute;
  & > div {
    width: 40px;
    height: 40px;
    background-size: cover;
    & > div {
      position: absolute;
      left: 12px;
      bottom: -4px;
      padding-inline: 4px;
      ${({ theme }) => theme.text.overline}
      background-color: ${({ theme }) => theme.color.monotone.darkGray};
      border-radius: 24px;
      color: ${({ theme }) => theme.color.monotone.light};
    }
  }
`;
