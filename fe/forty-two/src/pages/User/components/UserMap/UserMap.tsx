import { getAccessToken, getUserPlace } from "../../../../api";
import { NaverDynamicMap } from "../../../../components";
import { userLocationUpdateState } from "../../../../recoil/location/selectors";
import { userState } from "../../../../recoil/user/atoms";
import { userAccessTokenState } from "../../../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../../../utils";
import { useState, useEffect } from "react";
import { Marker, useNavermaps } from "react-naver-maps";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type userMapProps = { userData: TUserDetail | undefined };

function UserMap({ userData }: userMapProps) {
  const location = useRecoilValue<TLocation | null>(userLocationUpdateState);
  const [center, setCenter] = useState();
  const [isMapLoad, setIsMapLoad] = useState<boolean>(false);
  const navermaps = useNavermaps();
  const [markers, setMarkers] = useState<JSX.Element[] | []>([]);

  const boundList: any[] = [];
  const [bound, setBound] = useState<any>();

  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);
  const getUserPlaceList = (placeIdx: number) => {
    if (userData) {
      const params = { placeIdx: placeIdx, userIdx: userData.userIdx };
      getUserPlace(accessToken, params)
        .then((res) => console.log(res))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getUserPlace(res.data.data.accessToken, params).then((res) =>
                console.log(res.data.data)
              );
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
    }
  };

  useEffect(() => {
    if (userData) {
      setMarkers(
        userData.placeResDtos.map((data, idx) => {
          setCenter(
            new navermaps.LatLng(data.placeLatitude, data.placeLongitude)
          );
          const latlng = new navermaps.LatLng(
            data.placeLatitude,
            data.placeLongitude
          );
          boundList.push(latlng);
          return (
            <Marker
              key={`user-data-${idx}`}
              position={latlng}
              icon={{
                url: `https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/static/${userData.emoji}.png`,
                scaledSize: new navermaps.Size(40, 40),
                origin: new navermaps.Point(0, 0),
                anchor: new navermaps.Point(20, 20),
              }}
              onClick={(e) => getUserPlaceList(data.placeIdx)}
            />
          );
        })
      );
      if (userData.placeResDtos.length > 1) {
        setBound(new navermaps.LatLngBounds.bounds(...boundList));
      }
      setIsMapLoad(true);
    }
  }, [userData]);

  return (
    <StyledUserMap>
      {isMapLoad && center ? (
        <NaverDynamicMap
          children={markers}
          center={center}
          bound={bound}
        ></NaverDynamicMap>
      ) : null}
    </StyledUserMap>
  );
}

export default UserMap;

const StyledUserMap = styled.div`
  width: 100%;
  height: 100%;
`;
