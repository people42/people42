import Combine
import CoreLocation

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private let locationManager = CLLocationManager()
    
    @Published var currentLocation: CLLocation?

    override init() {
        super.init()
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestAlwaysAuthorization()
        self.locationManager.startUpdatingLocation()
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        // 권한 변경 시 처리
    }

    // locationManager.startUpdatingLocation()가 호출되면 위치가 업데이트될 때마다 CoreLocation 프레임워크가 자동으로 locationManager(_:didUpdateLocations:) 메서드를 호출
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        self.currentLocation = location
    }
}
