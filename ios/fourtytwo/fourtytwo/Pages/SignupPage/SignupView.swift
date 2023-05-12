import SwiftUI

struct SignupView: View {
    @State private var progress = 1
    
    @Environment(\.colorScheme) var colorScheme
    var imageName: String { colorScheme == .dark ? "BgLogoWhite" : "BgLogoGray" }
        
    var body: some View {
        ZStack {
            GeometryReader { geometry in
                Image(imageName)
                    .resizable()
                    .scaledToFit()
                    .frame(width: geometry.size.width, height: geometry.size.height)
                    .offset(y: geometry.size.height/3)
            }

            
            VStack {
                GeometryReader { geometry in
                    let progressBarWidth = min(geometry.size.width * 0.9, 400)
                    
                    ZStack {
                        SegmentedProgressBar(segments: 3, progress: CGFloat(progress), availableWidth: progressBarWidth)
                            .frame(width: progressBarWidth)
                            .padding(.top, 16)
                    }
                    .frame(width: geometry.size.width)
                }
                .frame(height: 20)
                
                switch progress {
                case 1:
                    HStack {
                        Text("닉네임을\n설정해주세요")
                        Spacer()
                    }
                    .padding(24)
                    .font(.customHeader5)
                    FirstSectionView(progress: $progress)
                        .background(Color.backgroundSecondary)
                        .cornerRadius(32)
                        .padding(24)
                        .shadow(color: Color.black.opacity(colorScheme == .dark ? 0 : 0.05), radius: 4, x: -1, y: -1)
                        .shadow(color: Color.black.opacity(colorScheme == .dark ? 0 : 0.2), radius: 4, x: 4, y: 4)

                    Spacer()
                case 2:
                    HStack {
                        Text("프로필을\n선택해주세요")
                        Spacer()
                    }
                    .padding(24)
                    .font(.customHeader5)
                    SecondSectionView(progress: $progress)
                        .background(Color.backgroundSecondary)
                        .cornerRadius(32)
                        .padding(24)
                        .shadow(color: Color.black.opacity(colorScheme == .dark ? 0 : 0.05), radius: 4, x: -1, y: -1)
                        .shadow(color: Color.black.opacity(colorScheme == .dark ? 0 : 0.2), radius: 4, x: 4, y: 4)
                    Spacer()
                case 3:
                    Spacer()
                    ThirdSectionView()
//                        .background(Color.backgroundSecondary)
                        .cornerRadius(32)
                        .padding(24)
                        .shadow(color: Color.black.opacity(colorScheme == .dark ? 0 : 0.05), radius: 4, x: -1, y: -1)
                        .shadow(color: Color.black.opacity(colorScheme == .dark ? 0 : 0.2), radius: 4, x: 4, y: 4)
                    Spacer()
                default:
                    Text("Error")
                }
            }
        }
        .background(Color.backgroundPrimary.edgesIgnoringSafeArea(.all))
    }
}

struct SignupView_Previews: PreviewProvider {
    static var previews: some View {
        SignupView()
    }
}
