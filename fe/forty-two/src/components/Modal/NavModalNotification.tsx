import NavModalNotificationRow from "./NavModalNotificationRow";
import styled from "styled-components";

type navModalNotificationProps = {};

function NavModalNotification({}: navModalNotificationProps) {
  return (
    <StyledNavModalNotification>
      <NavModalNotificationRow />
    </StyledNavModalNotification>
  );
}

export default NavModalNotification;

const StyledNavModalNotification = styled.div``;
