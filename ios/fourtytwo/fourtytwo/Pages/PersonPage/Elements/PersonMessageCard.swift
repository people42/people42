import SwiftUI

struct PersonMessageInfo {
    let messageIdx: Int
    let profileImage: String
    let nickname: String
    let time: String
    let contents: String
    let emotion: String
}


struct PersonMessageCard: View {
    @Environment(\.colorScheme) var colorScheme
    
    let messageInfo: PersonMessageInfo
    
    @State private var contentHeight: CGFloat = 0
    
    init(messageInfo: PersonMessageInfo) {
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
                
                RoundedRectangle(cornerRadius: 24)
                    .foregroundColor(Color("BgSecondary"))
                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: -2, y: -1)
                
            }
            VStack(alignment: .leading) {
                HStack(alignment: .bottom, spacing: 4) {
                    GifImage(messageInfo.profileImage, isAnimated: false)
                        .frame(width: 40, height: 40)
                    Spacer()
                }
                .padding(.top, -16)
                .padding(.leading, -16)
                .offset(x: 32)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(messageInfo.nickname)
                        .font(.customOverline)
                    
                    Text(getTimeStringFromISODate(messageInfo.time ))
                    .font(.customOverline)
                    Spacer()
                    Text(messageInfo.contents)
                        .font(.customBody2)
                        .lineLimit(nil)
                        .multilineTextAlignment(.leading)
                        .fixedSize(horizontal: false, vertical: true)
                    Spacer()

                }
                .padding(.horizontal)
                .foregroundColor(Color("Text"))
                
                Spacer()
            }
        }
        .frame(height: 100 + contentHeight)
        .padding(.bottom, 24)
    }

}

struct PersonMessageCard_Preview: PreviewProvider {
    static var previews: some View {
        PersonMessageCard(messageInfo: PersonMessageInfo(messageIdx: 1, profileImage: "alien", nickname: "NICKNAME", time: "2023-04-20T13:12:10", contents: "ContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContentsContents", emotion: "delete"))
    }
}

