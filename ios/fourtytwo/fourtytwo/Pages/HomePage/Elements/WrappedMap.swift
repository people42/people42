import SwiftUI
import MapKit
import CoreLocation

class CustomPointAnnotation: MKPointAnnotation {
    var emoji: String?
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

    class Coordinator: NSObject, MKMapViewDelegate, CLLocationManagerDelegate {
        var parent: WrappedMap

        init(_ parent: WrappedMap) {
            self.parent = parent
        }

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

                view.image = nil
                return view
            } else if let pointAnnotation = annotation as? CustomPointAnnotation {
                let identifier = "PointAnnotation"
                var view: MKAnnotationView

                if let dequeuedView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier) {
                    dequeuedView.annotation = pointAnnotation
                    view = dequeuedView
                } else {
                    view = MKAnnotationView(annotation: pointAnnotation, reuseIdentifier: identifier)
                }

                if let emoji = pointAnnotation.emoji {
                    let imageView = UIImageView(image: UIImage(named: emoji))
                    imageView.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
                    imageView.contentMode = .scaleAspectFit
                    view.addSubview(imageView)
                }

                view.canShowCallout = true
                view.rightCalloutAccessoryView = UIButton(type: .detailDisclosure)

                return view
            }

            return nil
        }


        func mapView(_ mapView: MKMapView, didUpdate userLocation: MKUserLocation) {
            let nearUsers = parent.nearUsers

            // remove existing annotations except for user location
            mapView.annotations.filter { $0 !== userLocation && !($0 is MKUserLocation) }
                .forEach { mapView.removeAnnotation($0) }

            // add new annotations for near users
            for (_, nearUser) in nearUsers {
                guard let latitude = nearUser["latitude"] as? CLLocationDegrees,
                      let longitude = nearUser["longitude"] as? CLLocationDegrees,
                      let nickname = nearUser["nickname"] as? String,
                      let emoji = nearUser["emoji"] as? String else {
                    continue
                }

                let annotation = CustomPointAnnotation()
                annotation.coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
                annotation.title = nickname
                annotation.emoji = emoji
                mapView.addAnnotation(annotation)
            }

        }

    }

}
