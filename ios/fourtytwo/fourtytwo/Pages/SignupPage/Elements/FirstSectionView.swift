import SwiftUI

struct FirstSectionView: View {
    @Binding var progress: Int
    @EnvironmentObject var signUpData: SignUpState

    @State private var leftIndex = 0
    @State private var rightIndex = 0
    @State private var shouldAnimate = false
    @State private var buttonScale: CGFloat = 1.0
    @State private var newLeftText: [String] = []
    @State private var newRightText: [String] = []
    @State private var isClicked = false
    

    let firstWords = ["눌러", "주세요"]
    
    var body: some View {
        VStack {
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
                    progress += 1
                }, isDisabled: !isClicked)
            }
            Spacer()
                .frame(maxHeight: 40)
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
                        signUpData.nickname = nameData
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
}

struct SlotMachineText: View {
    let words: [String]
    @Binding var currentIndex: Int
    
    var body: some View {
        GeometryReader { geometry in
            ZStack {
                RoundedRectangle(cornerRadius: 24)
                    .foregroundColor(Color("BgPrimary")) // 글자 상자 색상 위치
                ForEach(words.indices, id: \.self) { index in
                    Text(words[index])
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(Color("Text"))
                        .position(x: geometry.size.width / 2, y: geometry.size.height / 2)
                        .offset(y: CGFloat(currentIndex - index) * 30)
                }
            }
        }
        .frame(width: 141, height: 86)
        .clipShape(RoundedRectangle(cornerRadius: 24))
        .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
        .animation(.easeInOut, value: currentIndex)
    }
}

struct SignupView_Previews2: PreviewProvider {
    static var previews: some View {
        SignupView()
    }
}

