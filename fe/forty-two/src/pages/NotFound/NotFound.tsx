import { LogoBg } from "../../components";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import styled from "styled-components";

type notFoundProps = {};

function NotFound({}: notFoundProps) {
  const navigate = useNavigate();
  const [countdown, setCountdown] = useState(5);

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prevCountdown) => prevCountdown - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    if (countdown === 0) {
      navigate("/");
    }
  }, [countdown, navigate]);

  return (
    <StyledNotFound>
      <h1>페이지를 찾지 못했습니다</h1>
      <h2>{countdown}초 후 홈으로 이동합니다.</h2>
      <LogoBg isBlue={false} />
    </StyledNotFound>
  );
}

export default NotFound;

const StyledNotFound = styled.main`
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  ${({ theme }) => theme.text.header2}
  & > h2 {
    ${({ theme }) => theme.text.header6}
  }
`;
