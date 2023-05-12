import UIKit
import SwiftUI
import Firebase
import FirebaseMessaging

class AppDelegate: UIResponder, UIApplicationDelegate {
    var locationManager = LocationManager()
    var locationSender: LocationSender?
    
    // Firebase Push Notifications 관련 변수
    let gcmMessageIDKey = "gcm.message_id"
    
    // SceneDelegate 설정
    func application(
      _ application: UIApplication,
      configurationForConnecting connectingSceneSession: UISceneSession,
      options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
      let sceneConfig = UISceneConfiguration(name: nil, sessionRole: connectingSceneSession.role)
      sceneConfig.delegateClass = SceneDelegate.self
      return sceneConfig
    }

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // 위치 전송 기능 시작
        locationSender = LocationSender(locationManager: locationManager)
        locationSender?.startSendingLocations()
        
        // 파이어베이스 설정
        FirebaseApp.configure()
        
        // iOS 10 이상일 경우, 알림 권한 요청 및 알림 설정
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().delegate = self
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
        } else {
            let settings: UIUserNotificationSettings =
            UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }

        // 원격 알림 등록
        application.registerForRemoteNotifications()

        // 메세징 델리겟 설정
        Messaging.messaging().delegate = self

        // UNUserNotificationCenter 델리겟 설정
        UNUserNotificationCenter.current().delegate = self

        return true
    }

    // fcm 토큰이 등록 되었을 때
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }

}

// Cloud Messaging...
extension AppDelegate: MessagingDelegate {
    // fcm 등록 토큰을 받았을 때
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
//        print("토큰을 받았다")
        if let token = fcmToken {
            let dataDict: [String: String] = ["token": token]
//            print(dataDict)
            // 서버로 FCM 토큰 전달
            UserService.postFCMToken(data: dataDict) { result in
                switch result {
                case .success(let response):
                    print("FCM 토큰 전송 성공: \(response.message)")
                case .failure(let error):
                    print("FCM 토큰 전송 실패: \(error)")
                }
            }
        }
    }
}

// User Notifications... (InApp Notification)
@available(iOS 10, *)
extension AppDelegate: UNUserNotificationCenterDelegate {
    // 앱이 켜져있을 때 푸시 메시지가 나타날 때
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                              willPresent notification: UNNotification,
                              withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions)
                                -> Void) {
        let userInfo = notification.request.content.userInfo

        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
            }

            print(userInfo)

            completionHandler([[.banner, .badge, .sound]])
        }

    // 푸시메시지를 받았을 때
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                              didReceive response: UNNotificationResponse,
                              withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo

        // Do Something With MSG Data...
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        
        print(userInfo)

        completionHandler()
    }
}
