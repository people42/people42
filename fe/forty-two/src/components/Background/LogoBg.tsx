import logoBgBlue from "../../assets/images/background/signUpBgBlue.png";
import logoBgDark from "../../assets/images/background/signUpBgDark.png";
import logoBgLight from "../../assets/images/background/signUpBgLight.png";
import styled from "styled-components";

type logoBgProps = { isBlue: boolean };

function LogoBg({ isBlue }: logoBgProps) {
  return <StyledLogoBg isBlue={isBlue} />;
}

export default LogoBg;

const StyledLogoBg = styled.div<{ isBlue: boolean }>`
  z-index: -99;
  position: fixed;
  top: 0px;
  left: 0px;
  width: 100vw;
  height: 100vh;
  background: center
    url(${(props) =>
      props.isBlue
        ? logoBgBlue
        : props.theme.isDark
        ? logoBgDark
        : logoBgLight});
  background-size: cover;
`;
