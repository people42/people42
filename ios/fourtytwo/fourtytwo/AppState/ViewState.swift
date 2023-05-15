import SwiftUI

class AppState: ObservableObject {
    @Published var currentView: ViewType = .login

    enum ViewType {
        case login
        case home
        case signup
    }

    func switchView(to view: ViewType) {
        withAnimation(.easeInOut(duration: 0.3)) {
            currentView = view
        }
    }
}

class PlaceViewState: ObservableObject {
    @Published var selectedPlaceID: Int?
    @Published var navigateToPlaceView: Bool = false
    @Published var placeDate: String?
}
