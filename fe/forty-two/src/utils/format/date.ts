export function formatMessageDate(dateString: string) {
  const date = new Date(dateString);
  const now = new Date();

  const diffTime = now.getTime() - date.getTime();
  const diffDays = diffTime / (1000 * 3600 * 24);

  if (diffDays < 1) {
    // 오늘인 경우
    const hour = date.getHours();
    return `오늘 ${hour}시 쯤`;
  } else if (diffDays < 2) {
    // 어제인 경우
    const hour = date.getHours();
    return `어제 ${hour}시 쯤`;
  } else {
    // 그 외의 경우
    return dateString;
  }
}

export function getTodayDate() {
  const today = new Date();
  const year = today.getFullYear();
  const month = (today.getMonth() + 1).toString().padStart(2, "0");
  const day = today.getDate().toString().padStart(2, "0");
  return `${year}-${month}-${day}`;
}
