import SwiftUI

struct OverView: View {
    @Environment(\.colorScheme) var colorScheme

    @State private var metersPerCircle: Double = 100
    

    var body: some View {
        ZStack {
            MapView()
//                .overlay(
//                    RadialGradient(gradient: Gradient(colors: [
//                        Color.clear, Color("BgPrimary").opacity(0), Color("BgPrimary").opacity(0.3), Color.clear]),
//                        center: .center,
//                        startRadius: 100,
//                        endRadius: 190)
//                    .clipShape(Circle())
//                    .frame(height: 480)
//                )

//            .scaleEffect(CGSize(width: 1.1, height: 1.1))
            
            circles
        
        }

    }
    
    private var circles: some View {
        let minOpacity: Double = 0.2
        let maxOpacity: Double = 0.1
        let numberOfCircles = 2

        return ForEach(0..<numberOfCircles) { index in
            Circle()
                .stroke(Color.gray.opacity(minOpacity + (maxOpacity - minOpacity) * Double(index) / Double(numberOfCircles - 1)), lineWidth: 1)
                .frame(width: CGFloat(metersPerCircle * Double(index + 1) * 2), height: CGFloat(metersPerCircle * Double(index + 1) * 2))
                .scaledToFit()
        }
    }
    
    
}

