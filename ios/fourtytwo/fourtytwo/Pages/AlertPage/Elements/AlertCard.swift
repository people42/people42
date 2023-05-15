import SwiftUI

struct AlertCard: View {
    var notificationHistory: NotificationHistory
    
    var body: some View {
        VStack {
            HStack {
                Image(notificationHistory.emoji)
                    .scaleEffect(0.8)
                    .frame(width: 40, height: 40)
                VStack {
                    HStack {
                        Text(notificationHistory.title)
                            .font(.customBody2)
                        Spacer()
                    }
                    .padding(.bottom, 4)
                    
                    HStack(alignment: .bottom) {
                        Text(notificationHistory.body)
                            .font(.customCaption)
                        Spacer()
                        Text(timeAgoSinceDate(from: notificationHistory.createdAt))
                            .font(.customOverline)
                    }
                }
                .padding(EdgeInsets(top: 4, leading: 16, bottom: 0, trailing: 8))
                
            }
            .padding(.bottom, 8)
            Divider()
        }
        .padding(.horizontal, 16)
        .padding(.top, 8)
    }
}

extension AlertCard {
    private func timeAgoSinceDate(from dateString: String, numericDates: Bool = true) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        guard let date = dateFormatter.date(from: dateString) else {
            return dateString
        }

        let calendar = Calendar.current
        let now = Date()
        let earliest = now < date ? now : date
        let latest = (earliest == now) ? date : now

        let components: DateComponents = calendar.dateComponents([.minute, .hour, .day, .weekOfYear, .month, .year, .second], from: earliest, to: latest)

        if (components.year! >= 2) {
            return "\(components.year!)년 전"
        } else if (components.year! >= 1){
            return (numericDates ? "1년 전" : "작년")
        } else if (components.month! >= 2) {
            return "\(components.month!)달 전"
        } else if (components.month! >= 1){
            return (numericDates ? "1달 전" : "지난 달")
        } else if (components.weekOfYear! >= 2) {
            return "\(components.weekOfYear!)주 전"
        } else if (components.weekOfYear! >= 1){
            return (numericDates ? "1주 전" : "지난 주")
        } else if (components.day! >= 2) {
            return "\(components.day!)일 전"
        } else if (components.day! >= 1){
            return (numericDates ? "1일 전" : "어제")
        } else if (components.hour! >= 2) {
            return "\(components.hour!)시간 전"
        } else if (components.hour! >= 1){
            return (numericDates ? "1시간 전" : "한 시간 전")
        } else if (components.minute! >= 2) {
            return "\(components.minute!)분 전"
        } else if (components.minute! >= 1){
            return (numericDates ? "1분 전" : "일 분 전")
        } else {
            return (numericDates ? "방금 전" : "방금 전")
        }
    }

}

struct AlertCard_Previews: PreviewProvider {
    
    static let noti: NotificationHistory = NotificationHistory(title: "누군가 감정을 표현했어요", body: "나는 코딩중", emoji: "fire", createdAt: "2023-05-10T01:14:55")
    
    static var previews: some View {
        VStack {
            AlertCard(notificationHistory: noti)
            AlertCard(notificationHistory: noti)
        }
    }
}
