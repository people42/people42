import SwiftUI

struct ContentView: View {
    @EnvironmentObject var appState: AppState
    
    var body: some View {
        NavigationView {
            VStack {
                if appState.currentView == .login {
                    LoginView()
                        .transition(.move(edge: .bottom)) // 아래에서 올라오는 애니메이션 적용
                } else if appState.currentView == .home {
                    HomeView()
                        .transition(.move(edge: .bottom)) // 아래에서 올라오는 애니메이션 적용
                } else if appState.currentView == .signup {
                    SignupView()
                        .transition(.move(edge: .bottom)) // 아래에서 올라오는 애니메이션 적용
                }
            }
            .navigationBarHidden(true)
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView().environmentObject(AppState())
    }
}

