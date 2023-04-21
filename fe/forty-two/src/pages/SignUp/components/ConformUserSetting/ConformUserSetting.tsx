import styled from "styled-components";
import { CommonBtn } from "../../../../components";

type conformUserSettingProps = { onClick(e: React.MouseEvent): void };

function ConformUserSetting({ onClick }: conformUserSettingProps) {
  return (
    <StyledConformUserSetting>
      <div>asdf</div>
      <CommonBtn onClick={onClick} btnType="primary">
        확인
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
  }
`;
