import SwiftUI
import MapKit

class UserAnnotation: NSObject, MKAnnotation {
    let id: Int
    let coordinate: CLLocationCoordinate2D
    let title: String?
    let subtitle: String?

    init(id: Int, coordinate: CLLocationCoordinate2D, title: String?, subtitle: String?) {
        self.id = id
        self.coordinate = coordinate
        self.title = title
        self.subtitle = subtitle
    }
}

struct WrappedMap: UIViewRepresentable {
    @Binding var region: MKCoordinateRegion
    @Binding var currentLocation: CLLocation?
    @Binding var nearUsers: [Int: [String: Any]]
    
    var isHeadingActive: Bool
    var heading: CLLocationDirection
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator
        mapView.showsUserLocation = true
        mapView.isRotateEnabled = false
        return mapView
    }
    
    func updateUIView(_ mapView: MKMapView, context: Context) {
        mapView.setRegion(region, animated: true)
        
        // 기존의 모든 annotation 제거
        let allAnnotations = mapView.annotations
        mapView.removeAnnotations(allAnnotations)
        
        // 주변 사용자의 위치를 표시
        for user in nearUsers.values {
            if let latitude = user["latitude"] as? Double,
               let longitude = user["longitude"] as? Double,
               let nickname = user["nickname"] as? String,
               let userID = user["userIdx"] as? Int {
                let userLocation = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
                let userAnnotation = UserAnnotation(id: userID, coordinate: userLocation, title: nickname, subtitle: nil)
                mapView.addAnnotation(userAnnotation)
            }
        }
        
        // 내 위치를 표시
        if let userLocationView = mapView.view(for: mapView.userLocation) {
            if userLocationView.subviews.isEmpty {
                let directionView = UIImageView(image: UIImage(named: "arrow"))
                directionView.contentMode = .center
                directionView.frame = userLocationView.bounds
                userLocationView.addSubview(directionView)
                userLocationView.bringSubviewToFront(directionView)
            }
            
            UIView.animate(withDuration: 1.2) {
                if self.isHeadingActive {
                    let newCamera = MKMapCamera(lookingAtCenter: mapView.userLocation.coordinate, fromDistance: mapView.camera.altitude, pitch: mapView.camera.pitch, heading: self.heading)
                    mapView.setCamera(newCamera, animated: false)
                    userLocationView.subviews.first?.transform = CGAffineTransform.identity
                } else {
                    let newCamera = MKMapCamera(lookingAtCenter: mapView.userLocation.coordinate, fromDistance: mapView.camera.altitude, pitch: mapView.camera.pitch, heading: 0)
                    mapView.setCamera(newCamera, animated: false)
                    userLocationView.subviews.first?.transform = CGAffineTransform(rotationAngle: CGFloat((self.heading) * Double.pi / 180))
                }
            }
        }
    }
    
    class Coordinator: NSObject, MKMapViewDelegate {
        var parent: WrappedMap
        var imageCache: [Int: UIImage] = [:] // 이미지 캐싱
        
        init(_ parent: WrappedMap) {
            self.parent = parent
        }
        
        func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
            guard let annotation = annotation as? UserAnnotation else { return nil }
            
            let identifier = "UserAnnotation"
            var view: MKAnnotationView
            
            if let dequeuedView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier) {
                dequeuedView.annotation = annotation
                view = dequeuedView
            } else {
                view = MKAnnotationView(annotation: annotation, reuseIdentifier: identifier)
            }
            
            let user = parent.nearUsers[annotation.id]
            
            if let user = user {
                if let status = user["status"] as? String, let emoji = user["emoji"] as? String {
                    if status == "writing" {
                        view.image = UIImage(systemName: "ellipsis.bubble")
                    } else {
                        if let cachedImage = imageCache[annotation.id] {
                            view.image = cachedImage
                        } else {
                            if let image = UIImage(named: emoji) {
                                let resizedImage = self.resizeImage(image: image, targetSize: CGSize(width: 50, height: 50))
                                imageCache[annotation.id] = resizedImage
                                view.image = resizedImage
                            }
                        }
                    }
                }
            }
            
            return view
        }
        
        func resizeImage(image: UIImage, targetSize: CGSize) -> UIImage {
            let size = image.size
            let widthRatio  = targetSize.width  / size.width
            let heightRatio = targetSize.height / size.height
            
            var newSize: CGSize
            if(widthRatio > heightRatio) {
                newSize = CGSize(width: size.width * heightRatio, height: size.height * heightRatio)
            } else {
                newSize = CGSize(width: size.width * widthRatio,  height: size.height * widthRatio)
            }
            
            let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
            
            UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
            image.draw(in: rect)
            let newImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            
            return newImage!
        }
    }
    
}
