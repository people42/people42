export function formatMessageDate(dateString: string) {
  const date = new Date(dateString);
  const now = new Date();

  // 현재 날짜의 0시 0분 0초를 기준으로 한 Date 객체 생성
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  // 입력된 날짜와 현재 날짜의 차이 계산
  const diffTime = date.getTime() - today.getTime();
  const hour = date.getHours();
  if (diffTime >= 0) {
    // 오늘인 경우
    return `오늘 ${hour}시 쯤`;
  } else {
    // 그 외의 경우
    return `어제 ${hour}시 쯤`;
  }
}

export function getTodayDate() {
  const today = new Date();
  const year = today.getFullYear();
  const month = (today.getMonth() + 1).toString().padStart(2, "0");
  const day = today.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`;
}
