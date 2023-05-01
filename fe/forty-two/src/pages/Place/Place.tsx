import { getAccessToken, getPlace } from "../../api";
import { NavBar } from "../../components";
import { placeState } from "../../recoil/place/atoms";
import { userState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../utils";
import { PlaceMap, PlaceMessageList } from "./components";
import React, { useEffect, useState } from "react";
import { IoMdArrowBack } from "react-icons/io";
import Skeleton from "react-loading-skeleton";
import { useLocation, useNavigate } from "react-router";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

function Place() {
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);

  const location = useLocation();
  const navigate = useNavigate();
  const placeInfo = location.state;

  const [placeData, setPlaceData] = useRecoilState(placeState);
  useEffect(() => {
    setPlaceData(null);
    if (accessToken && placeInfo)
      getPlace(accessToken, placeInfo)
        .then((res) => setPlaceData(res.data.data))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getPlace(res.data.data.accessToken, placeInfo).then((res) =>
                setPlaceData(res.data.data)
              );
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
  }, [placeInfo]);

  return (
    <StyledPlace>
      <NavBar></NavBar>
      <div className="place">
        <span
          className="place-title"
          onClick={() => {
            navigate(-1);
          }}
        >
          <IoMdArrowBack size={24} />
          {placeData ? (
            <h1>{placeData?.placeWithTimeAndGpsInfo.placeName} 근처</h1>
          ) : (
            <Skeleton
              baseColor="#86868626"
              highlightColor="#8686863c"
              width={320}
              height={32}
            ></Skeleton>
          )}
        </span>
        <div className="place-body">
          <article className="place-body-list">
            <PlaceMessageList></PlaceMessageList>
          </article>

          <div className="place-body-map">
            <PlaceMap></PlaceMap>
          </div>
        </div>
      </div>
    </StyledPlace>
  );
}

export default React.memo(Place);

const StyledPlace = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;

  .place-title {
    cursor: pointer;
    ${({ theme }) => theme.text.header6}
    display: flex;
    align-items: center;
    margin-bottom: 16px;
  }
  .place {
    width: 100%;
    max-width: 1024px;
    padding: 36px;
    display: flex;
    flex-direction: column;
    align-items: start;

    &-body {
      width: 100%;
      max-width: 1024px;
      display: flex;
      margin-bottom: 24px;
      &-list {
        width: 280px;
      }
      &-map {
        flex-grow: 1;
      }
    }
  }
`;
