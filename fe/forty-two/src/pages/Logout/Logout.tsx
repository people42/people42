import { deleteLogout, getAccessToken } from "../../api";
import Spinner from "../../components/Spinner/Spinner";
import { isLoginState, userState } from "../../recoil/user/atoms";
import {
  userAccessTokenState,
  userLogoutState,
} from "../../recoil/user/selectors";
import { useEffect } from "react";
import { useNavigate } from "react-router";
import { useRecoilState, useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type logoutProps = {};

function Logout({}: logoutProps) {
  const accessToken = useRecoilValue(userAccessTokenState);
  const navigate = useNavigate();
  const [user, userLogout] = useRecoilState(userLogoutState);
  const setIsLogin = useSetRecoilState(isLoginState);
  useEffect(() => {
    logout();
  }, []);

  const logout = () => {
    if (accessToken) {
      deleteLogout(accessToken)
        .then((res) => {
          removeToken();
          setIsLogin(false);
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              deleteLogout(res.data.data.accessToken).then(() => {
                removeToken();
                setIsLogin(false);
              });
            });
          }
        });
    } else {
      navigate("/signin");
    }
  };

  const removeToken = () => {
    userLogout(user);
    localStorage.removeItem("user_platform");
    alert("안전하게 로그아웃 되었습니다.");
    navigate("/signin");
  };

  return (
    <StyledLogout>
      <Spinner></Spinner>
    </StyledLogout>
  );
}

export default Logout;

const StyledLogout = styled.main`
  height: 100vh;
`;
