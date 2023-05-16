import SwiftUI

struct NicknameChangeView: View {
    @EnvironmentObject var userstate: UserState
    @Environment(\.presentationMode) var presentationMode

    @State private var leftIndex = 0
    @State private var rightIndex = 0
    @State private var shouldAnimate = false
    @State private var buttonScale: CGFloat = 1.0
    @State private var newLeftText: [String] = []
    @State private var newRightText: [String] = []
    @State private var isClicked = false
    @State private var newNickname = ""
    

    let firstWords = ["눌러", "주세요"]
    
    var body: some View {
        VStack {
            Spacer()
                .frame(height: 40)
            HStack {
                Spacer()
                    .frame(width: 32)
                Text("닉네임을 \n선택해주세요!")
                    .font(.customHeader4)
                Spacer()
            }
            Spacer()
            HStack(spacing: 10) {
                Spacer()
                SlotMachineText(words: [firstWords[0]] + [""] + newLeftText, currentIndex: $leftIndex)
                SlotMachineText(words: [firstWords[1]] + [""] + newRightText, currentIndex: $rightIndex)
                
                Spacer()
            }
            Spacer()
                .frame(maxHeight: 100)
            Circle()
                .frame(width: 50, height: 50)
                .foregroundColor(Color("BgPrimary"))
                .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
                .overlay(
                    Image(systemName: "arrow.clockwise")
                        .font(.system(size: 24))
                )
                .scaleEffect(buttonScale)
                .onTapGesture {
                    if !shouldAnimate {
                        let feedbackGenerator = UIImpactFeedbackGenerator(style: .medium)
                        feedbackGenerator.prepare()
                        feedbackGenerator.impactOccurred()
                        shouldAnimate = true
                        addNewText()
                        animateText()
                        buttonPressedAnimation()
                        isClicked = true
                    }
                }
            Spacer()
            HStack {
                CustomButton(style: isClicked ? .blue : .disabled, buttonText: "완료", action: {
                    changeNickname()
                }, isDisabled: !isClicked)
            }
            Spacer()
                .frame(maxHeight: 40)
        }
        .padding(.top, 32)
        .background(Color.backgroundPrimary.edgesIgnoringSafeArea(.all))
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .regular))
                        .foregroundColor(Color("Text"))
                }
            }
            ToolbarItem(placement: .principal) {
                Text("닉네임변경")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }
    }
    
    
    private func addNewText() {
        
        // 랜덤 닉네임 요청
        UserService.randomNickname() { result in
            switch result {
            case .success(let responseMessage):
                print("Response: \(responseMessage)")
                
                DispatchQueue.main.async {
                    if let nameData = responseMessage.data?.nickname {
                        print("Nickname data: \(nameData)")
                        let nameArray = nameData.split(separator: " ")
                        // 새로운 글자를 추가
                        newLeftText.append(String(nameArray[0]))
                        newLeftText.append("")
                        newRightText.append(String(nameArray[1]))
                        newRightText.append("")
                        
                        newNickname = nameData
                    }
                }
                
            case .failure(let error):
                print("Error: \(error.localizedDescription)")
            }
        }
        

    }
    
    private func animateText() {
        let interval: TimeInterval = 1
        
        Timer.scheduledTimer(withTimeInterval: interval, repeats: true) { timer in
            withAnimation(.easeInOut(duration: interval)) {
                leftIndex += 2
                rightIndex += 2
            }
            timer.invalidate()
            shouldAnimate = false
        }
    }
    
    private func buttonPressedAnimation() {
        withAnimation(.easeInOut(duration: 0.15)) {
            buttonScale = 0.85
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) {
            withAnimation(.easeInOut(duration: 0.15)) {
                buttonScale = 1.0
            }
        }
    }
    
    private func changeNickname() {
        let data = ["nickname": newNickname]
        AccountService.changeNickname(data: data) { result in
            switch result {
            case .success( _):
                print("NickName Changed!")
                userstate.nickname = newNickname
                presentationMode.wrappedValue.dismiss()
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
    
}


struct NicknameChangeView_Previews: PreviewProvider {
    static var previews: some View {
        NicknameChangeView()
    }
}
