import SwiftUI

@main
struct fourtytwoApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    @StateObject private var appState = AppState()
    @StateObject private var signUpState = SignUpState()
    @StateObject private var userState = UserState()
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

class AppDelegate: UIResponder, UIApplicationDelegate {
    var locationManager = LocationManager()
    var locationSender: LocationSender?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        locationSender = LocationSender(locationManager: locationManager)
        locationSender?.startSendingLocations()
        return true
    }
}
