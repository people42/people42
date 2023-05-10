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
            latitudinalMeters: 1000, longitudinalMeters: 1000
        )
        super.init()
        locationManager.delegate = self
        locationManager.distanceFilter = 5 // 위치가 5미터 이상 변경되면 업데이트

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
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
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
    func locationManager(_ manager: CLLocationManager, didUpdateHeading newHeading: CLHeading) {
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

    var body: some View {
        Map(coordinateRegion: $locationManager.region, interactionModes: [], showsUserLocation: true, annotationItems: annotationData) { nearUser in
            MapAnnotation(coordinate: nearUser.coordinate) {
                VStack {
                    if let status = nearUser.status {
                        MessageView(status: status, message: nearUser.message)
                        
                        if let emoji = nearUser.emoji {
                            GifImage(emoji)
                                .frame(width: 30, height: 30)
                        }
                    }
                }
                .frame(height: 60)
            }
        }
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
                title: nickname,
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
    var title: String
    var emoji: String
    var status: String
    var message: String
}

struct MessageView: View {
    var status: String
    var message: String
    @State private var previousMessage: String = ""

    var body: some View {
        if status == "writing" {
            Text("...")
                .font(.system(size: 12))
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(RoundedRectangle(cornerRadius: 32).foregroundColor(Color.black).opacity(0.3))
        } else if message != previousMessage && message != "" {
            Text(message)
                .font(.system(size: 12))
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(RoundedRectangle(cornerRadius: 32).foregroundColor(Color.black).opacity(0.3))
                .onAppear {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
                        self.previousMessage = message
                    }
                }
        } else {
            Spacer()
                .frame(height: 30)
        }
    }
}

