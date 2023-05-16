import SwiftUI

struct MessageInfo {
    let profileImage: String
    let stack: Int
    let nickname: String
    let contents: String
    let placeIdx: Int?
    let placeName: String
    let hour: String
    let hasMultiple: Bool
    let cardColor: CardColor
    let messageIdx: Int
    let emotion: String
    let userIdx: Int
}

struct MessageCard: View {
    @Environment(\.colorScheme) var colorScheme
    
    let messageInfo: MessageInfo
    
    @State private var contentHeight: CGFloat = 0
    
    @State private var showActionSheet = false
    @State private var showReportMessageSheet = false
    @State private var showBlockUserSheet = false
    
    init(messageInfo: MessageInfo) {
        self.messageInfo = messageInfo
        
        let font = UIFont.preferredFont(forTextStyle: .body)
        let attributedText = NSAttributedString(string: messageInfo.contents, attributes: [.font: font])
        let textWidth = UIScreen.main.bounds.width - 32 // Adjust the width based on your layout
        let textHeight = attributedText.boundingRect(with: CGSize(width: textWidth, height: .greatestFiniteMagnitude), options: [.usesLineFragmentOrigin, .usesFontLeading], context: nil).height
        self._contentHeight = State(initialValue: textHeight)
    }

    
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
                    
                    HStack {
                        Text(messageInfo.nickname)
                            .font(.customOverline)
                            .foregroundColor(Color("Text")) +
                        Text("님과 \(messageInfo.stack)번 스쳤습니다.")
                            .font(.customOverline)
                            .foregroundColor(Color("Text"))
                            .fontWeight(.bold)
                        
                        Spacer()
                        
                        Button(action: {
                            showActionSheet.toggle()
                        }) {
                            Image(systemName: "ellipsis")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(Color("Text"))
                        }
                        
                        Spacer()
                            .frame(width: 48)
                    }
                }
                .padding(.top, -16)
                .padding(.leading, -16)
                .offset(x: 32)
                
                VStack(alignment: .leading, spacing: 4) {
                    
                    Text(messageInfo.contents)
                        .font(.customBody2)
                        .lineLimit(nil)
                        .multilineTextAlignment(.leading)
                        .fixedSize(horizontal: false, vertical: true)
                        .padding(.bottom, 16)

                    
                    HStack {
                        Text(messageInfo.placeName)
                            .font(.customOverline)
                        Spacer()
                    }
                    
                    Text(getTimeStringFromISODate(messageInfo.hour))
                        .font(.customOverline)
                }
                .padding(.horizontal, 16)
                .padding(.top)
                .foregroundColor(Color("Text"))
                
                Spacer()
            }
            
            VStack {
                Spacer()
                HStack(alignment: .bottom) {
                    Spacer()
                    ReactionButton(messageIdx: messageInfo.messageIdx, emotion: messageInfo.emotion)
                        .offset(x: 0, y: 20)
                }
                .padding(.trailing)
            }
        }
        .frame(height: 108 + contentHeight)
        .padding(.bottom, 16)
        .actionSheet(isPresented: $showActionSheet) {
            ActionSheet(title: Text("하나의 게시글에 한 번의 신고만 가능합니다.\n3번의 신고가 이루어지면 게시글이 삭제될 수 있습니다."),
                buttons: [
                    .destructive(Text("신고")) {
                        showReportMessageSheet = true
                    },
                    .destructive(Text("차단")) {
                        showBlockUserSheet = true
                    },
                    .cancel(Text("취소")) { }
                ])
        }
        .sheet(isPresented: $showReportMessageSheet) {
            ReportView(nickname: messageInfo.nickname, messageIdx: messageInfo.messageIdx)
        }
        .sheet(isPresented: $showBlockUserSheet) {
            BlockView(nickname: messageInfo.nickname, userIdx: messageInfo.userIdx)
        }
    }
}

struct MessageCard_Previews: PreviewProvider {
    static var previews: some View {
        MessageCard(messageInfo: MessageInfo(profileImage: "alien", stack: 30, nickname: "NICKNAME", contents: "ContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContents", placeIdx: 1, placeName: "Place", hour: "2023-04-20T13:12:10", hasMultiple: true, cardColor: .blue, messageIdx: 1, emotion: "delete", userIdx: 1))
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

