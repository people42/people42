import SwiftUI

struct AlertView: View {
    @Environment(\.presentationMode) var presentationMode
    
    @State var newNotifications: [NotificationHistory]?

    var body: some View {
        VStack {
            if let newNotifications = newNotifications {
                if newNotifications.count > 0 {
                    ScrollView {
                        VStack {
                            ForEach(newNotifications.indices, id: \.self) { index in
                                AlertCard(notificationHistory: newNotifications[index])
                            }
                        }
                    }
                } else {
                    Spacer()
                    HStack {
                        Spacer()
                        Text("최근 알림이 없어요")
                        Spacer()
                    }
                    Spacer()
                }
            }
        }
        .padding(.horizontal, 8)
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
                if let newNotifications = newNotifications {
                    Text("새로운 알림 \(newNotifications.count)건")
                        .font(.system(size: 18))
                        .fontWeight(.semibold)
                }
            }
        }
        .onAppear {
            getNotiHistory()
        }
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.willEnterForegroundNotification)) { _ in
                    getNotiHistory()
                }
    }
}

extension AlertView {
    func getNotiHistory() {
        NotificationService.getNotificationHistory { result in
            
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    if let newNotis = response.data {
                        DispatchQueue.main.async {
                            self.newNotifications = newNotis
                            print(newNotis)
                        }
                    }
                }
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
}

struct AlertView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            AlertView()
        }
    }
}
