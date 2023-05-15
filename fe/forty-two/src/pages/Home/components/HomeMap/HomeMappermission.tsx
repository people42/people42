import { TbLocationBroken } from "react-icons/tb";
import styled from "styled-components";

type homeMapPermissionProps = {};

function HomeMapPermission({}: homeMapPermissionProps) {
  return (
    <StyledHomeMapPermission>
      <TbLocationBroken size={64} />
      <p>위치를 불러올 수 없습니다</p>
      <p>{'좌상단 자물쇠 아이콘(🔒︎) > 사이트 설정 > 권한 > 위치 "허용"'}</p>
    </StyledHomeMapPermission>
  );
}

export default HomeMapPermission;

const StyledHomeMapPermission = styled.div`
  z-index: 5;
  width: 100%;
  height: 100%;
  position: absolute;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  ${({ theme }) => theme.text.header6}
  & > svg {
    margin-bottom: 24px;
    color: ${({ theme }) => theme.color.brand.red};
  }
  & > p:last-child {
    ${({ theme }) => theme.text.subtitle2}
    color: ${({ theme }) => theme.color.text.secondary};
  }
`;
