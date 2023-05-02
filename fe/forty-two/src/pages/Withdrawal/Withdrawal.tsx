import {
  getAccessToken,
  postWithdrawalApple,
  postWithdrawalGoogle,
} from "../../api";
import Spinner from "../../components/Spinner/Spinner";
import {
  userAccessTokenState,
  userLogoutState,
} from "../../recoil/user/selectors";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router";
import { useSearchParams } from "react-router-dom";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

type withdrawalProps = {};

function Withdrawal({}: withdrawalProps) {
  const accessToken = useRecoilValue(userAccessTokenState);
  const navigate = useNavigate();
  const [user, userLogout] = useRecoilState(userLogoutState);
  const params = useParams();
  const [searchParams, setSeratchParams] = useSearchParams();

  useEffect(() => {
    withdrawal(params.platform);
  }, []);

  const withdrawal = (platform: string | undefined) => {
    switch (platform) {
      case "google":
        withdrawalGoogle();
        break;
      case "apple":
        withdrawalApple();
        break;
      default:
        userLogout(user);
        alert("문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        navigate("/signin");
        break;
    }
  };

  const withdrawalGoogle = () => {
    if (accessToken) {
      postWithdrawalGoogle(accessToken)
        .then((res) => {
          userLogout(user);
          alert("정상적으로 탈퇴 되었습니다.");
          navigate("/signin");
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken()
              .then((res) =>
                postWithdrawalGoogle(res.data.data.accessToken).then((res) => {
                  userLogout(user);
                  alert("정상적으로 탈퇴 되었습니다.");
                  navigate("/signin");
                })
              )
              .catch((e) => {
                alert("회원 탈퇴 중 문제가 발생했습니다. 다시 시도해주세요.");
              });
          }
        });
    }
  };

  const withdrawalApple = () => {
    const appleCode = searchParams.get("code");
    console.log("asdfasdf", appleCode);
    if (appleCode) {
      if (accessToken) {
        postWithdrawalApple(accessToken, appleCode)
          .then((res) => {
            userLogout(user);
            alert("정상적으로 탈퇴 되었습니다.");
            navigate("/signin");
          })
          .catch((e) => {
            if (e.response.status == 401) {
              getAccessToken()
                .then((res) =>
                  postWithdrawalApple(
                    res.data.data.accessToken,
                    appleCode
                  ).then((res) => {
                    userLogout(user);
                    alert("정상적으로 탈퇴 되었습니다.");
                    navigate("/signin");
                  })
                )
                .catch((e) => {
                  alert("회원 탈퇴 중 문제가 발생했습니다. 다시 시도해주세요.");
                });
            }
          });
      }
    } else {
      getAppleSignInCode();
    }
  };

  const getAppleSignInCode = () => {
    const config = {
      client_id: "com.cider.fortytwo", // This is the service ID we created.
      redirect_uri: "https://people42.com/withdrawal/apple", // As registered along with our service ID
      response_type: "code",
      state: "origin:web", // Any string of your choice that you may use for some logic. It's optional and you may omit it.
      response_mode: "query",
      m: 11,
      v: "1.5.4",
    };

    const queryString = Object.entries(config)
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join("&");

    location.href = `https://appleid.apple.com/auth/authorize?${queryString}`;
  };

  return (
    <StyledWithdrawal>
      <Spinner></Spinner>
    </StyledWithdrawal>
  );
}

export default Withdrawal;

const StyledWithdrawal = styled.main`
  height: 100vh;
`;
