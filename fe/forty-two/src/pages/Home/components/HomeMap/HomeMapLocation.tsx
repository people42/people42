import Spinner from "../../../../components/Spinner/Spinner";
import { locationInfoState } from "../../../../recoil/location/atoms";
import { TbLocationFilled, TbLocationOff, TbLocation } from "react-icons/tb";
import { useNavigate } from "react-router";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type homeMapLocationProps = {};

function HomeMapLocation({}: homeMapLocationProps) {
  const locationInfo = useRecoilValue<TLocationInfo | null>(locationInfoState);
  const navigate = useNavigate();
  return (
    <StyledHomeMapLocation
      onClick={() => navigate("/xxx")}
      className="map-place-label"
    >
      {locationInfo?.placeName ? (
        <TbLocationFilled color="#1cb800" size={16} />
      ) : (
        <TbLocation color="gray" size={16} />
      )}

      {locationInfo?.placeName ? (
        <span>{locationInfo?.placeName} 근처</span>
      ) : (
        <span>
          <Spinner></Spinner>
        </span>
      )}
    </StyledHomeMapLocation>
  );
}

export default HomeMapLocation;

const StyledHomeMapLocation = styled.section`
  display: flex;
  align-items: center;
  z-index: 3;
  position: fixed;
  bottom: 24px;
  right: 24px;
  ${({ theme }) => theme.text.subtitle2};
  ${({ theme }) => theme.shadow.iconShadow};
  background-color: ${({ theme }) => theme.color.background.secondary};
  padding: 8px 16px;
  border-radius: 50px;
  & > svg {
    margin-right: 8px;
  }
`;
