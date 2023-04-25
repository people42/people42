import { getNickname } from "../../../../api/auth";
import { CommonBtn, FloatIconBtn } from "../../../../components/index";
import { signUpUserState } from "../../../../recoil/user/atoms";
import RandomNicknameCard from "./RandomNicknameCard";
import _ from "lodash";
import { useEffect, useState } from "react";
import { TbReload } from "react-icons/tb";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type nicknamePickerProps = {
  onClick(e: React.MouseEvent): void;
};

const aWordList: string[] = [
  "아름다운",
  "작은",
  "큰",
  "빠른",
  "느린",
  "높은",
  "낮은",
  "좋은",
  "나쁜",
  "맛있는",
  "신선한",
  "오래",
];
const nWordList: string[] = [
  "호랑이",
  "사자",
  "곰",
  "양",
  "말",
  "돼지",
  "고양이",
  "강아지",
  "코끼리",
  "기린",
  "상어",
  "고래",
  "원숭이",
  "쥐",
  "물고기",
  "독수리",
  "참새",
  "장미",
  "백합",
  "튤립",
];

function NicknamePicker({ onClick }: nicknamePickerProps) {
  const [signUpUser, setSignUpUser] = useRecoilState(signUpUserState);

  const setSignUpUserNickname = (nickname: string) => {
    const newSignUpUser = Object.assign({}, signUpUser);
    newSignUpUser.nickname = nickname;
    setSignUpUser(newSignUpUser);
  };

  const [userNickname, setUserNickname] = useState<{
    aword: string | null;
    nword: string | null;
  }>({ aword: null, nword: null });

  useEffect(() => {
    getNewNickname();
  }, []);

  const getNewNickname = async () => {
    getNickname()
      .then((res) => {
        console.log();
        if (!randomWordAnimation) {
          setRandomWordAnimation(true);
          const splitNickname = res.data.data.nickname.split(" ");
          setTimeout(() => {
            setUserNickname({
              aword: splitNickname[0],
              nword: splitNickname[1],
            });
            setRandomWordAnimation(false);
          }, 100);
        }
      })
      .catch((e) => console.log(e));
  };

  useEffect(() => {
    setSignUpUserNickname(
      userNickname.aword && userNickname.nword
        ? userNickname.aword + " " + userNickname.nword
        : ""
    );
    if (userNickname.aword && userNickname.nword) {
      setRandomWordAList([
        "",
        "",
        "",
        "",
        userNickname.aword,
        _.sample(aWordList)!,
        _.sample(aWordList)!,
        _.sample(aWordList)!,
        "",
      ]);
      setRandomWordNList([
        "",
        "",
        "",
        "",
        userNickname.nword,
        _.sample(nWordList)!,
        _.sample(nWordList)!,
        _.sample(nWordList)!,
        "",
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
