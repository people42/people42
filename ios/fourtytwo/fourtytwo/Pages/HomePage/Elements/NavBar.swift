import SwiftUI

struct NavBar: View {
    @Binding var isNewAlert: Bool
    
    var body: some View {
        HStack {
            // 메인로고 추가
            GifImage("animatedLogo_w120")
                .frame(width: 52, height: 40)
                .padding(.leading, 16)
//            Image("MainLogo")
//                .resizable()
//                .frame(width: 52, height: 40)
//                .padding(.leading, 16)
            Spacer()
            
            // 알림 아이콘 추가
//            NavigationLink(destination: AlertView()) {
//                ZStack {
//                    Image(systemName: "bell.fill")
//                        .resizable()
//                        .foregroundColor(.monotoneGray)
//                        .frame(width: 24, height: 24)
//                    if isNewAlert {
//                        Circle()
//                            .foregroundColor(.red)
//                            .frame(width: 10, height: 10)
//                            .offset(x:6, y: -8)
//                    }
//                }
//            }
//            .padding(.trailing, 8)
            
            // 설정 아이콘 추가
            NavigationLink(destination: SystemView()) {
                Image(systemName: "gearshape.fill")
                    .resizable()
                    .foregroundColor(.monotoneGray)
                    .frame(width: 24, height: 24)
            }
            .padding(.trailing, 16)
        }
        .padding(.horizontal, 8)
    }
}

struct NavBar_Previews: PreviewProvider {
    static var previews: some View {
        NavBar(isNewAlert: .constant(true))
    }
}

