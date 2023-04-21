import { useNavigate } from "react-router";
import styled from "styled-components";

type SocialLoginBtnProps = {
  logoImg: string;
  bgColor: string;
  textColor: string;
  label: string;
  onClick(e: React.MouseEvent): void;
};

function SocialLoginBtn({
  logoImg,
  bgColor,
  textColor,
  label,
  onClick,
}: SocialLoginBtnProps) {
  let navigate = useNavigate();

  return (
    <StyledSocialLoginBtn
      // onClick={onClick}
      onClick={() => navigate("/signup")}
      bgColor={bgColor}
      textColor={textColor}
    >
      <img src={logoImg}></img>
      <p>{label}</p>
    </StyledSocialLoginBtn>
  );
}

export default SocialLoginBtn;

const StyledSocialLoginBtn = styled.button<{
  bgColor: string;
  textColor: string;
}>`
  display: flex;
  justify-content: start;
  align-items: center;
  width: 100%;
  max-width: 320px;
  height: 40px;
  padding-inline: 16px;
  margin-block: 8px;
  margin-inline: 24px;
  box-sizing: border-box;
  color: ${({ textColor }) => textColor};
  background-color: ${({ bgColor }) => bgColor};
  border: none;
  border-radius: 8px;
  box-shadow: 0px 0px 3px rgba(0, 0, 0, 0.084), 0px 2px 3px rgba(0, 0, 0, 0.168);
  cursor: pointer;
  &:active {
    filter: brightness(0.95);
    scale: 0.99;
  }

  & > p {
    flex-grow: 1;
    margin-left: 8px;
    ${({ theme }) => theme.text.button}
    color: ${({ textColor }) => textColor};
  }
`;
