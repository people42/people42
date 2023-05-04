import styled from "styled-components";

type navModalNotificationRowProps = {};

function NavModalNotificationRow({}: navModalNotificationRowProps) {
  const S3_URL = import.meta.env.VITE_S3_URL;
  return (
    <StyledNavModalNotificationRow>
      <img src={`${S3_URL}emoji/animate/${"ghost"}.gif`}></img>
      <div>
        <span>0분 전</span>
        <span>누군가 회원님의 생각을 좋아해요</span>
        <p>노티 내용</p>
      </div>
    </StyledNavModalNotificationRow>
  );
}

export default NavModalNotificationRow;

const StyledNavModalNotificationRow = styled.div`
  padding-block: 8px;
  & > img {
    width: 40px;
    height: 40px;
    margin: 4px;
    margin-right: 8px;
  }
  display: flex;
  ${({ theme }) => theme.text.body2}
  & > div {
    & > span {
      ${({ theme }) => theme.text.overline}
      &:nth-child(1) {
        color: ${({ theme }) => theme.color.text.secondary};
        margin-right: 8px;
      }
    }
    & > p {
      margin-top: 4px;
    }
  }
`;
