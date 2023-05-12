import SwiftUI

struct LoginView: View {
    var body: some View {
        ZStack {
            Image("BgLogoBlue")
                .resizable()
                .scaledToFit()
                .edgesIgnoringSafeArea(.all)
                .offset(y: -100)
            
            VStack(spacing: 16) {
                Text("로그인 또는 회원가입")
                    .font(.customSubtitle2)
                GoogleSigninButton()
                AppleSigninButton()
            }
            .frame(height: UIScreen.main.bounds.height)
            .offset(y: 240)
        }
        .background(Color.backgroundPrimary.edgesIgnoringSafeArea(.all))
    
    }
    
}



struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
