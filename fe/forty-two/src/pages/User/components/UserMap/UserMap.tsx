import { NaverDynamicMap } from "../../../../components";
import { userLocationUpdateState } from "../../../../recoil/location/selectors";
import { useState, useEffect } from "react";
import { Marker, useNavermaps } from "react-naver-maps";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

type userMapProps = { userData: TUserDetail | undefined };

function UserMap({ userData }: userMapProps) {
  const location = useRecoilValue<TLocation | null>(userLocationUpdateState);
  const [center, setCenter] = useState();
  const [isMapLoad, setIsMapLoad] = useState<boolean>(false);
  const navermaps = useNavermaps();
  const [markers, setMarkers] = useState<JSX.Element[] | []>([]);

  useEffect(() => {
    if (location) {
      setCenter(new navermaps.LatLng(location?.latitude, location?.longitude));
      setIsMapLoad(true);
    }
  }, [location]);

  useEffect(() => {
    if (userData) {
      setMarkers(
        userData.placeResDtos.map((data, idx) => (
          <Marker
            key={`user-data-${idx}`}
            position={
              new navermaps.LatLng(data.placeLatitude, data.placeLongitude)
            }
            icon={{
              url: `https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/static/${userData.emoji}.png`,
              scaledSize: new navermaps.Size(40, 40),
              origin: new navermaps.Point(0, 0),
              anchor: new navermaps.Point(20, 20),
            }}
          />
        ))
      );
    }
  }, [userData]);

  return (
    <StyledUserMap>
      {isMapLoad && center ? (
        <NaverDynamicMap children={markers} center={center}></NaverDynamicMap>
      ) : null}
    </StyledUserMap>
  );
}

export default UserMap;

const StyledUserMap = styled.div`
  width: 100%;
  height: 100%;
`;
