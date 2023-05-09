import { getAccessToken, getPlace } from "../../api";
import { NavBar } from "../../components";
import { placeState } from "../../recoil/place/atoms";
import { userState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import { formatMessageDate, setSessionRefreshToken } from "../../utils";
import { PlaceMap, PlaceMessageList } from "./components";
import React, { useEffect, useState } from "react";
import { IoMdArrowBack } from "react-icons/io";
import {
  TbArrowBigDown,
  TbArrowBigUp,
  TbArrowDownCircle,
  TbArrowUp,
  TbArrowUpCircle,
} from "react-icons/tb";
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

  const [page, setPage] = useState<number>(0);

  const [placeData, setPlaceData] = useRecoilState(placeState);
  useEffect(() => {
    setPlaceData(null);
    if (accessToken && placeInfo) {
      getPlace(accessToken, { page: page, ...placeInfo })
        .then((res) => {
          if (res.data.data.messagesInfo.length > 0) {
            setPlaceData(res.data.data);
          } else {
            alert("마지막 페이지입니다.");
            setPage(page - 1);
          }
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getPlace(res.data.data.accessToken, {
                page: page,
                ...placeInfo,
              }).then((res) => setPlaceData(res.data.data));
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
    } else {
      navigate("/");
    }
  }, [page]);

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
          <IoMdArrowBack size={30} />
          {placeData ? (
            <div>
              <h1>{placeData?.placeWithTimeAndGpsInfo.placeName} 근처</h1>
              <p>
                {formatMessageDate(placeData?.placeWithTimeAndGpsInfo.time)}
              </p>
            </div>
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
            {page > 0 && placeData ? (
              <div className="list-page" onClick={() => setPage(page - 1)}>
                <TbArrowBigUp></TbArrowBigUp> 이전 메시지
              </div>
            ) : null}
            <PlaceMessageList></PlaceMessageList>
            {placeData ? (
              <div className="list-page" onClick={() => setPage(page + 1)}>
                <TbArrowBigDown></TbArrowBigDown> 더 많은 메시지
              </div>
            ) : null}
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
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;

  .place-title {
    cursor: pointer;
    ${({ theme }) => theme.text.header6}
    display: flex;
    align-items: start;
    margin-bottom: 16px;
    padding: 8px;
    svg {
      margin-right: 8px;
    }
    p {
      ${({ theme }) => theme.text.subtitle2}
      color: ${({ theme }) => theme.color.text.secondary};
    }
  }
  .place {
    width: 100%;
    max-width: 1024px;
    padding: 36px;
    display: flex;
    flex-direction: column;
    align-items: start;
    flex-grow: 1;

    &-body {
      width: 100%;
      max-width: 1024px;
      display: flex;
      margin-bottom: 24px;
      position: relative;

      &-list {
        width: 380px;
        flex-shrink: 0;
      }
      &-map {
        flex-grow: 1;
        display: flex;
        justify-content: center;
      }
    }
  }

  .list-page {
    cursor: pointer;
    margin-top: 16px;
    width: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
    ${({ theme }) => theme.text.subtitle2}
    color: ${({ theme }) => theme.color.text.primary};
    padding: 8px;

    & > svg {
      margin-right: 4px;
    }
  }
`;
