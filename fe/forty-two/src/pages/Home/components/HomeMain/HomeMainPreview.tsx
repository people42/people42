import { getMessageCnt } from "../../../../api";
import { CommonBtn } from "../../../../components";
import { socketAllMessageCntState } from "../../../../recoil/socket/atoms";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type homeMainPreviewProps = {};

function HomeMainPreview({}: homeMainPreviewProps) {
  const navigate = useNavigate();
  const [messageCnt, setMessageCnt] = useRecoilState(socketAllMessageCntState);
  useEffect(() => {
    getMessageCnt().then((res) => setMessageCnt(res.data.data.cnt));
  }, []);

  return (
    <StyledHomeMainPreview>
      <h1>어쩌면 마주친 사이</h1>
      <h2>나도 모르게 스쳐간 인연과</h2>
      <h2>익명으로 생각을 공유해보세요.</h2>
      <h3>지금까지 {messageCnt}개의 생각이 익명으로 공유되었습니다.</h3>
      <div>
        <CommonBtn onClick={() => navigate("/signin")} btnType="primary">
          로그인해서 확인하기
        </CommonBtn>
      </div>
    </StyledHomeMainPreview>
  );
}

export default HomeMainPreview;

const StyledHomeMainPreview = styled.div`
  padding: 36px;
  width: 100%;
  white-space: nowrap;

  & > h1 {
    animation: floatingUp 0.3s both;
    animation-delay: 0.2s;
    margin-block: 16px;
    ${({ theme }) => theme.text.header3}
  }
  & > h2 {
    animation: floatingUp 0.3s both;
    animation-delay: 0.4s;
    ${({ theme }) => theme.text.header5}
  }
  & > h3 {
    animation: floatingUp 0.3s both;
    animation-delay: 0.6s;
    margin-block: 24px;
    color: ${({ theme }) => theme.color.brand.blue};
    ${({ theme }) => theme.text.header6}
  }
  & > div {
    animation: floatingUp 0.3s both;
    animation-delay: 0.8s;
    margin-block: 8px;
  }
`;
