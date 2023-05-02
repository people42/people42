import SwiftUI
import Alamofire
import WebKit
import AuthenticationServices

// 뷰모델
class SystemViewModel: ObservableObject {
    @Published var showingWithdrawalAlert = false
}

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
            startSignInWithAppleFlow()
        case .none:
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
    
    private func startSignInWithAppleFlow() {
        
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

    
}

struct SystemView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var userState: UserState
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var signUpState: SignUpState
    
    @StateObject var viewModel = SystemViewModel()
    
    var appleSignInCoordinator = AppleSignInCoordinator()
    
    var body: some View {
        VStack {
            VStack(alignment: .leading, spacing: 32) {
                NavLink(title: "이모지 변경", page: EmojiSwapView())
                
//                NavLink(title: "알림 및 소리", page: Text("알림 및 소리"))
                
//                NavLink(title: "차단 사용자 관리", page: Text("차단 사용자 관리"))
                
                NavLink(title: "이용약관 및 개인정보처리방침", page: PolicyWebView())
                
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
                    viewModel.showingWithdrawalAlert = true
                }) {
                    HStack {
                        Text("회원 탈퇴")
                            .foregroundColor(.red)
                        Spacer()
                    }
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
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(Color("Text"))
                }
            }
            ToolbarItem(placement: .principal) {
                Text("설정")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }
        .alert(isPresented: $viewModel.showingWithdrawalAlert) {
            Alert(title: Text("회원 탈퇴 확인"),
                  message: Text("정말 탈퇴하시겠습니까?"),
                  primaryButton: .destructive(Text("탈퇴")) {
                    withdrawal()
                  },
                  secondaryButton: .cancel(Text("취소"))
            )
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
