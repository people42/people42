import { useSearchParams } from "react-router-dom";
import styled from "styled-components";

type appleAccountCheckProps = {};

function AppleAccountCheck({}: appleAccountCheckProps) {
  const [searchParams, setSeratchParams] = useSearchParams();
  return (
    <StyledAppleAccountCheck>
      하필 애플로 로그인중입니다...ㅠ
      <button
        onClick={(e) => {
          console.log(window.parent);
          window.parent.postMessage(
            "Hello from child window!",
            "http://people42.com/signin"
          );
        }}
      >
        http://people42.com/signin
      </button>
      <button
        onClick={(e) => {
          console.log(window.parent);
          window.parent.postMessage(
            "Hello from child window!",
            "http://people42.com"
          );
        }}
      >
        http://people42.com
      </button>
      <button
        onClick={(e) => {
          console.log(searchParams.get("code"));
        }}
      >
        code
      </button>
    </StyledAppleAccountCheck>
  );
}

export default AppleAccountCheck;

const StyledAppleAccountCheck = styled.nav``;
