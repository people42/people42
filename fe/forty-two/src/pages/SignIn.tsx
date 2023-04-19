import styled from "styled-components";
import signUpBgBlue from "../assets/images/background/signUpBgBlue.png";

function SignIn() {
  return <StyledSignInBg />;
}

export default SignIn;

const StyledSignInBg = styled.div`
  width: 100vw;
  height: 100vh;
  background: center url(${signUpBgBlue});
  background-size: cover;
`;
