import styled from "styled-components";

type navModalSettingRowProps = {
  children: string;
  onClick(e: React.MouseEvent): void;
  color?: string;
};

function NavModalSettingRow({
  children,
  onClick,
  color,
}: navModalSettingRowProps) {
  return (
    <StyledNavModalSettingRow onClick={onClick} style={{ color: color }}>
      {children}
    </StyledNavModalSettingRow>
  );
}

export default NavModalSettingRow;

const StyledNavModalSettingRow = styled.li`
  height: 32px;
  padding-inline: 8px;
  cursor: pointer;
  ${({ theme }) => theme.text.subtitle2}
  display: flex;
  align-items: center;
  transition: all 0.3s;
  border-radius: 16px;
  &:hover {
    background-color: ${({ theme }) => theme.color.text.primary + "10"};
  }
`;
