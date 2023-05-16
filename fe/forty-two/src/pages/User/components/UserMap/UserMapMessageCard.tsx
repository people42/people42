import { getAccessToken, postReport } from "../../../../api";
import { userState } from "../../../../recoil/user/atoms";
import { userAccessTokenState } from "../../../../recoil/user/selectors";
import { formatMessageDate, setSessionRefreshToken } from "../../../../utils";
import { useState, useEffect } from "react";
import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";

type userMapMessageCardProps = { data: TUserDetail["placeMessageInfo"] };

function UserMapMessageCard({ data }: userMapMessageCardProps) {
  const accessToken = useRecoilValue(userAccessTokenState);
  const setUserRefresh = useSetRecoilState(userState);
  const reportMessage = () => {
    if (accessToken) {
      postReport(accessToken, {
        messageIdx: data.messageIdx,
        content: reportContent,
      })
        .then((res) => {
          alert(
            `정상적으로 신고 완료되었습니다. \n \n * 신고내용: ${reportContent}`
          );
        })
        .catch((e) => {
          if (e.response.status == 401) {
            getAccessToken().then((res) => {
              postReport(res.data.data.accessToken, {
                messageIdx: data.messageIdx,
                content: reportContent,
              }).then((res) => {
                alert(
                  `정상적으로 신고 완료되었습니다. \n \n * 신고내용: ${reportContent}`
                );
              });
              setUserRefresh(res.data.data);
              setSessionRefreshToken(res.data.data.refreshToken);
            });
          }
        });
    }
  };

  const [reportContent, setReportContent] = useState<string>("");

  useEffect(() => {
    if (reportContent) {
      reportMessage();
    }
  }, [reportContent]);

  return (
    <StyledUserMapMessageCard>
      <p>{data.content}</p>
      <div>
        <p>{formatMessageDate(data.time)}</p>
        <span
          onClick={() => {
            if (confirm("메시지를 신고하시겠습니까?")) {
              const content = prompt("신고내용을 입력하세요");
              if (content) {
                setReportContent(content);
              }
            }
          }}
        >
          신고
        </span>
      </div>
    </StyledUserMapMessageCard>
  );
}

export default UserMapMessageCard;

const StyledUserMapMessageCard = styled.div`
  ${({ theme }) => theme.text.body2}
  padding: 8px 16px 8px 8px;
  margin-block: 8px;

  & > div {
    display: flex;
    justify-content: space-between;

    & > p {
      ${({ theme }) => theme.text.overline}
      color: ${({ theme }) => theme.color.text.secondary};
      padding-block: 8px;
    }

    & > span {
      flex-shrink: 0;
      background-color: ${({ theme }) => theme.color.background.secondary};
      cursor: pointer;
      &:hover {
        filter: ${({ theme }) =>
          theme.isDark == true ? "brightness(1.1)" : "brightness(0.95)"};
      }
      border-radius: 16px;
      padding: 8px;
      ${({ theme }) => theme.text.overline}
      color: ${({ theme }) => theme.color.brand.red};
    }
  }
`;
