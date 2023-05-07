import { TbLocationBroken } from "react-icons/tb";
import styled from "styled-components";

type homeMapPermissionProps = {};

function HomeMapPermission({}: homeMapPermissionProps) {
  return (
    <StyledHomeMapPermission>
      <TbLocationBroken size={64} />
      <p>위치를 불러올 수 없습니다</p>
      <p>위치 권한을 허용해주세요</p>
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
