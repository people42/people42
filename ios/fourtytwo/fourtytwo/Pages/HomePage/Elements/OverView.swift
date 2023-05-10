import SwiftUI
import MapKit

struct OverView: View {
    @Binding var region: MKCoordinateRegion
    @Binding var nearUsers: [Int: [String: Any]]

    var body: some View {
        Map(coordinateRegion: $region, interactionModes: [], showsUserLocation: true, annotationItems: annotationData) { nearUser in
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
        nearUsers.compactMap { (id, data) in
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

