import SwiftUI
import MapKit
import CoreLocation


struct PlaceMapView: View {
    @ObservedObject var viewModel: PlaceViewModel
    
    @State private var metersPerCircle: Double = 100
    @State var location: CLLocationCoordinate2D
    

    var body: some View {
        ZStack {
            map
            compass
            circles
            profileImages
        }
    }

    private var map: some View {
        Map(coordinateRegion: .constant(MKCoordinateRegion(center: location, latitudinalMeters: 500, longitudinalMeters: 500)), interactionModes: [], showsUserLocation: false)
            .frame(height: 300)
    }
    
    private var compass: some View {
        VStack {
            Image("circle")
        }
    }

    private var circles: some View {
        let minOpacity: Double = 0.2
        let maxOpacity: Double = 0.2
        let numberOfCircles = 2

        return ForEach(0..<numberOfCircles) { index in
            Circle()
                .stroke(Color.gray.opacity(minOpacity + (maxOpacity - minOpacity) * Double(index) / Double(numberOfCircles - 1)), lineWidth: 1)
                .frame(width: CGFloat(metersPerCircle * Double(index + 1) * 2), height: CGFloat(metersPerCircle * Double(index + 1) * 2))
                .scaledToFit()
                .frame(height: 300)
                .mask(
                    Rectangle()
                        .frame(width: UIScreen.main.bounds.width, height: 300)
                )
        }
    }
    
    private var profileImages: some View {
        GeometryReader { geometry in
            ForEach(viewModel.messageInfoList.indices, id: \.self) { index in
                let randomPosition = randomPositionOnScreen(geometry: geometry)
                GifUIkit(viewModel.messageInfoList[index].profileImage)
                    .frame(width: 40, height: 40)
                    .scaleEffect(0.5)
                    .position(x: randomPosition.x, y: randomPosition.y)
            }
        }
        .frame(height: 300)
    }

    private func randomPositionOnScreen(geometry: GeometryProxy) -> CGPoint {
        let padding: CGFloat = 40
        let centerX = geometry.size.width / 2
        let centerY = geometry.size.height / 2
        let avoidCenterWidth: CGFloat = 80
        let avoidCenterHeight: CGFloat = 80
        
        var randomX = CGFloat.random(in: padding..<geometry.size.width - padding)
        var randomY = CGFloat.random(in: padding..<geometry.size.height - padding)
        
        while abs(randomX - centerX) < avoidCenterWidth / 2 && abs(randomY - centerY) < avoidCenterHeight / 2 {
            randomX = CGFloat.random(in: padding..<geometry.size.width - padding)
            randomY = CGFloat.random(in: padding..<geometry.size.height - padding)
        }
        
        return CGPoint(x: randomX, y: randomY)
    }

}


