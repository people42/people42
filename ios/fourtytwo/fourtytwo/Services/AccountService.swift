import Alamofire
import Foundation

struct Myinfo: Codable {
    let emoji: String
    let message: String?
    let messageCnt: Int
    let fire: Int
    let heart: Int
    let tear: Int
    let thumbsUp: Int
}

// -----------------------

struct MyHistory: Codable {
    let messageIdx: Int
    let content: String
    let createdAt: String
    let heart: Int
    let fire: Int
    let tear: Int
    let thumbsUp: Int
}

struct AccountService {
    private init() {}
    
    // 나의 현재 상태 조회
    static func getMyinfo(completion: @escaping (Result<ResponseMessage<Myinfo>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/myinfo", method: .get, responseType: ResponseMessage<Myinfo>.self, completion: completion)
    }
    
    // 나의 생각 작성
    static func writeMessage(data: [String : Any], completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/message", method: .post, parameters: data, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
    // 나의 이전 생각 조회
    static func getMyHistory(data: [String : Any], completion: @escaping (Result<ResponseMessage<[MyHistory]>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/history", method: .get, parameters: data, responseType: ResponseMessage<[MyHistory]>.self, completion: completion)
    }
    
    // 나의 닉네임 변경
    static func changeNickname(data: [String : Any], completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/nickname", method: .put, parameters: data, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
    // 나의 프로필 이모지 변경
    static func changeEmoji(data: [String : Any], completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/emoji", method: .put, parameters: data, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
    // 나의 상태메세지 삭제
    static func deleteMessage(data: [String : Any], completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/message", method: .put, parameters: data, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
    // 신고
    static func reportMessage(data: [String : Any], completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/report", method: .post, parameters: data, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
    // 차단
    static func blockUser(data: [String : Any], completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/block", method: .post, parameters: data, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
}
