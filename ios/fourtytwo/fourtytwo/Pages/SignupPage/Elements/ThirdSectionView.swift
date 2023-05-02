import SwiftUI
import Alamofire
import WebKit

struct ThirdSectionView: View {
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var signUpData: SignUpState
    
    @State private var isChecked = false
    @State private var showAgreementDetail = false
    
    var body: some View {
        VStack {
            Spacer()
            Text("환영합니다")
                .font(.customHeader4)
            Spacer()
            HStack {
                Button(action: {
                    isChecked.toggle()
                }) {
                    Image(systemName: isChecked ? "checkmark.square.fill" : "square")
                        .foregroundColor(isChecked ? .green : .gray)
                }
                
                Button(action: {
                    showAgreementDetail.toggle()
                }) {
                    HStack {
                        Text("이용약관 및 개인정보 처리 방침")
                            .font(.customSubtitle1)
                            .foregroundColor(Color("Text"))
                        Image(systemName: "chevron.right")
                            .foregroundColor(Color("Text"))
                    }
                }.sheet(isPresented: $showAgreementDetail) {
                    AgreementDetailView(showAgreementDetail: $showAgreementDetail)
                }
            }
            .padding()
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(Color.gray, lineWidth: 2)
            )
            .padding([.leading, .trailing])
            Spacer()
                .frame(maxHeight: 40)
            HStack {
                CustomButton(style: isChecked ? .blue : .disabled, buttonText: "시작하기", action: {
                    signUp()
                }, isDisabled: !isChecked)
            }
            Spacer()
                .frame(maxHeight: 40)
        }
    }
    
    private func signUp() {
        let loginType = signUpData.loginType
        let data = [
            "email" : signUpData.email,
            "nickname" : signUpData.nickname,
            "o_auth_token" : signUpData.oAuthToken,
            "emoji" : signUpData.emoji
        ]

        switch loginType {
        case .google:
            UserService.signupGoogle(data: data, completion: handleSignUpResult)
        case .apple:
            UserService.signupApple(data: data, completion: handleSignUpResult)
        case .none:
            print("로그인 타입 에러")
        }
    }

    private func handleSignUpResult(_ result: Result<ResponseMessage<UserData>, AFError>) {
        switch result {
        case .success(let responseMessage):
            if let userData = responseMessage.data {
                // 유저 정보 저장
                APIManager.shared.userState.user_idx = userData.user_idx
                APIManager.shared.userState.email = userData.email
                APIManager.shared.userState.nickname = userData.nickname
                APIManager.shared.userState.emoji = userData.emoji
                APIManager.shared.setAccessToken(at: userData.accessToken, rt: userData.refreshToken)
                
                // 홈으로 이동
                appState.currentView = .home
            }
            print("response: \(responseMessage)")
        case .failure(let error):
            print("Error: \(error.localizedDescription)")
        }
    }

}

struct AgreementDetailView: View {
    @Binding var showAgreementDetail: Bool
    
    var body: some View {
        VStack {
            PolicyWebView()
        }
    }
}

struct WebView: UIViewRepresentable {
    let url: String
    
    func makeUIView(context: Context) -> WKWebView {
        return WKWebView()
    }
    
    func updateUIView(_ uiView: WKWebView, context: Context) {
        guard let url = URL(string: url) else {
            return
        }
        let request = URLRequest(url: url)
        uiView.load(request)
    }
}

struct ThirdSectionView_Previews: PreviewProvider {
    static var previews: some View {
        ThirdSectionView()
    }
}
