import SwiftUI

struct SplashView: View {
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var userState: UserState
    
    @State private var isActive = false

    var body: some View {
        VStack {
            Image("Splash")
                .resizable()
                .scaledToFill()
                .onAppear {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        withAnimation {
                            isActive = true
                            if userState.user_idx != nil {
                                print("로그인 되어있음")
                                appState.currentView = .home
                            }
                        }
                    }
                }
        }
        .edgesIgnoringSafeArea(.all)
        .background(Color("SplashBgColor"))
        .navigationBarHidden(true)
        .fullScreenCover(isPresented: $isActive, content: {
            ContentView().environmentObject(appState)
        })
    }
}

struct SplashView_Previews: PreviewProvider {
    static var previews: some View {
        SplashView().environmentObject(AppState())
    }
}
