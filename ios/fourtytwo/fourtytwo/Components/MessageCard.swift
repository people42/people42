import SwiftUI


struct MessageCard: View {
    let messageInfo: MessageInfo
    @Environment(\.colorScheme) var colorScheme
    @State var fulllText = false
    
    var body: some View {
        ZStack {
            ZStack {
                
                if messageInfo.hasMultiple {
                    ForEach(1..<3, id: \.self) { index in
                        RoundedRectangle(cornerRadius: 24)
                            .foregroundColor(messageInfo.cardColor.color(for: colorScheme).opacity(0.5))
                            .offset(x: CGFloat(index) * 4, y: CGFloat(index) * 4)
                    }
                }
                
                RoundedRectangle(cornerRadius: 24)
                    .foregroundColor(messageInfo.cardColor.color(for: colorScheme))
                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: -2, y: -1)
                
            }
            VStack(alignment: .leading) {
                HStack(alignment: .bottom, spacing: 4) {
                    GifImage(messageInfo.profileImage, isAnimated: false)
                        .frame(width: 40, height: 40)
                    
                    Text("\(messageInfo.stack)번 스쳤습니다.")
                        .font(.system(size: 16))
                        .fontWeight(.bold)
                }
                .padding(.top, -16)
                .padding(.leading, -16)
                .offset(x: 32)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(messageInfo.nickname)
                        .font(.customOverline)
                    
                    if fulllText {
                        Text(messageInfo.contents)
                            .font(.customBody1)
                            .lineLimit(nil)
                            .fixedSize(horizontal: false, vertical: true)
                            .padding(.bottom, 16)
                    } else {
                        Text(messageInfo.contents)
                            .font(.customBody1)
                            .padding(.bottom, 16)
                    }

                    
                    HStack {
                        if let placeName = messageInfo.placeName {
                            Text(placeName)
                                .font(.customSubtitle2)
                        }
                        Spacer()
                    }
                    
                    if let hour = messageInfo.hour {
                        Text(getTimeStringFromISODate(hour))
                            .font(.customCaption)
                            .foregroundColor(.monotoneLight)
                    }
                }
                .padding(.horizontal)
                .padding(.top)
                .foregroundColor(Color("Text"))
                
                Spacer()
            }
            
            VStack {
                Spacer()
                HStack(alignment: .bottom) {
                    Spacer()
                    ReactionButton(messageIdx: messageInfo.messageIdx, emotion: messageInfo.emotion)
                        .offset(x: -25, y: 25)
                }
                .padding(.trailing)
            }
        }
        .frame(height: 152)
        .padding(.bottom, 16)
    }

    
    func getTimeStringFromISODate(_ isoString: String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        dateFormatter.timeZone = Calendar.current.timeZone
        if let date = dateFormatter.date(from: isoString) {
            let calendar = Calendar.current
            let hour = calendar.component(.hour, from: date)
            
            // 현재 날짜와 비교하여 날짜가 어제인지 오늘인지 판단
            let now = Date()
            let components = calendar.dateComponents([.day], from: date, to: now)
            
            if components.day == 0 {
                return "오늘 \(hour)시쯤"
            } else if components.day == -1 {
                return "어제 \(hour)시쯤"
            }
        }
        return "시간정보 없음"
    }
}

struct MessageCard_Previews: PreviewProvider {
    static var previews: some View {
        MessageCard(messageInfo: MessageInfo(profileImage: "alien", stack: 30, nickname: "NICKNAME", contents: "Contents", placeIdx: 1, placeName: "Place", hour: "2023-04-20T13:12:10", hasMultiple: true, cardColor: .blue, messageIdx: 1, emotion: "delete"))
    }
}

enum CardColor: String, CaseIterable {
    case red, orange, yellow, green, sky, blue, purple, pink
    
    private static let redColor = DynamicColor(light: Color.PersonalRedLight, dark: Color.PersonalRedDark)
    private static let orangeColor = DynamicColor(light: Color.PersonalOrangeLight, dark: Color.PersonalOrangeDark)
    private static let yellowColor = DynamicColor(light: Color.PersonalYellowLight, dark: Color.PersonalYellowDark)
    private static let greenColor = DynamicColor(light: Color.PersonalGreenLight, dark: Color.PersonalGreenDark)
    private static let skyColor = DynamicColor(light: Color.PersonalSkyLight, dark: Color.PersonalSkyDark)
    private static let blueColor = DynamicColor(light: Color.PersonalBlueLight, dark: Color.PersonalBlueDark)
    private static let purpleColor = DynamicColor(light: Color.PersonalPurpleLight, dark: Color.PersonalPurpleDark)
    private static let pinkColor = DynamicColor(light: Color.PersonalPinkLight, dark: Color.PersonalPinkDark)
    
    func color(for colorScheme: ColorScheme) -> Color {
        switch self {
        case .red:
            return CardColor.redColor.color(for: colorScheme)
        case .orange:
            return CardColor.orangeColor.color(for: colorScheme)
        case .yellow:
            return CardColor.yellowColor.color(for: colorScheme)
        case .green:
            return CardColor.greenColor.color(for: colorScheme)
        case .sky:
            return CardColor.skyColor.color(for: colorScheme)
        case .blue:
            return CardColor.blueColor.color(for: colorScheme)
        case .purple:
            return CardColor.purpleColor.color(for: colorScheme)
        case .pink:
            return CardColor.pinkColor.color(for: colorScheme)
        }
    }
}

