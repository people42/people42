import SwiftUI
import UIKit

struct SecondSectionView: View {
    @Binding var progress: Int
    @EnvironmentObject var signUpData: SignUpState
    
    let emojis = ["alien", "angry-face", "anguished-face", "anxious-face-with-sweat", "beaming-face-with-smiling-eyes", "cat-with-tears-of-joy", "cat-with-wry-smile", "clown-face", "cold-face", "confounded-face", "confused-face", "cowboy-hat-face", "crying-cat", "crying-face", "disappointed-face", "disguised-face", "dizzy-face", "downcast-face-with-sweat", "drooling-face", "exploding-head", "face-blowing-a-kiss", "face-exhaling", "face-in-clouds", "face-savoring-food", "face-screaming-in-fear", "face-vomiting", "face-with-hand-over-mouth", "face-with-head-bandage", "face-with-medical-mask", "face-with-monocle", "face-with-open-mouth", "face-with-raised-eyebrow", "face-with-rolling-eyes", "face-with-spiral-eyes", "face-with-steam-from-nose", "face-with-symbols-on-mouth", "face-with-tears-of-joy", "face-with-thermometer", "face-with-tongue", "face-without-mouth", "fearful-face", "flushed-face", "frowning-face-with-open-mouth", "frowning-face", "ghost", "grimacing-face", "grinning-cat-with-smiling-eyes", "grinning-cat", "grinning-face-with-big-eyes", "grinning-face-with-smiling-eyes", "grinning-face-with-sweat", "grinning-face", "grinning-squinting-face", "hot-face", "hugging-face", "hushed-face", "kissing-cat", "kissing-face-with-closed-eyes", "kissing-face-with-smiling-eyes", "kissing-face", "loudly-crying-face", "lying-face", "money-mouth-face", "nauseated-face", "nerd-face", "neutral-face", "partying-face", "pensive-face", "persevering-face", "pile-of-poo", "pleading-face", "pouting-face", "purple-monster", "relieved-face", "robot", "rolling-on-the-floor-laughing", "sad-but-relieved-face", "shushing-face", "skull", "sleeping-face", "sleepy-face", "slightly-frowning-face", "slightly-smiling-face", "smiling-cat-with-heart-eyes", "smiling-face-with-halo", "smiling-face-with-heart-eyes", "smiling-face-with-hearts", "smiling-face-with-horns", "smiling-face-with-smiling-eyes", "smiling-face-with-sunglasses", "smiling-face-with-tear", "smiling-face", "smirking-face", "sneezing-face", "squinting-face-with-tongue", "star-struck", "thinking-face", "tired-face", "unamused-face", "upside-down-face", "weary-cat", "weary-face", "winking-face-with-tongue", "winking-face", "woozy-face", "worried-face", "yawning-face", "zany-face", "zipper-mouth-face"]
    
    @State private var currentIndex = 0
    @State private var totalTranslation: CGFloat = 0
    private let emojiWidth: CGFloat = 40
    private let spacing: CGFloat = 10
    private let thresholdDistance: CGFloat = 50  // 원하는 드래그 거리로 설정
    @State private var previousHapticFeedback: CGFloat = 0
    
    var body: some View {
        VStack {
            Spacer()
            VStack {
                GifImage(emojis[currentIndex % emojis.count])
                    .frame(width: 200, height: 200)
                    .padding(.bottom, 40)
                
                GeometryReader { geometry in
                    ZStack {
                        HStack(alignment: .center, spacing: spacing) {
                            ForEach(0..<emojis.count, id: \.self) { index in
                                GeometryReader { itemGeometry in
                                    ZStack {
                                        if itemGeometry.frame(in: .global).midX >= geometry.frame(in: .global).midX - (emojiWidth / 2) && itemGeometry.frame(in: .global).midX <= geometry.frame(in: .global).midX + (emojiWidth / 2) {
                                                                Circle()
                                                                    .fill(Color.gray.opacity(0.2))
                                                                    .frame(width: emojiWidth + 10, height: emojiWidth + 10)
                                                            }
                                        GifImage(emojis[index % emojis.count], isAnimated: false)
                                            .frame(width: emojiWidth, height: emojiWidth)
                                            .id(index)
                                            .scaleEffect(scaleFactor(geometry: geometry, itemGeometry: itemGeometry))
                                    }
                                }
                                .frame(width: emojiWidth, height: emojiWidth)
                            }
                        }
                        .frame(height: emojiWidth + spacing + 10) // 포인팅 백그라운드의 크기 조절
                        .offset(x: -CGFloat(currentIndex) * (emojiWidth + spacing) + geometry.size.width / 2 - emojiWidth / 2 + totalTranslation)
                        .animation(.easeInOut)
                        .gesture(
                            DragGesture()
                                .onChanged { value in
                                    totalTranslation = value.translation.width
                                    
                                    // 50마다 햅틱
                                    let feedbackThreshold: CGFloat = 50
                                    let delta = abs(totalTranslation - previousHapticFeedback)
                                    if delta >= feedbackThreshold {
                                        let feedbackGenerator = UIImpactFeedbackGenerator(style: .medium)
                                        feedbackGenerator.prepare()
                                        feedbackGenerator.impactOccurred()
                                        previousHapticFeedback = totalTranslation
                                    }
                                }
                                .onEnded { value in
                                    let delta = Int((totalTranslation / thresholdDistance).rounded())
                                    let newIndex = currentIndex - delta
                                    
                                    currentIndex = newIndex % emojis.count
                                    while currentIndex < 0 {
                                        currentIndex += emojis.count
                                    }
                                    totalTranslation = 0
                                    previousHapticFeedback = 0

                                }
                        )
                    }
                    .clipped()
                }
                .frame(height: emojiWidth + spacing)

            }
            .onAppear {
                DispatchQueue.main.async {
                    currentIndex = emojis.count / 2
                }
            }
            Spacer()
            HStack {
                CustomButton(style: .blue, buttonText: "완료", action: {
                    signUpData.emoji = emojis[currentIndex % emojis.count]
                    progress += 1
                })
            }
            Spacer()
                .frame(maxHeight: 40)
        }
    }
    
    private func scaleFactor(geometry: GeometryProxy, itemGeometry: GeometryProxy) -> CGFloat {
        let itemCenterX = itemGeometry.frame(in: .global).midX   // 각 아이템의 중앙 X 좌표
        let centerX = geometry.frame(in: .global).midX   // 스크롤 중앙 X 좌표
        let distance = abs(itemCenterX - centerX)
        let threshold: CGFloat = 200   // 효과가 적용될 범위

        // 거리에 따라 스케일을 조정하려면 이 값을 변경하세요.
        let minScale: CGFloat = 0.4
        let maxScale: CGFloat = 1.0

        if distance < threshold {
            return maxScale - (distance / threshold) * (maxScale - minScale)
        } else {
            return minScale
        }
    }

}

struct SignupView_Previews3: PreviewProvider {
    static var previews: some View {
        SignupView()
    }
}
