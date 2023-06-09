export function formatMessageDate(dateString: string) {
  const date = new Date(dateString);
  const now = new Date();

  // 현재 날짜의 0시 0분 0초를 기준으로 한 Date 객체 생성
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  // 입력된 날짜와 현재 날짜의 차이 계산
  const diffTime = date.getTime() - today.getTime();
  const hour = date.getHours();

  const diffMs = now.valueOf() - date.valueOf();
  const diffHours = Math.floor(diffMs / 3600000);

  console.log(date, diffMs, diffHours);
  if (diffTime >= 0) {
    // 오늘인 경우
    return `오늘 ${hour}시 쯤`;
  } else if (diffHours < 24) {
    // 어제인 경우
    return `어제 ${hour}시 쯤`;
  } else {
    const month = date.toLocaleString("default", { month: "long" });
    const day = date.getDate();
    const hour = date.getHours();
    return `${month} ${day}일 ${hour}시 쯤`;
  }
}

export function getTodayDate() {
  const today = new Date();
  const year = today.getFullYear();
  const month = (today.getMonth() + 1).toString().padStart(2, "0");
  const day = today.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function notificationDateTime(dtStr: string) {
  const dt = new Date(dtStr);
  const now = new Date();
  const diffMs = now.valueOf() - dt.valueOf();
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  if (diffMins <= 59) {
    return diffMins + "분 전";
  } else if (diffHours <= 23) {
    return diffHours + "시간 전";
  } else {
    const month = dt.toLocaleString("default", { month: "long" });
    const day = dt.getDate();
    const hour = dt.getHours();
    const min = dt.getMinutes();
    return `${month} ${day}일 ${hour}시 ${min}분`;
  }
}
