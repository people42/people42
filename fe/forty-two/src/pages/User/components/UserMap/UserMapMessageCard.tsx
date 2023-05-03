import { Card } from "../../../../components";
import { formatMessageDate } from "../../../../utils";
import styled from "styled-components";

type userMapMessageCardProps = { data: TUserDetail["placeMessageInfo"] };

function UserMapMessageCard({ data }: userMapMessageCardProps) {
  return (
    <StyledUserMapMessageCard>
      <p>{data.content}</p>
      <p>{formatMessageDate(data.time)}</p>
    </StyledUserMapMessageCard>
  );
}

export default UserMapMessageCard;

const StyledUserMapMessageCard = styled.div`
  ${({ theme }) => theme.text.body2}

  border-radius: 16px;
  padding-block: 8px;
  margin-block: 8px;
  & > p:last-child {
    ${({ theme }) => theme.text.overline}
    color: ${({ theme }) => theme.color.text.secondary};
  }
`;
