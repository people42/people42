import { getAccessToken, getUserPlace } from "../../../../api";
import { NaverDynamicMap } from "../../../../components";
import { userLocationUpdateState } from "../../../../recoil/location/selectors";
import { userState } from "../../../../recoil/user/atoms";
import { userAccessTokenState } from "../../../../recoil/user/selectors";
import UserMapMessageList from "./UserMapMessageList";
import { useState, useEffect } from "react";
import { Marker, useListener, useNavermaps } from "react-naver-maps";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type userMapProps = {
  userData: TUserDetail | undefined;
};

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
      setIsOpen(false);
      getUserPlace(accessToken, params)
        .then((res) => {
          setIsOpen(true);
          setMessagesInfo(res.data.data.messagesInfo);
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getUserPlace(res.data.data.accessToken, params).then((res) => {
                setIsOpen(true);
                setMessagesInfo(res.data.data.messagesInfo);
              });
              setUserRefresh(res.data.data);
            });
          }
        });
    }
  };

  const S3_URL = import.meta.env.VITE_S3_URL;

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
                url: `${S3_URL}emoji/static/${userData.emoji}.png`,
                scaledSize: new navermaps.Size(40, 40),
                origin: new navermaps.Point(0, 0),
                anchor: new navermaps.Point(20, 20),
              }}
              onClick={(e) => {
                setPlaceInfo(data);
                getUserPlaceList(data.placeIdx);
              }}
              onMouseover={(e) => {
                e.overlay.eventTarget.src = `${S3_URL}emoji/animate/${userData.emoji}.gif`;
                e.overlay.eventTarget.style.transition = "all 0.3s";
                e.overlay.eventTarget.style.transform = "scale(1.1)";
              }}
              onMouseout={(e) => {
                e.overlay.eventTarget.src = `${S3_URL}emoji/static/${userData.emoji}.png`;
                e.overlay.eventTarget.style.transition = "none";
                e.overlay.eventTarget.style.transform = "scale(1)";
              }}
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

  const [messagesInfo, setMessagesInfo] = useState<
    TUserDetail["placeMessageInfo"][] | null
  >(null);
  const [placeInfo, setPlaceInfo] = useState<
    TUserDetail["placeResDtos"][0] | null
  >(null);
  const [isOpen, setIsOpen] = useState<boolean>(false);

  return (
    <StyledUserMap>
      {isMapLoad && center ? (
        <NaverDynamicMap
          children={markers}
          center={center}
          bound={bound}
        ></NaverDynamicMap>
      ) : null}
      {isOpen && messagesInfo ? (
        <UserMapMessageList
          placeInfo={placeInfo}
          messagesInfo={messagesInfo}
          setIsOpen={setIsOpen}
        ></UserMapMessageList>
      ) : null}
    </StyledUserMap>
  );
}

export default UserMap;

const StyledUserMap = styled.div`
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
`;
