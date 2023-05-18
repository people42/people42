import SwiftUI

struct ReactionButton: View {
    
    @State private var showReactions = false
    @State private var selectedReaction: String?
    
    let messageIdx: Int
    @State private var emotion: String {
        didSet {
            selectedReaction = (emotion == "delete") ? nil : emotion
        }
    }
    
    let reactions: [String] = ["heart", "fire", "tear", "thumbsUp"]
    
    let size: CGFloat = 40
    
    // init()를 명시적으로 작성할때는 모두 초기화 해야함, init()를 사용안 할 경우 자동으로 같은 이름의 프로퍼티와 연동됨.
    init(messageIdx: Int, emotion: String) {
        self.messageIdx = messageIdx
        self.emotion = emotion
        // selectedReaction 초기값 설정
        // _는 저장프로퍼티 접근, State(initialValue:)로 초기값 저장
        _selectedReaction = State(initialValue: emotion == "delete" ? nil : emotion)
    }


    var body: some View {
        ZStack {
            if showReactions {
                Capsule()
                    .frame(width: size + CGFloat(reactions.count) * size, height: size)
                    .foregroundColor(Color("BgSecondary"))
                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
            } else {
                Circle()
                    .frame(width: size, height: size)
                    .foregroundColor(Color("BgSecondary"))
                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
            }
            HStack(spacing: 10) {
                if showReactions {
                    ForEach(reactions, id: \.self) { reaction in
                        Button(action: {
                            withAnimation(.easeInOut(duration: 0.3)) {
                                selectedReaction = reaction
                                selectEmotion()
                                showReactions = false
                            }
                        }) {
                            Image(reaction)
                                .resizable()
                                .frame(width: size*0.7, height: size*0.7)
                        }
                    }
                }
                
                Button(action: {
                    withAnimation(.easeInOut(duration: 0.3)) {
                        if showReactions {
                            selectedReaction = nil
                            selectEmotion()
                        }
                        showReactions.toggle()
                    }
                }) {
                    ZStack {
                        HStack {
                            if showReactions {
                                Image(systemName: "xmark")
                                    .font(.system(size: size*0.5, weight: .black))
                                    .foregroundColor(.monotoneGray)
                            } else {
                                if let selected = selectedReaction {
                                    Image(selected)
                                        .resizable()
                                        .frame(width: size*0.7, height: size*0.7)
                                        .foregroundColor(.monotoneGray)
                                } else {
                                    Image(systemName: "plus")
                                        .font(.system(size: size*0.5, weight: .black))
                                        .foregroundColor(.monotoneGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private func selectEmotion() {
        var data: [String: Any] = ["messageIdx": messageIdx]
        
        if let emotion = selectedReaction {
            data["emotion"] = emotion
        } else {
            data["emotion"] = "delete"
        }
        
       
        FeedService.selectEmotion(data: data) { result in
            switch result {
            case .success( _):
                print("emotion selected!")
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
}

struct ReactionButton_Previews: PreviewProvider {
    
    static var previews: some View {
        ReactionButton(messageIdx: 1, emotion: "heart")
    }
}
