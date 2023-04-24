import { CommonBtn } from "../../../../components";
import { signUpUserState } from "../../../../recoil/auth/atoms";
import { Link } from "react-router-dom";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type conformUserSettingProps = { onClick(e: React.MouseEvent): void };

function ConformUserSetting({ onClick }: conformUserSettingProps) {
  const signUpUser = useRecoilValue(signUpUserState);

  return (
    <StyledConformUserSetting>
      <div>
        <SelectedEmojiIcon
          style={{
            backgroundImage: `url("src/assets/images/emoji/animate/${signUpUser.emoji}.gif")`,
          }}
        ></SelectedEmojiIcon>
        <p>{signUpUser.nickname}</p>
      </div>
      <Link to={"/"}>
        <CommonBtn onClick={() => {}} btnType="primary">
          이 프로필로 시작하기
        </CommonBtn>
      </Link>
      <CommonBtn onClick={onClick} btnType="secondary">
        다시 선택할래요
      </CommonBtn>
    </StyledConformUserSetting>
  );
}

export default ConformUserSetting;

const StyledConformUserSetting = styled.div`
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-grow: 1;
  & > div {
    flex-grow: 1;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    & > p {
      ${({ theme }) => theme.text.header6}
      margin-top: 8px;
      margin-bottom: 16px;
    }
  }
`;

const SelectedEmojiIcon = styled.div`
  animation: floatingUp 0.3s;
  width: 120px;
  height: 120px;
  background-size: 100%;
`;
