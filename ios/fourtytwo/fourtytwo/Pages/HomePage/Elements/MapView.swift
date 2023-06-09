import SwiftUI
import MapKit
import CoreLocation

@MainActor
class MapManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published var region: MKCoordinateRegion
    @Published var currentLocation: CLLocation? // 위치
    @Published var heading: Double = 0.0 // 헤딩
    
    private let locationManager = CLLocationManager()

    override init() {
        region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 36.355352726497806, longitude: 127.29817332461586),
            latitudinalMeters: 500, longitudinalMeters: 500
        )
        super.init()
        locationManager.delegate = self
        locationManager.distanceFilter = 10 // 위치가 10미터 이상 변경되면 업데이트

        Task {
            
            await MainActor.run {
                // 위치
                locationManager.requestWhenInUseAuthorization()
                locationManager.startUpdatingLocation()
                // 헤딩
                locationManager.desiredAccuracy = kCLLocationAccuracyBest
                locationManager.startUpdatingHeading()
            }
        }
    }

    // 위치 업데이트 - nonisolated 붙히면 안됨
    nonisolated func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }

        Task {
            await MainActor.run {
                print("위치 업데이트!")
                self.currentLocation = location
                self.region = MKCoordinateRegion(
                    center: CLLocationCoordinate2D(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude),
                    latitudinalMeters: 500, longitudinalMeters: 500
                )
            }
        }
    }

    // 해딩 업데이트 - nonisolated 붙히면 안됨
    nonisolated func locationManager(_ manager: CLLocationManager, didUpdateHeading newHeading: CLHeading) {
        Task {
            await MainActor.run {
                self.heading = newHeading.trueHeading
            }
        }
    }

}


struct MapView: View {
    @StateObject private var locationManager = MapManager()
    @ObservedObject var webSocketManager = WebSocketManager.shared
    @State private var showToast: Bool = false
    @State private var toastMessage: String = ""
    @State private var previousUserIds: Set<Int> = []
    @State private var selectedUser: CustomPointAnnotation? = nil

    var body: some View {
        ZStack {
            Map(coordinateRegion: $locationManager.region, interactionModes: [.pan, .zoom], showsUserLocation: true, annotationItems: annotationData) { nearUser in
                MapAnnotation(coordinate: nearUser.coordinate) {
                    VStack {
                        MessageView(status: nearUser.status, nickname: nearUser.nickname, message: nearUser.message, selectedUser: $selectedUser)

                        GifImage(nearUser.emoji)
                            .frame(width: 30, height: 30)
                            .scaleEffect(self.showToast ? 0.5 : 1.0)
                            .animation(.spring(response: 0.5, dampingFraction: 0.5, blendDuration: 0.5))
                            .onTapGesture {
                                self.selectedUser = nearUser
                            }
                    }
                    .frame(height: 60)
                }
            }
            
            
            if showToast {
                Text(toastMessage)
                    .font(.customCaption)
                    .padding(4)
                    .background(Color.black.opacity(0.3))
                    .foregroundColor(Color("Text"))
                    .cornerRadius(10)
                    .transition(.slide)
                    .onAppear(perform: {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            withAnimation {
                                showToast = false
                            }
                        }
                    })
                    .offset(y: -200)
            }
            
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    Button(action: {
                        withAnimation {
                            guard let location = locationManager.currentLocation else { return }
                            locationManager.region = MKCoordinateRegion(
                                center: CLLocationCoordinate2D(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude),
                                latitudinalMeters: 500, longitudinalMeters: 500
                            )
                        }
                    }) {
                        Image(systemName: "location.fill")
                            .foregroundColor(.white)
                            .padding()
                            .background(Color.blue)
                            .clipShape(Circle())
                            .padding()
                    }
                }
                Spacer()
                    .frame(height: 60)
            }
        }
        .onChange(of: Set(webSocketManager.nearUsers.keys), perform: { newValue in
            let oldUserSet = previousUserIds
            let newUserSet = newValue
//            if let removedUser = oldUserSet.subtracting(newUserSet).first,
//               let nickname = webSocketManager.nearUsers[removedUser]?["nickname"] as? String {
//                showToast = true
//                toastMessage = "\(nickname) 님이 멀어졌어요."
//            }
            if let addedUser = newUserSet.subtracting(oldUserSet).first,
               let nickname = webSocketManager.nearUsers[addedUser]?["nickname"] as? String {
                showToast = true
                toastMessage = "\(nickname) 님이 근처에 나타났어요."
            }
            previousUserIds = newValue
        })
    }
    
    private var annotationData: [CustomPointAnnotation] {
        webSocketManager.nearUsers.compactMap { (id, data) in
            guard let latitude = data["latitude"] as? CLLocationDegrees,
                  let longitude = data["longitude"] as? CLLocationDegrees,
                  let nickname = data["nickname"] as? String,
                  let emoji = data["emoji"] as? String,
                  let status = data["status"] as? String,
                  let message = data["message"] as? String else {
                return nil
            }

            return CustomPointAnnotation(
                id: id,
                coordinate: CLLocationCoordinate2D(latitude: latitude, longitude: longitude),
                nickname: nickname,
                emoji: emoji,
                status: status,
                message: message
            )
        }
    }
}


struct CustomPointAnnotation: Identifiable {
    var id: Int
    var coordinate: CLLocationCoordinate2D
    var nickname: String
    var emoji: String
    var status: String
    var message: String
}

struct MessageView: View {
    var status: String
    var nickname: String
    var message: String
    @State private var previousMessage: String = ""
    @Binding var selectedUser: CustomPointAnnotation?

    var body: some View {
        let displayMessage = message.count > 20 ? String(message.prefix(20)) + "..." : message
        
        if status == "writing" {
            Text("...")
                .font(.system(size: 12))
                .foregroundColor(.white)
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(RoundedRectangle(cornerRadius: 32).foregroundColor(Color.black).opacity(0.5))
        } else if message != previousMessage && message != "" {
            VStack(alignment: .leading) {
                Text(nickname)
                    .font(.system(size: 10, weight: .black))
                    .foregroundColor(.white)
                Text(displayMessage)
                    .font(.system(size: 12))
                    .foregroundColor(.white)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(RoundedRectangle(cornerRadius: 8).foregroundColor(Color.black).opacity(0.5))
            .onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
                    self.previousMessage = message
                }
            }
            
        } else if let selectedUserNick = selectedUser?.nickname, selectedUserNick == nickname {
            VStack(alignment: .leading) {
                Text(nickname)
                    .font(.system(size: 10, weight: .black))
                    .foregroundColor(.white)
                Text(displayMessage)
                    .font(.system(size: 12))
                    .foregroundColor(.white)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(RoundedRectangle(cornerRadius: 8).foregroundColor(Color.black).opacity(0.5))
            .onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                    self.selectedUser = nil
                }
            }
            
        } else {
            Spacer()
                .frame(height: 42)
        }
    }
}

