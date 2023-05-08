import SwiftUI
import AuthenticationServices

struct AppleSigninButton : View{
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var signUpData: SignUpState
    
    var body: some View{
        SignInWithAppleButton(
            onRequest: { request in
                request.requestedScopes = [.fullName, .email]
            },
            onCompletion: { result in
                switch result {
                case .success(let authResults):
                    print("Apple Login Successful")
                    switch authResults.credential{
                        case let appleIDCredential as ASAuthorizationAppleIDCredential:
                           // 계정 정보 가져오기
//                            let UserIdentifier = appleIDCredential.user
//                            let fullName = appleIDCredential.fullName
//                            let name =  (fullName?.familyName ?? "") + (fullName?.givenName ?? "")
//                            let email = appleIDCredential.email
//                            let IdentityToken = String(data: appleIDCredential.identityToken!, encoding: .utf8)
//                            let AuthorizationCode = String(data: appleIDCredential.authorizationCode!, encoding: .utf8)
//
//                            print("UserIdentifier \(UserIdentifier)")
//                            print("fullName \(fullName)")
//                            print("이름 \(name)")
//                            print("이메일 \(email)")
//                            print("IdentityToken \(IdentityToken)")
//                            print("AuthorizationCode \(AuthorizationCode)")
                        
                            if let IdentityToken = String(data: appleIDCredential.identityToken!, encoding: .utf8) {

                                print("IdentityToken \(IdentityToken)")
                                
                                // 로그인 진행
                                login(accessToken: IdentityToken)
                            }
                            
                    default:
                        break
                    }
                case .failure(let error):
                    print("Authorization failed: " + error.localizedDescription)
                }
            }
        )
        .frame(width : 310, height:50)
        .cornerRadius(5)
    }
    
    private func login(accessToken: String) {
        let data: [String : String] = ["o_auth_token": accessToken]
        UserService.loginApple(data: data) { result in
            switch result {
            case .success(let responseMessage):
                print("Response: \(responseMessage)")
                if let userData = responseMessage.data {
                    print("User data: \(userData)")
                    // 토큰이 있는 경우 HomeView로 이동
                    if userData.accessToken == nil {
                        // accessToken이 nil인 경우 회원 가입 페이지로 이동
                        
                        // 이동 전 가입 용 데이터 담기
                        signUpData.oAuthToken = accessToken
                        signUpData.email = userData.email
                        signUpData.loginType = .apple
                        
                        // 회원가입으로 이동
                        print("Navigate to the signup page")
                        appState.switchView(to: .signup)
                    } else {
                        // accessToken이 있는 경우 홈 화면으로 이동
                        
                        // 로그인 타입 - 애플
                        signUpData.loginType = .apple
                        
                        // 유저 정보 저장
                        APIManager.shared.userState.user_idx = userData.user_idx
                        APIManager.shared.userState.email = userData.email
                        APIManager.shared.userState.nickname = userData.nickname
                        APIManager.shared.userState.emoji = userData.emoji
                        APIManager.shared.setAccessToken(at: userData.accessToken, rt: userData.refreshToken)
                        
                        // 소켓 연결
                        WebSocketManager.shared.connect()
                        
                        // 홈으로 이동
                        print("Navigate to the home screen")
                        appState.switchView(to: .home)
                    }
                } else {
                    print("No user data received.")
                }
            case .failure(let error):
                print("Error: \(error.localizedDescription)")
            }
        }
    }
}

struct AppleSigninButton_Previews: PreviewProvider {
    static var previews: some View {
        AppleSigninButton()
    }
}
