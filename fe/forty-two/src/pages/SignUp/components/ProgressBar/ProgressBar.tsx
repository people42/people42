import styled from "styled-components";

function ProgressBar({ step }: { step: number }) {
  return (
    <StyledProgressBar step={step}>
      <div className="progress-first">
        <p>1. 닉네임 설정</p>
      </div>
      <div className="progress-second">
        <p>2. 프로필 이모지</p>
      </div>
      <div className="progress-third">
        <p>3. 최종 확인</p>
      </div>
    </StyledProgressBar>
  );
}

export default ProgressBar;

const StyledProgressBar = styled.div<{ step: number }>`
  display: flex;
  height: 10px;
  margin-bottom: 40px;
  transition: all 0.3s;
  & > div {
    margin: 4px;
    flex-grow: 1;
    height: 4px;
    border-radius: 2px;
    & > p {
      margin-top: 8px;
      ${({ theme }) => theme.text.overline}
    }
  }
  & .progress-first {
    background-color: ${(props) =>
      props.step >= 1
        ? props.theme.color.brand.blue
        : props.theme.color.text.secondary};
    & > p {
      color: ${(props) =>
        props.step >= 1
          ? props.theme.color.brand.blue
          : props.theme.color.text.secondary};
    }
  }
  & .progress-second {
    background-color: ${(props) =>
      props.step >= 2
        ? props.theme.color.brand.blue
        : props.theme.color.text.secondary};
    & > p {
      color: ${(props) =>
        props.step >= 2
          ? props.theme.color.brand.blue
          : props.theme.color.text.secondary};
    }
  }
  & .progress-third {
    background-color: ${(props) =>
      props.step >= 3
        ? props.theme.color.brand.blue
        : props.theme.color.text.secondary};
    & > p {
      color: ${(props) =>
        props.step >= 3
          ? props.theme.color.brand.blue
          : props.theme.color.text.secondary};
    }
  }
`;
