import { getAccessToken, getUser, postBlock } from "../../api";
import { CommonBtn, NavBar } from "../../components";
import { userState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import { UserMap } from "./components";
import { useEffect, useState } from "react";
import { IoMdArrowBack } from "react-icons/io";
import Skeleton from "react-loading-skeleton";
import { useNavigate, useParams } from "react-router";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

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
            });
          }
        });
    }
  }, [params]);

  const userBlock = () => {
    if (accessToken && userData?.userIdx) {
      postBlock(accessToken, userData?.userIdx)
        .then((res) => {
          alert(`${userData?.nickname}님을 차단했습니다.`);
          navigate("/");
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              postBlock(res.data.data.accessToken, userData?.userIdx).then(
                (res) => {
                  alert(`${userData?.nickname}님을 차단했습니다.`);
                  navigate("/");
                }
              );
              setUserRefresh(res.data.data);
            });
          }
        });
    }
  };

  const S3_URL = import.meta.env.VITE_S3_URL;

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
          <IoMdArrowBack size={30} />
          {userData ? (
            <>
              <div
                className="emoji"
                style={{
                  backgroundImage: `url("${S3_URL}emoji/animate/${userData.emoji}.gif")`,
                }}
              ></div>
              <h1>
                {userData?.brushCnt}번 스친 {userData?.nickname}
              </h1>
            </>
          ) : (
            <Skeleton
              baseColor="#86868626"
              highlightColor="#8686863c"
              width={210}
              height={24}
            ></Skeleton>
          )}
        </div>
        <CommonBtn onClick={() => userBlock()} btnType="primary">
          차단
        </CommonBtn>
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
    align-items: center;
    justify-content: space-between;
    &-title {
      cursor: pointer;
      ${({ theme }) => theme.text.header6}
      display: flex;
      align-items: center;
      padding: 8px;
      svg {
        margin-right: 8px;
      }
    }
    &-map {
      width: 100%;
      flex-grow: 1;
    }
  }
  .emoji {
    width: 32px;
    height: 32px;
    background-size: cover;
    margin-right: 8px;
  }
`;
