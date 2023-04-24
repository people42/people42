import { CommonBtn, FloatIconBtn } from "../../../../components/index";
import { signUpUserState } from "../../../../recoil/auth/atoms";
import RandomNicknameCard from "./RandomNicknameCard";
import { useEffect, useState } from "react";
import { TbReload } from "react-icons/tb";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type nicknamePickerProps = {
  onClick(e: React.MouseEvent): void;
};

function NicknamePicker({ onClick }: nicknamePickerProps) {
  const [signUpUser, setSignUpUser] =
    useRecoilState<TSignUpUser>(signUpUserState);

  const [userNickname, setUserNickname] = useState<{
    aword: string | null;
    nword: string | null;
  }>({ aword: null, nword: null });

  const getNewNickname = () => {
    if (!randomWordAnimation) {
      setRandomWordAnimation(true);
      setTimeout(() => {
        setUserNickname({ aword: "안멋진", nword: "사자" });
        setRandomWordAnimation(false);
      }, 50);
    }
  };

  useEffect(() => {
    setUserNickname({ aword: "멋진", nword: "호랑이" });
  }, []);

  useEffect(() => {
    if (userNickname.aword && userNickname.nword) {
      setRandomWordAList([
        "멍멍 짖는",
        "야옹 우는",
        "멍멍 짖는",
        "야옹 우는",
        userNickname.aword,
        "야옹 우는",
        "멍멍 짖는",
        "야옹 우는",
        "멍멍 짖는",
      ]);
      setRandomWordNList([
        "고양이",
        "강아지",
        "고양이",
        "강아지",
        userNickname.nword,
        "강아지",
        "고양이",
        "강아지",
        "고양이",
      ]);
    }
  }, [userNickname]);

  const [randomWordAList, setRandomWordAList] = useState<string[]>([]);
  const [randomWordNList, setRandomWordNList] = useState<string[]>([]);
  const [randomWordAnimation, setRandomWordAnimation] =
    useState<boolean>(false);

  return (
    <StyledNicknamePicker>
      <div>
        <div>
          <RandomNicknameCard
            nickname={userNickname.aword ?? ""}
            randomWordList={randomWordAList}
            randomWordAnimation={randomWordAnimation}
          ></RandomNicknameCard>
          <RandomNicknameCard
            nickname={userNickname.nword ?? ""}
            randomWordList={randomWordNList}
            randomWordAnimation={randomWordAnimation}
          ></RandomNicknameCard>
        </div>
        <p>다시 고를래요</p>
        <FloatIconBtn onClick={getNewNickname}>
          <TbReload size={24} />
        </FloatIconBtn>
      </div>
      <CommonBtn onClick={onClick} btnType="primary">
        결정했어요
      </CommonBtn>
    </StyledNicknamePicker>
  );
}

export default NicknamePicker;

const StyledNicknamePicker = styled.div`
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-grow: 1;
  & > div {
    flex-grow: 1;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    & > div {
      display: flex;
      width: 100%;
      height: 52px;
      margin-bottom: 40px;
    }
    & > p {
      color: ${({ theme }) => theme.color.text.secondary};
      ${({ theme }) => theme.text.caption};
      margin-bottom: 8px;
    }
  }
`;
