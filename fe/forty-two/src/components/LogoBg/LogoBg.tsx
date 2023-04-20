import styled from "styled-components";
import logoBgBlue from "../../assets/images/background/signUpBgBlue.png";
import logoBgLight from "../../assets/images/background/signUpBgLight.png";
import logoBgDark from "../../assets/images/background/signUpBgDark.png";

type logoBgProps = { isBlue: boolean };

function logoBg({ isBlue }: logoBgProps) {
  return <StyledLogoBg isBlue={isBlue} />;
}

export default logoBg;

const StyledLogoBg = styled.div<logoBgProps>`
  z-index: -99;
  position: fixed;
  top: 0px;
  left: 0px;
  width: 100vw;
  height: 100vh;
  background: center url(${({ isBlue }) => (isBlue ? logoBgBlue : logoBgLight)});
  background-size: cover;
`;
