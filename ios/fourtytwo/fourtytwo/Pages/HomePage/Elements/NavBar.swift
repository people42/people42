import SwiftUI

struct NavBar: View {
    @State private var newNotificationCnt: Int = 0
    
    private var isNewAlert: Bool {
        return newNotificationCnt >= 1
    }
    
    var body: some View {
        HStack {
            // 메인로고 추가
//            GifImage("animatedLogo_w120")
//                .frame(width: 52, height: 40)
//                .padding(.leading, 16)
            
            // 스틸컷 로고
            Image("MainLogo")
                .resizable()
                .frame(width: 52, height: 40)
                .padding(.leading, 16)
            Spacer()
            
            // 알림 아이콘 추가
            NavigationLink(destination: AlertView()) {
                ZStack {
                    Image(systemName: "bell.fill")
                        .resizable()
                        .foregroundColor(.monotoneGray)
                        .frame(width: 24, height: 24)
                    if isNewAlert {
                        Circle()
                            .foregroundColor(.red)
                            .frame(width: 10, height: 10)
                            .offset(x:6, y: -8)
                    }
                }
            }
            .padding(.trailing, 16)
            
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
        .onAppear {
            getNewNotification()
        }
    }
}

extension NavBar {
    private func getNewNotification() {
        NotificationService.getNewNotification { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    if let newNoti = response.data {
                        DispatchQueue.main.async {
                            self.newNotificationCnt = newNoti.notificationCnt
                        }
                    }
                }
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
}

struct NavBar_Previews: PreviewProvider {
    static var previews: some View {
        NavBar()
    }
}

