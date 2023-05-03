//import SwiftUI
//
//
//struct PersonMessageCard: View {
//    let messageInfo: PersonPlaceResDtos
//    @Environment(\.colorScheme) var colorScheme
//    @State var fulllText = false
//    
//    var body: some View {
//        ZStack {
//            ZStack {
//                
//                RoundedRectangle(cornerRadius: 24)
//                    .foregroundColor(messageInfo.cardColor.color(for: colorScheme))
//                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
//                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: -2, y: -1)
//                
//            }
//            VStack(alignment: .leading) {
//                
//                VStack(alignment: .leading, spacing: 4) {
//                    Text(messageInfo.)
//                        .font(.customOverline)
//                    
//                    if fulllText {
//                        Text(messageInfo.contents)
//                            .font(.customBody1)
//                            .lineLimit(nil)
//                            .fixedSize(horizontal: false, vertical: true)
//                            .padding(.bottom, 16)
//                    } else {
//                        Text(messageInfo.contents)
//                            .font(.customBody1)
//                            .padding(.bottom, 16)
//                    }
//
//                    
//                    HStack {
//                        if let placeName = messageInfo.placeName {
//                            Text(placeName)
//                                .font(.customSubtitle2)
//                        }
//                        Spacer()
//                    }
//                    
//                    if let hour = messageInfo.hour {
//                        Text(getTimeStringFromISODate(hour))
//                            .font(.customCaption)
//                            .foregroundColor(.monotoneLight)
//                    }
//                }
//                .padding(.horizontal)
//                .padding(.top)
//                .foregroundColor(Color("Text"))
//                
//                Spacer()
//            }
//            
//            VStack {
//                Spacer()
//                HStack(alignment: .bottom) {
//                    Spacer()
//                    ReactionButton(messageIdx: messageInfo.messageIdx, emotion: messageInfo.emotion)
//                        .offset(x: -25, y: 25)
//                }
//                .padding(.trailing)
//            }
//        }
//        .frame(height: 152)
//        .padding(.bottom, 16)
//    }
//
//    
//    func getTimeStringFromISODate(_ isoString: String) -> String {
//        let dateFormatter = DateFormatter()
//        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
//        dateFormatter.timeZone = Calendar.current.timeZone
//        
//        if let date = dateFormatter.date(from: isoString) {
//            let calendar = Calendar.current
//            let hour = calendar.component(.hour, from: date)
//            
//            // 현재 날짜와 비교하여 날짜가 어제인지 오늘인지 판단
//            let now = Date()
//            let components = calendar.dateComponents([.day], from: date, to: now)
//            
//            if components.day == 0 {
//                return "오늘 \(hour)시쯤"
//            } else if components.day == -1 {
//                return "어제 \(hour)시쯤"
//            } else if let day = components.day, day < 0 {
//                // 이전 날짜인 경우
//                let absDay = abs(day)
//                return "\(absDay)일전 \(hour)시쯤"
//            } else if let day = components.day, day > 0 {
//                // 이후 날짜인 경우
//                return "\(day)일후 \(hour)시쯤"
//            }
//        }
//        return "시간정보 없음"
//    }
//
//}
//
