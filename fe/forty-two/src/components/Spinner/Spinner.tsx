import { themeState } from "../../recoil/theme/atoms";
import ReactLoading from "react-loading";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type spinnerProps = {};

function Spinner({}: spinnerProps) {
  return (
    <StyledSpinner>
      <ReactLoading
        type={"spinningBubbles"}
        color={useRecoilValue(themeState) ? "#ffffff" : "#151515"}
        height={32}
        width={32}
      />
    </StyledSpinner>
  );
}

export default Spinner;

const StyledSpinner = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
`;
