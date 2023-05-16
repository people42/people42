import UIKit
import SwiftUI

class SceneDelegate: NSObject, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let _ = (scene as? UIWindowScene) else { return }
    }
    
    func scene(_ scene: UIScene, continue userActivity: NSUserActivity) {
        guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
            let incomingURL = userActivity.webpageURL,
            let components = NSURLComponents(url: incomingURL, resolvingAgainstBaseURL: true),
            let path = components.path else {
                    return
            }
            
        //도메인 주소의 쿼리값을 받음
        let params = components.queryItems ?? [URLQueryItem]()
        print("path = \(incomingURL)")
        print("params = \(params)")
    }

    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        for urlContext in URLContexts {
            let urlToOpen = urlContext.url
        }
    }

    func sceneDidDisconnect(_ scene: UIScene) {
      
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
      
    }

    func sceneWillResignActive(_ scene: UIScene) {
      
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
      print("포그라운드 전환")
        WebSocketManager.shared.connect()
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
      print("백그라운드 전환")
        WebSocketManager.shared.disconnect()
    }
}
