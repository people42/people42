import SwiftUI
import MapKit
import CoreLocation


struct PlaceMapView: View {
    @ObservedObject var viewModel: PlaceViewModel
    
    @State private var metersPerCircle: Double = 100
    @State var location: CLLocationCoordinate2D
    @Binding var toggleHeight: CGFloat
    
    @State private var existingPositions: [CGPoint] = []
    

    var body: some View {
        ZStack {
            map
            compass
            circles
            profileImages
        }
    }

    private var map: some View {
        Map(coordinateRegion: .constant(MKCoordinateRegion(center: location, latitudinalMeters: 300, longitudinalMeters: 500)), interactionModes: [], showsUserLocation: false)
            .frame(height: toggleHeight)
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
                .frame(height: toggleHeight)
                .mask(
                    Rectangle()
                        .frame(width: UIScreen.main.bounds.width, height: toggleHeight)
                )
        }
    }
    
    private var profileImages: some View {
        GeometryReader { geometry in
            ForEach(viewModel.messageInfoList.indices, id: \.self) { index in
                let randomPosition = randomPositionOnScreen(geometry: geometry, existingPositions: existingPositions)
                GifImage(viewModel.messageInfoList[index].profileImage)
                    .frame(width: 32, height: 32)
                    .position(randomPosition)
                    .onAppear {
                        existingPositions.append(randomPosition)
                    }
            }
        }
        .frame(height: toggleHeight)
    }

    private func randomPositionOnScreen(geometry: GeometryProxy, existingPositions: [CGPoint], minimumDistance: CGFloat = 16) -> CGPoint {
        var newPosition: CGPoint
        let padding: CGFloat = 10
        
        repeat {
            
            let randomX = CGFloat.random(in: padding..<geometry.size.width - padding)
            let randomY = CGFloat.random(in: padding..<geometry.size.height - padding)
            newPosition = CGPoint(x: randomX, y: randomY)
            
        } while existingPositions.contains(where: { distanceBetween($0, newPosition) < minimumDistance })
        
        return newPosition
    }

    private func distanceBetween(_ point1: CGPoint, _ point2: CGPoint) -> CGFloat {
        return hypot(point1.x - point2.x, point1.y - point2.y)  // 빗변 계산
    }

}


