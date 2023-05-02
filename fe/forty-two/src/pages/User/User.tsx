import { getAccessToken, getUser } from "../../api";
import { NavBar, NaverDynamicMap } from "../../components";
import { userState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../utils";
import { useEffect, useState } from "react";
import { IoMdArrowBack } from "react-icons/io";
import Skeleton from "react-loading-skeleton";
import { Marker } from "react-naver-maps";
import { useNavigate, useParams } from "react-router";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";
import { UserMap } from "./components";

function User() {
  const navigate = useNavigate();
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);
  const [userData, setUserData] = useState<TUserDetail>();
  const params = useParams();

  useEffect(() => {
    if (accessToken && params.user_id) {
      getUser(accessToken, { userIdx: parseInt(params.user_id) })
        .then((res) => setUserData(res.data.data))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              params.user_id
                ? getUser(res.data.data.accessToken, {
                    userIdx: parseInt(params.user_id),
                  }).then((res) => setUserData(res.data.data))
                : null;
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
    }
  }, [params]);

  return (
    <StyledUser>
      <NavBar></NavBar>
      <div className="user">
        <div
          className="user-title"
          onClick={() => {
            navigate(-1);
          }}
        >
          <IoMdArrowBack size={24} />
          {userData ? (
            <h1>{userData?.nickname}</h1>
          ) : (
            <Skeleton
              baseColor="#86868626"
              highlightColor="#8686863c"
              width={210}
              height={24}
            ></Skeleton>
          )}
        </div>
      </div>
      <div className="user-map">
        <UserMap userData={userData}></UserMap>
      </div>
    </StyledUser>
  );
}

export default User;

const StyledUser = styled.main`
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  .user {
    width: 100%;
    max-width: 1024px;
    padding: 36px 36px 16px 36px;
    display: flex;
    flex-direction: column;
    align-items: start;
    &-title {
      cursor: pointer;
      ${({ theme }) => theme.text.header6}
      display: flex;
      align-items: center;
    }
    &-map {
      width: 100%;
      flex-grow: 1;
    }
  }
`;
