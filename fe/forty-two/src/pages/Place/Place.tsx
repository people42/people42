import { getAccessToken, getPlace } from "../../api";
import { NavBar } from "../../components";
import { userState } from "../../recoil/user/atoms";
import { userAccessTokenState } from "../../recoil/user/selectors";
import { setSessionRefreshToken } from "../../utils";
import React, { useEffect } from "react";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

function Place() {
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);

  const param = { placeIdx: 1, time: "string", page: 0, size: 0 };

  useEffect(() => {
    if (accessToken)
      getPlace(accessToken, param)
        .then((res) => console.log(res))
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              getPlace(res.data.data.accessToken, param);
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
  }, []);

  return (
    <StyledPlace>
      <NavBar></NavBar>
    </StyledPlace>
  );
}

export default React.memo(Place);

const StyledPlace = styled.div``;
