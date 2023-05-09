import Alamofire
import SwiftUI

// Notification

struct Notification: Codable {
    let notificationCnt: Int
}

// -----------------------

struct NotificationHistory: Codable {
    let title: String
    let body: String
    let emoji: String
    let createdAt: String
}

// Notification


struct NotificationService {
    private init() {}
    
    // 최근 알림 수 조회
    static func getNewNotification(completion: @escaping (Result<ResponseMessage<Notification>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/notification", method: .get, responseType: ResponseMessage<Notification>.self, completion: completion)
    }
    
    // 최근 피드 조회
    static func getNotificationHistory(completion: @escaping (Result<ResponseMessage<[NotificationHistory]>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/notification/history", method: .get, responseType: ResponseMessage<[NotificationHistory]>.self, completion: completion)
    }
}
