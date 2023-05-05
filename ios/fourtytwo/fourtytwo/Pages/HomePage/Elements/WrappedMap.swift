import SwiftUI
import MapKit
import CoreLocation

struct WrappedMap: UIViewRepresentable {
    @Binding var region: MKCoordinateRegion // 지도 영역 바인딩
    @Binding var currentLocation: CLLocation? // 사용자 현재 위치 바인딩

    var isHeadingActive: Bool // 방향 활성화 상태
    var heading: CLLocationDirection // 사용자의 현재 방향

    // Coordinator 생성
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    // MKMapView 생성 및 초기 설정
    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator
        mapView.showsUserLocation = true
        return mapView
    }

    // 지도 업데이트 및 화살표 이미지 설정
    func updateUIView(_ mapView: MKMapView, context: Context) {
        mapView.setRegion(region, animated: true) // 지도 영역 설정

        if let userLocationView = mapView.view(for: mapView.userLocation) {
            if userLocationView.subviews.isEmpty {
                let directionView = UIImageView(image: UIImage(named: "arrow"))
                directionView.contentMode = .center
                directionView.frame = userLocationView.bounds
                userLocationView.addSubview(directionView)
                userLocationView.bringSubviewToFront(directionView)
            }

            // 화살표 이미지 회전 업데이트
            UIView.animate(withDuration: 1.2) {
                userLocationView.subviews.first?.transform = CGAffineTransform(rotationAngle: CGFloat((heading) * Double.pi / 180))
            }
        }
    }

    // MKMapViewDelegate를 구현하는 Coordinator 클래스
    class Coordinator: NSObject, MKMapViewDelegate {
        var parent: WrappedMap

        init(_ parent: WrappedMap) {
            self.parent = parent
        }

        // 사용자 위치 어노테이션 뷰 설정 및 기본 아이콘 제거
        func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
            if annotation is MKUserLocation {
                let identifier = "UserLocation"
                var view: MKAnnotationView

                if let dequeuedView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier) {
                    dequeuedView.annotation = annotation
                    view = dequeuedView
                } else {
                    view = MKAnnotationView(annotation: annotation, reuseIdentifier: identifier)
                }

                view.image = nil // 기본 제공되는 사용자 위치 아이콘 제거
                return view
            }

            return nil
        }
    }
}
