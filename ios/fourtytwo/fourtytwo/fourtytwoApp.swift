import SwiftUI


@main
struct fourtytwoApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    @StateObject private var appState = AppState()
    @StateObject private var userState = UserState()
    @StateObject private var signUpState = SignUpState()
    @StateObject private var placeViewState = PlaceViewState()

    var body: some Scene {
        WindowGroup {
            SplashView()
                .environmentObject(appState)
                .environmentObject(signUpState)
                .environmentObject(userState)
                .environmentObject(placeViewState)
        }
    }
}


