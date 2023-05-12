import SwiftUI

// refreshable은 iOS 15.0 이상에서만 지원하면 15이상에서 작동하도록 만들어진 모디파이어

// refreshable modifier
struct RefreshableModifier: ViewModifier {
    @Binding var isRefreshing: Bool
    let action: () -> Void
    
    @ViewBuilder
    func body(content: Content) -> some View {
        if #available(iOS 15.0, *) {
            content.refreshable {
                withAnimation {
                    isRefreshing = true
                    action()
                }
            }
        } else {
            content
        }
    }
}

// 사용법 - 예시
//.modifier(RefreshableModifier(isRefreshing: $refreshing, action: {
//    viewModel.fetchRecentFeed()
//    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
//        refreshing = false
//    }
//}))
