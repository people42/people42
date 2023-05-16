import Foundation
import SwiftUI
import FirebaseMessaging

func getTimeStringFromISODate(_ isoString: String) -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    dateFormatter.timeZone = Calendar.current.timeZone
    if let date = dateFormatter.date(from: isoString) {
        let calendar = Calendar.current
        let hour = calendar.component(.hour, from: date)

        // 현재 날짜와 비교하여 날짜가 어제인지 오늘인지 판단
        let now = calendar.startOfDay(for: Date())
        let dateStartOfDay = calendar.startOfDay(for: date)
        let components = calendar.dateComponents([.day], from: now, to: dateStartOfDay)

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
        } else {
            return "\(hour)시쯤"
        }
    }
    return "시간정보 없음"
}


func sendFCMTokenToServer() {
    Messaging.messaging().token { token, error in
        if let error = error {
            print("토큰을 가져오는 데 오류가 발생했습니다. \(error)")
        } else if let token = token {
            let dataDict: [String: String] = ["token": token]
            // 서버로 FCM 토큰 전달
            UserService.postFCMToken(data: dataDict) { result in
                switch result {
                case .success(let response):
                    print("FCM 토큰 전송 성공: \(response.message)")
                case .failure(let error):
                    print("FCM 토큰 전송 실패: \(error)")
                }
            }
        }
    }
}
