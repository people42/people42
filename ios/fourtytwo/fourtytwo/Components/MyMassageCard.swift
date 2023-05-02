import SwiftUI

class MyMessageCardViewModel: ObservableObject {
    @Published var comment: String = ""
    
    func send() {
        let data = ["message": comment]
        AccountService.writeMessage(data: data) { result in
            switch result {
            case .success:
                // 전송 성공 시, comment를 초기화
                self.comment = ""
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
}


struct MyMessageCard: View {
    @EnvironmentObject var userstate: UserState
    @ObservedObject var viewModel = MyMessageCardViewModel()
    @Environment(\.scenePhase) private var scenePhase
    
    let cardType: CardType
    let hasMultiple: Bool
    let onSend: () -> Void
    
    let reactionType: [String] = ["fire_circle", "heart_circle", "tear_circle", "thumbsUp_circle"]
    
    var sortedReactionType: [String] {
        reactionType.sorted { cardType.reactionCnt[$0] ?? 0 > cardType.reactionCnt[$1] ?? 0 }
            .filter { cardType.reactionCnt[$0] ?? 0 > 0 }
    }
    
    enum CardType {
        case displayMessage(String, [String: Int])
        case writeMessage
        
        var reactionCnt: [String: Int] {
            switch self {
            case .displayMessage(_, let cnt):
                return cnt
            case .writeMessage:
                return [:]
            }
        }
    }
    
    var body: some View {
        VStack {
            GeometryReader { geometry in
                ZStack {
                    if hasMultiple {
                        ForEach(sortedReactionType.indices, id: \.self) { index in
                            RoundedRectangle(cornerRadius: 32)
                                .fill(Color.blue.opacity(0.2))
                                .offset(x: CGFloat(index) * 4, y: CGFloat(index) * 4)
                                .shadow(color: Color.black.opacity(0.2), radius: 2, x: 2, y: 2)
                        }
                    }

                    RoundedRectangle(cornerRadius: 24)
                        .fill(Color.blue)
                    
                    GifUIkit(userstate.emoji ??  "", isAnimated: true)
                        .frame(width: 80, height: 80)
                        .position(x: 60, y: 0)
                    
                    VStack(alignment: .leading) {
                        Spacer()
                            .frame(height: 32)
                        cardContent
                    }
                    .padding(16)
                }
                
            }
            .frame(height: 100)
        }
        .padding()
        .padding(.top, 32)
        .padding(.bottom, 8)
        .onAppear {
            // 홈 화면이 뜰 때마다 UserState를 가져와 업데이트합니다.
            userstate.update()
        }
        .onChange(of: scenePhase) { newScenePhase in
            if newScenePhase == .active {
                // foreground로 전환될 때 데이터를 새로 고칩니다.
                userstate.update()
            }
        }
        
    }
    
    @ViewBuilder
    private var cardContent: some View {
        switch cardType {
        case .displayMessage(let message, _):
            HStack {
                Text(message)
                    .padding(.horizontal, 16)
                    .foregroundColor(.white)
                    .font(.customSubtitle1)
                Spacer()
            }
            .overlay(
                ZStack(alignment: .leading) {
                    ForEach(sortedReactionType.indices, id: \.self) { i in
                        Image(sortedReactionType[i])
                            .scaleEffect(i==0 ? 1 : 0.7)
                            .shadow(radius: 2, x: 2, y: 2)
                            .offset(x: i == 0 ? -8 : CGFloat(i) * 42, y: ((i % 2) != 0) ? 10 : 0)
                    }
                }
                .offset(x: 20, y: -70)
            )

            
        case .writeMessage:
            HStack {
                TextField("지금 내 생각을 자유롭게 적어주세요", text: $viewModel.comment)
                    .padding(.vertical, 8)
                    .padding(.horizontal, 16)
                    .background(Color("BgSecondary"))
                    .textFieldStyle(PlainTextFieldStyle())
                    .cornerRadius(32)
                
                Button(action: {
                    // 메시지 전송 버튼을 눌렀을 때의 동작
                    viewModel.send()
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                        onSend()
                    }
                }) {
                    Image(systemName: "cloud.fill")
                        .foregroundColor(.white)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 8)
                        .background(Color.monotoneLightTranslucent)
                        .cornerRadius(32)
                }
            }
        }
    }
    
}

struct MyMessageCard_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 0) {
            MyMessageCard(cardType: .displayMessage("안녕하세요!", ["fire_circle": 1, "heart_circle": 2, "tear_circle": 3, "thumbsUp_circle": 4]), hasMultiple: true, onSend: {})
            MyMessageCard(cardType: .displayMessage("안녕하세요!", ["fire_circle": 4, "heart_circle": 3, "tear_circle": 2, "thumbsUp_circle": 4]), hasMultiple: false, onSend: {})
            MyMessageCard(cardType: .writeMessage, hasMultiple: false, onSend: {})
        }
    }
}
