import Alamofire
import SwiftUI

// 최근 피드 조회 자료 구조 시작

struct RecentFeed: Codable {
    let recentMessageInfo: RecentMessageInfo
    let placeWithTimeInfo: PlaceWithTimeInfo
}

struct RecentMessageInfo: Codable {
    let messageIdx: Int
    let content: String
    let userIdx: Int
    let nickname: String
    let emoji: String?
    let color: String
    let brushCnt: Int
    let emotion: String?
}

struct PlaceWithTimeInfo: Codable {
    let placeIdx: Int
    let placeName: String
    let time: String
}

// 최근 피드 조회 자료 구조 끝
// -----------------------
// 장소별 피드 조회 자료 구조 시작

struct PlaceFeed: Codable {
    let messagesInfo: [RecentMessageInfo]
    let placeWithTimeAndGpsInfo: PlaceWithTimeAndGpsInfo
}

struct PlaceWithTimeAndGpsInfo: Codable {
    let placeIdx: Int
    let placeName: String
    let time: String
    let placeLatitude: Double
    let placeLongitude: Double
}

// 장소별 피드 조회 자료 구조 끝
// -----------------------
// 감정표현 자료 구조 시작

struct EmotionFeed: Codable {
    let emotion: String
    let messageIdx: Int
}

// 감정표현 자료 구조 끝
// -----------------------
// 사람별 피드 조회 자료 구조 시작

struct PersonPlaces: Codable {
    let brushCnt: Int
    let userIdx: Int
    let nickname: String
    let emoji: String
    let placeResDtos: [PlaceResDtos]
}

struct PlaceResDtos: Codable {
    let placeIdx: Int
    let placeName: String
    let placeLatitude: Double
    let placeLongitude: Double
    let brushCnt: Int
}

// 사람별 피드 조회 자료 구조 끝
// -----------------------
// 사람/장소 피드 조회 자료 구조 시작

struct PersonPlaceFeed: Codable {
    let messagesInfo: [PersonPlaceResDtos]
    let brushCnt: Int
}

struct PersonPlaceResDtos: Codable {
    let messageIdx: Int
    let content: String
    let time: String
    let emtion: String?
}

// 사람/장소 피드 조회 자료 구조 끝


struct FeedService {
    private init() {}

    // 최근 피드 조회
    static func getRecentFeed(completion: @escaping (Result<ResponseMessage<[RecentFeed]>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/feed/recent", method: .get, responseType: ResponseMessage<[RecentFeed]>.self, completion: completion)
    }

    // 장소별 피드 조회
    static func getPlaceFeed(data: [String: Any], completion: @escaping (Result<ResponseMessage<PlaceFeed>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/feed/place", method: .get, parameters: data, responseType: ResponseMessage<PlaceFeed>.self, completion: completion)
    }
    
    // 감정표현
    static func selectEmotion(data: [String: Any], completion: @escaping (Result<ResponseMessage<EmotionFeed>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/feed/emotion", method: .post, parameters: data, responseType: ResponseMessage<EmotionFeed>.self, completion: completion)
    }
    
    // 사람별 피드 조회
    static func getPersonFeed(data: [String: Any], completion: @escaping (Result<ResponseMessage<PersonPlaces>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/feed/user", method: .get, parameters: data, responseType: ResponseMessage<PersonPlaces>.self, completion: completion)
    }
    
    // 사람/장소 피드 조회
    static func getPersonPlaceFeed(data: [String: Any], completion: @escaping (Result<ResponseMessage<PersonPlaceFeed>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/feed/user/place", method: .get, parameters: data, responseType: ResponseMessage<PersonPlaceFeed>.self, completion: completion)
    }

}
