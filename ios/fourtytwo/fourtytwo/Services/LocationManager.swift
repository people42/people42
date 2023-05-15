import Combine
import CoreLocation

// LocationManager 클래스는 사용자의 현재 위치를 추적하고 관리하는 역할을 하는 클래스입니다.
class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    
    // 싱글톤 인스턴스 생성
    static let shared = LocationManager()

    // CLLocationManager 인스턴스를 생성하여 위치 관련 이벤트를 처리할 수 있도록 합니다.
    private let locationManager = CLLocationManager()
    
    // 현재 위치를 저장하는 @Published 프로퍼티를 선언하여, 위치 업데이트 시 SwiftUI 뷰를 다시 렌더링할 수 있도록 합니다.
    @Published var currentLocation: CLLocation?

    // 초기화 메서드에서 CLLocationManager를 설정하고 위치 업데이트를 시작합니다.
    override init() {
        super.init()
        self.locationManager.delegate = self // 이 클래스의 인스턴스를 locationManager의 delegate로 설정합니다.
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest // 위치 정확도를 최상으로 설정합니다.
        self.locationManager.requestAlwaysAuthorization() // 위치 사용 권한을 항상 요청합니다.
        self.locationManager.allowsBackgroundLocationUpdates = true // 백그라운드에서의 위치 업데이트를 허용합니다.
//        self.locationManager.pausesLocationUpdatesAutomatically = false // 백그라운드 위치 업데이트를 자동으로 중지하지 않게 합니다.
        self.locationManager.startUpdatingLocation() // 위치 업데이트를 시작합니다.
    }

    // 사용자가 위치 서비스에 대한 권한을 변경하면 호출되는 메서드입니다.
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        // 권한 변경 시 처리
    }

    // 위치가 업데이트될 때마다 CoreLocation 프레임워크가 자동으로 locationManager(_:didUpdateLocations:) 메서드를 호출합니다.
    // locationManager.startUpdatingLocation()가 호출되면 이 메서드가 위치가 업데이트될 때마다 호출됩니다.
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        // locations 배열에서 가장 최근의 위치 정보를 가져옵니다.
        guard let location = locations.last else { return }
        
        // 콘솔에 위치 업데이트를 출력합니다.
//        print("Location update: \(location)")
        
        // 메인 스레드에서 위치 정보를 업데이트합니다.
        DispatchQueue.main.async {
            // 최근 위치 정보를 currentLocation 프로퍼티에 저장합니다.
            // 이렇게 하면 SwiftUI 뷰가 해당 위치 정보를 사용하여 업데이트됩니다.
            self.currentLocation = location
        }
    }
}
