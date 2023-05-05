import SwiftUI
import MapKit
import CoreLocation

class MapManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published var region: MKCoordinateRegion
    @Published var currentLocation: CLLocation? // 위치
    @Published var heading: Double = 0.0 // 헤딩
    
    private let locationManager = CLLocationManager()

    override init() {
        region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 36.355352726497806, longitude: 127.29817332461586),
            latitudinalMeters: 1000, longitudinalMeters: 1000
//        span: MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01) // 배율
        )
        super.init()
        locationManager.delegate = self
        // 위치
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
        // 헤딩
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingHeading()
    }

    // 위치 업데이트
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        self.currentLocation = location
        DispatchQueue.main.async {
            self.region = MKCoordinateRegion(
                center: CLLocationCoordinate2D(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude),
                latitudinalMeters: 500, longitudinalMeters: 500
            )
        }
    }
    
    // 헤딩 업데이트
    func locationManager(_ manager: CLLocationManager, didUpdateHeading newHeading: CLHeading) {
        DispatchQueue.main.async {
            self.heading = newHeading.trueHeading
        }
    }
}

struct MapView: View {
    @StateObject private var locationManager = MapManager()

    @State private var metersPerCircle: Double = 100
    @State private var isHeadingActive = false

    var body: some View {
        ZStack {
            WrappedMap(region: $locationManager.region, currentLocation: $locationManager.currentLocation, isHeadingActive: isHeadingActive, heading: locationManager.heading)
                .clipShape(Circle())
                .frame(height: 480)
                .overlay(
                    RadialGradient(gradient: Gradient(colors: [
                        Color.clear, Color("BgPrimary").opacity(0), Color("BgPrimary").opacity(1)]), center: .center, startRadius: 100, endRadius: 190)
                        .clipShape(Circle())
                        .frame(height: 480)
                )
                .scaleEffect(CGSize(width: 1.1, height: 1.1))
                .overlay(northArrow.opacity(isHeadingActive ? 0 : 1), alignment: .top)
            
            circles
            
            modeIndicator
                .padding(.top, 40)
                .padding(.trailing, 20)
                .frame(height: 480)

        }
        .onTapGesture {
            withAnimation(.easeInOut(duration: 1.2)) {
                isHeadingActive.toggle()
            }
        }
    }

    private var northArrow: some View {
        Text("N")
            .font(.system(size: 24, weight: .bold))
            .foregroundColor(Color.red.opacity(0.9))
            .offset(x: 0, y: 40) // 상단에 위치하도록 조정
    }
    
    private var circles: some View {
        let minOpacity: Double = 0.2
        let maxOpacity: Double = 0.1
        let numberOfCircles = 2

        // swiftlint:disable:next
        return ForEach(0..<numberOfCircles) { index in
            Circle()
                .stroke(Color.gray.opacity(minOpacity + (maxOpacity - minOpacity) * Double(index) / Double(numberOfCircles - 1)), lineWidth: 1)
                .frame(width: CGFloat(metersPerCircle * Double(index + 1) * 2), height: CGFloat(metersPerCircle * Double(index + 1) * 2))
                .scaledToFit()
        }
    }
    
    private var modeIndicator: some View {
        VStack {
            HStack {
                Spacer()
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(isHeadingActive ? Color.green : Color.gray, lineWidth: 2)
                        .frame(width: 48, height: 30)

                    Text(isHeadingActive ? "ON" : "OFF")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(isHeadingActive ? .green : .gray)
                }
            }
            Spacer()
        }
    }
    
}

struct MapView_Previews: PreviewProvider {
    static var previews: some View {
        let mapManager = MapManager()
        mapManager.region = MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: 36.355352726497806, longitude: 127.29817332461586),
            latitudinalMeters: 1000, longitudinalMeters: 1000
        )
        return MapView().environmentObject(mapManager)
            .background(Color("BgPrimary"))
    }
}

