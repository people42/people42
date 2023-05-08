import SwiftUI

struct DragToDeleteView<Content: View>: View {
    let content: Content
    let onDelete: () -> Void

    @State private var dragOffset: CGFloat = 0

    init(content: Content, onDelete: @escaping () -> Void) {
        self.content = content
        self.onDelete = onDelete
    }

    var body: some View {
        ZStack(alignment: .trailing) {
            RoundedRectangle(cornerRadius: 32)
                .foregroundColor(.red)
                .opacity(dragOffset > -80 ? 0 : Double((dragOffset + 80) / -20))
                .scaleEffect(0.98)
                .animation(.easeInOut, value: dragOffset)

            Text("삭제")
                .font(.customHeader6)
                .foregroundColor(.white)
                .padding(.trailing, 16)
                .opacity(dragOffset > -100 ? 0 : 1)
                .animation(.easeInOut, value: dragOffset)

            content
                .offset(x: dragOffset)
                .gesture(
                    DragGesture()
                        .onChanged { value in
                            let translation = value.translation.width
                            if translation < 0 {
                                dragOffset = translation * (1 - min(1, abs(translation) / 500))
                            }
                        }
                        .onEnded { value in
                            if dragOffset < -100 {
                                onDelete()
                            }
                            withAnimation {
                                dragOffset = 0
                            }
                        }
                )
        }
    }
}
