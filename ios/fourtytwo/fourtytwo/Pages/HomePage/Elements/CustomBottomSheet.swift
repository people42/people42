import SwiftUI

class CustomBottomSheetViewModel: ObservableObject {
    @Published var offsetY: CGFloat = 0
    @Published var translation: CGSize = .zero
    @Published var upperY: CGFloat = 48
    @Published var lowerY: CGFloat = 52
    
    var minOpacity: Double = 0

    func setOffset(_ height: CGFloat) -> CGFloat {
        if offsetY + translation.height <= upperY {
            return upperY
        } else if offsetY + translation.height >= height - lowerY {
            return height - lowerY
        } else {
            return offsetY + translation.height
        }
    }

    func getCurrentOpacity(_ height: CGFloat, reverse: Bool = false) -> Double {
        let currentOffset = offsetY + translation.height
        let maxOffset = height - lowerY

        var opacity: Double = 0

        if currentOffset <= upperY {
            opacity = 1
        } else if currentOffset >= maxOffset {
            opacity = minOpacity
        } else {
            let opacityRange = 1 - minOpacity
            let offsetProgress = 1 - (currentOffset - upperY) / (maxOffset - upperY)
            opacity = minOpacity + (opacityRange * Double(offsetProgress))
        }

        return reverse ? 1 - opacity : opacity
    }
    
    func toggleSheet(_ height: CGFloat) {
        withAnimation(.interactiveSpring(response: 0.5, dampingFraction: 0.8)) {
            if offsetY == upperY {
                offsetY = height - lowerY
            } else {
                offsetY = upperY
            }
        }
    }

}


struct CustomBottomSheet: View {
    @StateObject private var viewModel = CustomBottomSheetViewModel()

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                VStack {
                    Spacer()
                         .frame(height: 45)
                    
                    ZStack {
                        Color("BgPrimary")
//                        .opacity(viewModel.getCurrentOpacity(geometry.size.height))
                        PlaceTimelineView()
                    }
                    
                }
                .offset(y: viewModel.setOffset(geometry.size.height))

                VStack {
                    VStack {
                        HStack {
                            Spacer()
                            RoundedRectangle(cornerRadius: 3, style: .continuous)
                                .frame(width: 40, height: 5)
                                .padding(.top, 16)
                                .padding(.bottom, 8)
                            Spacer()
                        }
                        VStack {
                            Text("장소별 스침 확인하기")
                                .padding(.bottom, 16)
                                .font(.customOverline)
                                .opacity(viewModel.getCurrentOpacity(geometry.size.height, reverse: true))
                        }
                        .frame(height: 16)
                    }
                    .onTapGesture {
                        viewModel.toggleSheet(geometry.size.height)
                    }
                    .background(Color("BgSecondary"))
                    .clipShape(CustomCorners(corners: [.topRight, .topLeft], radius: 32))
//                    .opacity(viewModel.getCurrentOpacity(geometry.size.height))
                    Spacer()
                }
                .offset(y: viewModel.setOffset(geometry.size.height))
                .gesture(
                    DragGesture()
                        .onChanged { value in
                            viewModel.translation = value.translation
                        }
                        .onEnded { value in
                            withAnimation(.interactiveSpring(response: 0.5, dampingFraction: 0.8)) {
                                let threshold: CGFloat = 60

                                if value.translation.height < -threshold {
                                    viewModel.offsetY = viewModel.upperY
                                } else if value.translation.height > threshold {
                                    viewModel.offsetY = geometry.size.height - viewModel.lowerY
                                } else {
                                    viewModel.offsetY = viewModel.offsetY
                                }

                                viewModel.translation = .zero
                            }
                        }
                )
            }
            .onAppear {
                DispatchQueue.main.async {
                    viewModel.offsetY = geometry.size.height - viewModel.lowerY
                }
            }
        }
    }
}

//Custom Corner Shapes
struct CustomCorners: Shape {
    
    var corners: UIRectCorner
    var radius: CGFloat
    
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}

struct CustomBottomSheet_Previews: PreviewProvider {
    static var previews: some View {
        ZStack {
            CustomBottomSheet()
        }
        .background(Color.white)
    }
}
