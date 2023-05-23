import SwiftUI

struct CustomButton: View {
    enum Style {
        case blue, disabled, text
    }
    
    let style: Style
    let buttonText: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(buttonText)
                .font(.customButton)
                .padding(12)
                .padding(.horizontal, 24)
                .foregroundColor(getFontColor())
                .background(getButtonColor())
                .cornerRadius(32)
        }
        .disabled(style == .disabled)
    }
    
    private func getButtonColor() -> Color {
        switch style {
        case .blue:
            return Color.blue
        case .disabled:
            return Color.monotoneGray
        case .text:
            return Color.clear
        }
    }
    
    private func getFontColor() -> Color {
        switch style {
        case .blue:
            return Color.white
        case .disabled:
            return Color.monotoneTextTranslucent
        case .text:
            return Color.blue
        }
    }
}

struct CustomButton_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            CustomButton(style: .blue, buttonText: "버튼", action: {})
            CustomButton(style: .disabled, buttonText: "버튼", action: {})
            CustomButton(style: .text, buttonText: "버튼", action: {})
        }
    }
}

// 사용법
//CustomButton(style: .blue, buttonText: "파란색 버튼", action: {
//    print("파란색 버튼이 클릭되었습니다.")
//}, isDisabled: false)
//
//CustomButton(style: .disabled, buttonText: "Disable 버튼", action: {
//    print("Disable 버튼이 클릭되었습니다.")
//}, isDisabled: true)
//
//CustomButton(style: .text, buttonText: "배경색 없는 텍스트 버튼", action: {
//    print("배경색 없는 텍스트 버튼이 클릭되었습니다.")
//}, isDisabled: false)

