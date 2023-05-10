import SwiftUI

struct OverView: View {
    @Environment(\.colorScheme) var colorScheme
    
    
    
    // WebSocketManager의 isConnected와 바인딩된 @State 변수
    @State private var isConnect: Bool = WebSocketManager.shared.isConnected

    @State private var metersPerCircle: Double = 100
    

    var body: some View {
        ZStack {
            MapView()
                .clipShape(Circle())
                .frame(height: 480)
                .overlay(
                    RadialGradient(gradient: Gradient(colors: [
                        Color.clear, Color("BgPrimary").opacity(0), Color("BgPrimary").opacity(1)]), center: .center, startRadius: 100, endRadius: 190)
                        .clipShape(Circle())
                        .frame(height: 480)
                )
                .scaleEffect(CGSize(width: 1.1, height: 1.1))
                .overlay(northArrow.opacity(isConnect ? 0 : 1), alignment: .top)
            
            circles
            
            modeIndicator
                .padding(.top, 40)
                .padding(.trailing, 20)
                .frame(height: 480)

        }
        .onTapGesture {
            withAnimation(.easeInOut(duration: 1.2)) {
                isConnect.toggle()
                if isConnect {
                    WebSocketManager.shared.connect()
                } else {
                    WebSocketManager.shared.disconnect()
                }
            }
        }
    }

    private var northArrow: some View {
        Text("N")
            .font(.system(size: 24, weight: .bold))
            .foregroundColor(colorScheme == .dark ? .white.opacity(0.8) : .red.opacity(0.8))
            .offset(x: 0, y: 40) // 상단에 위치하도록 조정
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
    
    private var modeIndicator: some View {
        VStack {
            HStack {
                Spacer()
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(isConnect ? Color.green : Color.gray, lineWidth: 2)
                        .frame(width: 48, height: 30)

                    Text(isConnect ? "ON" : "OFF")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(isConnect ? .green : .gray)
                }
            }
            Spacer()
        }
    }
    
}

