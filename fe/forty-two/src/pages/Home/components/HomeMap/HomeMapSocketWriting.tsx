import styled from "styled-components";

type homeMapSocketWritingProps = {};

function HomeMapSocketWriting({}: homeMapSocketWritingProps) {
  return (
    <StyledHomeMapSocketWriting>
      <div className="socket-writing">
        <div className="socket-writing-circle"></div>
        <div className="socket-writing-circle writing-circle-1"></div>
        <div className="socket-writing-circle writing-circle-2"></div>
      </div>
    </StyledHomeMapSocketWriting>
  );
}

export default HomeMapSocketWriting;

const StyledHomeMapSocketWriting = styled.div`
  position: absolute;
  left: 16px;
  bottom: -8px;

  .socket-writing {
    animation: popIn 0.3s both;
    background-color: ${({ theme }) => theme.color.brand.blue + "c0"};
    border-radius: 24px;
    width: 48px;
    height: 24px;
    display: flex;
    justify-content: center;
    align-items: center;

    &-circle {
      animation: writingWave 2s infinite both linear;
      width: 8px;
      height: 8px;
      margin-inline: 2px;
      border-radius: 8px;
      background-color: ${({ theme }) => theme.color.monotone.light};
    }
  }
  .writing-circle-1 {
    animation-delay: 0.5s;
  }
  .writing-circle-2 {
    animation-delay: 1s;
  }
`;
