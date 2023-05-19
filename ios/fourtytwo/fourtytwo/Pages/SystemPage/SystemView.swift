import SwiftUI
import Alamofire
import WebKit
import AuthenticationServices


// 환경 변수를 참조하는 함수를 따로 모아둠
extension SystemView {
    
    func logout() {
        
        UserService.logout() { result in
            switch result {
            case .success(_):
                print("서버 토큰 삭제")
            case .failure(let error):
                print("Error: \(error.localizedDescription)")
            }
        }
        
        userState.accessToken = ""
        userState.refreshToken = ""
        userState.user_idx = nil
        userState.email = nil
        userState.nickname = nil
        userState.emoji = nil
        // 로그인 화면으로 이동
        appState.currentView = .login
        // 로그인 타입 - 논
        signUpState.loginType = .none

    }
    
    func withdrawal() {
        
        switch signUpState.loginType {
        case .google:
            print("구글탈퇴")
            withdrawalDefault()
        case .apple:
            print("애플탈퇴")
            startWithdrawalWithAppleFlow()
        case .none:
            print("로그인정보없음")
            break
        }
        
    }

    func withdrawalDefault() {
        
        UserService.withdrawal() { result in
            switch result {
            case .success(_):
                logout()
            case .failure(let error):
                print("Error: \(error.localizedDescription)")
            }
        }
        
    }
    
    func withdrawalApple(code: String) {
        
        let data = ["appleCode": code]
        UserService.withdrawalApple(data: data) { result in
            switch result {
            case .success(_):
                logout()
            case .failure(let error):
                print("Error: \(error.localizedDescription)")
            }
        }
        
    }
    
    private func startWithdrawalWithAppleFlow() {
        
        let request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = [.fullName, .email]

        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        authorizationController.delegate = appleSignInCoordinator
        authorizationController.presentationContextProvider = appleSignInCoordinator
        appleSignInCoordinator.completionHandler = { result in
            switch result {
            case .success(let authCode):
                print(authCode)
                DispatchQueue.main.async {
                    self.withdrawalApple(code: authCode)
                }
            case .failure(let error):
                print("Error: \(error.localizedDescription)")
            }
        }
        authorizationController.performRequests()
        
    }
    
    private func contact() {
        let email = "qorzi00@gmail.com"
        let subject = "사이 서비스 문의"
        let body = "이메일 : 회원가입한 이메일을 적어주세요.\n문의 내용 : "
        let encodedSubject = subject.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        let encodedBody = body.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        let url = URL(string: "mailto:\(email)?subject=\(encodedSubject)&body=\(encodedBody)")!
        UIApplication.shared.open(url)
    }
    
}

struct SystemView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var userState: UserState
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var signUpState: SignUpState
    
    @State var showingWithdrawalAlert = false
    @State var showingContactAlert = false
    
    var appleSignInCoordinator = AppleSignInCoordinator()
    
    var body: some View {
        VStack {
            VStack(alignment: .leading, spacing: 32) {
                NavLink(title: "닉네임 변경", page: NicknameChangeView())
                
                NavLink(title: "이모지 변경", page: EmojiSwapView())
                
//                NavLink(title: "알림 및 소리", page: Text("알림 및 소리"))
                
//                NavLink(title: "차단 사용자 관리", page: Text("차단 사용자 관리"))
                
                NavLink(title: "이용약관 및 개인정보처리방침", page: PolicyWebView())
                
                Button(action: {
                    // 문의하기 알림창
                    showingContactAlert = true
                }) {
                    HStack {
                        Text("문의하기")
                            .foregroundColor(Color("Text"))
                        Spacer()
                    }
                }
                .alert(isPresented: $showingContactAlert) {
                    Alert(title: Text("아래 이메일로 문의해주세요."),
                          message: Text("qorzi00@gmail.com\n문의하기를 누르시면 이메일 작성으로 넘어갑니다."),
                          primaryButton: .default(Text("문의하기")) {
                            contact()
                          },
                          secondaryButton: .cancel(Text("취소"))
                    )
                }
                
                Button(action: {
                    // 로그아웃 버튼 액션
                    logout()
                }) {
                    HStack {
                        Text("로그아웃")
                            .foregroundColor(Color("Text"))
                        Spacer()
                    }
                }
                
                Button(action: {
                    // 회원 탈퇴 알림창
                    showingWithdrawalAlert = true
                }) {
                    HStack {
                        Text("회원 탈퇴")
                            .foregroundColor(.red)
                        Spacer()
                    }
                }
                .alert(isPresented: $showingWithdrawalAlert) {
                    Alert(title: Text("회원 탈퇴 확인"),
                          message: Text("정말 탈퇴하시겠습니까?"),
                          primaryButton: .destructive(Text("탈퇴")) {
                            withdrawal()
                          },
                          secondaryButton: .cancel(Text("취소"))
                    )
                }
                
            }
            .padding(.leading, 32)
            Spacer()
            
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
                Text("설정")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }

    }
}

class AppleSignInCoordinator: NSObject, ASAuthorizationControllerDelegate, ASAuthorizationControllerPresentationContextProviding {
    var window: UIWindow?
    
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return self.window ?? UIWindow()
    }
    
    var completionHandler: ((Result<String, Error>) -> Void)?

    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
            if let authCodeData = appleIDCredential.authorizationCode,
               let authCode = String(data: authCodeData, encoding: .utf8) {
                completionHandler?(.success(authCode))
            } else {
                completionHandler?(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: "Authorization code not found"])))
            }
        }
    }

    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        completionHandler?(.failure(error))
    }
}

struct SystemView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            SystemView()
        }
    }
}
