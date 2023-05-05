import SwiftUI

struct PlacePersonView: View {
    let userIdx: Int
    let placeIdx: Int
    let placeName: String
    let profileImage: String
    let nickname: String
    
    @State var brushCnt: Int?
    @State var messagesInfo: [PersonMessageInfo]?
    
    var body: some View {
        VStack {
            
            if let brushCnt = brushCnt {
                Text("\(placeName)에서 \(brushCnt)번이나 마주쳤어요!")
                    .padding(24)
            }
            
            ScrollView {
                VStack {
                    if let messagesInfo = messagesInfo {
                        ForEach(messagesInfo, id: \.messageIdx) { messageInfo in
                            PersonMessageCard(messageInfo: messageInfo)
                                .padding(.bottom, 16)
                        }
                    } else {
                        HStack{
                            Spacer()
                        }
                    }
                }
                .padding()
                .padding(.top, 16)
            }
        }
        .background(Color("BgPrimary")).edgesIgnoringSafeArea(.bottom)
        .onAppear {
            getPersonPlaceFeed()
        }
    }
}

extension PlacePersonView {
    private func getPersonPlaceFeed() {
        let data = [
            "userIdx": userIdx,
            "placeIdx": placeIdx
        ]
        FeedService.getPersonPlaceFeed(data: data) { result in
            switch result {
            case .success(let response):
                if let responseData = response.data {
                    DispatchQueue.main.async {
                        brushCnt = responseData.brushCnt
                        
                        messagesInfo = responseData.messagesInfo.map { messageDto in
                            PersonMessageInfo(
                                messageIdx: messageDto.messageIdx,
                                profileImage: profileImage,
                                nickname: nickname,
                                time: messageDto.time,
                                contents: messageDto.content,
                                emotion: messageDto.emtion ?? "delete"
                            )
                        }
                    }
                }
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
}

struct PlacePersonView_Previews: PreviewProvider {
    static var previews: some View {
        PlacePersonView(userIdx: 1, placeIdx: 1, placeName: "gkd", profileImage: "alien", nickname: "qkqh")
    }
}
