import SwiftUI
import GoogleSignInSwift
import GoogleSignIn
import Alamofire

struct GoogleSigninButton : View {
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var signUpData: SignUpState
    
    var body: some View{
        Button(action: handleSignInButton) {
                    HStack {
                        Spacer()
                        Image("GoogleIcon")
                            .resizable()
                            .frame(width: 30, height: 30)
                        Text("Sign in with Google")
                            .foregroundColor(.black)
                            .font(Font.custom("Roboto-Medium", size: 18))
                        Spacer()
                    }
                    .padding()
                    .background(Color.white)
                    
        }
        .frame(width : 310, height:50)
        .cornerRadius(5)
        .shadow(radius: 2, x: 0, y: 2)
    }
    
    let signIn = GIDSignIn.sharedInstance

    func handleSignInButton() {
        guard let rootViewController = UIApplication.shared.windows.first?.rootViewController else { return }
        
        signIn.signIn(withPresenting: rootViewController) { signInResult, error in
            if let error = error {
                print("Google Sign-In failed with error: \(error.localizedDescription)")
                return
            }
            
            guard let result = signInResult else {
                print("Google Sign-In failed with unknown error.")
                return
            }
            
            // If sign in succeeded, display the app's main content View.
            print("Google Sign-In succeeded with result: \(result)")
    //        print(result.user)
            let user = result.user
    //        print(user.profile?.email)
    //        print(user.profile?.name)
    //        print(user.profile?.givenName)
    //        print(user.profile?.familyName)
    //        print(user.idToken)
//            print(user.accessToken)
            let accessToken = user.accessToken.tokenString
//            print(accessToken)
            let data: [String : String] = ["o_auth_token": accessToken]
            UserService.loginGoogle(data: data) { result in
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
                            signUpData.loginType = .google
                            
                            // 회원가입으로 이동
                            print("Navigate to the signup page")
                            appState.switchView(to: .signup)
                        } else {
                            // accessToken이 있는 경우 홈 화면으로 이동
                            
                            // 로그인 타입 - 구글
                            signUpData.loginType = .google
                            
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
}


struct GoogleSigninButton_Previews: PreviewProvider {
    static var previews: some View {
        GoogleSigninButton()
    }
}
