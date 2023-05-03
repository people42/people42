import Foundation

func getTimeStringFromISODate(_ isoString: String) -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    dateFormatter.timeZone = Calendar.current.timeZone
    if let date = dateFormatter.date(from: isoString) {
        let calendar = Calendar.current
        let hour = calendar.component(.hour, from: date)

        // 현재 날짜와 비교하여 날짜가 어제인지 오늘인지 판단
        let now = Date()
        let components = calendar.dateComponents([.day], from: now, to: date)

        if components.day == 0 {
            return "오늘 \(hour)시쯤"
        } else if components.day == 1 {
            return "내일 \(hour)시쯤"
        } else if components.day == -1 {
            return "어제 \(hour)시쯤"
        } else if let days = components.day, days > 1 {
            return "\(abs(days))일 후 \(hour)시쯤"
        } else if let days = components.day, days < -1 {
            return "\(abs(days))일 전 \(hour)시쯤"
        }
    }
    return "시간정보 없음"
}
