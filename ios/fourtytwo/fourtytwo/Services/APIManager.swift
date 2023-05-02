import Alamofire
import SwiftUI


struct ResponseMessage<T: Codable>: Codable {
    let message: String
    let status: Int
    let data: T?
}

class APIManager {
    // 싱글턴 인스턴스 생성
    static let shared = APIManager()

    // 기본 생성자를 private으로 설정하여 외부에서 인스턴스 생성을 제한함
    private init() {}

    // API 기본 URL 설정
    private let baseURL = "https://people42.com/be42/api/v1"

    // LoginUserData 인스턴스를 참조하는 프로퍼티 추가
    @Published var userState = UserState()

    // 기본 HTTP 헤더 설정
    private var headers: HTTPHeaders = [
    ]

    // 엑세스 토큰을 설정하고 상태 저장소에 저장하는 함수
    func setAccessToken(at accessToken: String?, rt refreshToken: String?) {
        userState.accessToken = accessToken ?? ""
        userState.refreshToken = refreshToken ?? ""
    }

    // 상태 저장소에서 엑세스 토큰을 가져오는 함수
    func getAccessToken() -> String? {
        return userState.accessToken.isEmpty ? nil : userState.accessToken
    }
    
    // 상태 저장소에서 리프레쉬 토큰을 가져오는 함수
    func getRefreshToken() -> String? {
        return userState.refreshToken.isEmpty ? nil : userState.refreshToken
    }

    // API 요청을 처리하는 메서드
    struct EmptyResponse: Codable {}
    
    // 엑세스 토큰을 새로 고치는 함수
    private func refreshAccessToken(completion: @escaping (Result<Void, AFError>) -> Void) {
        guard let refreshToken = getRefreshToken() else {
            completion(.failure(AFError.parameterEncodingFailed(reason: .missingURL)))
            return
        }

        // 헤더에서 엑세스 토큰 제거 및 리프레시 토큰 삽입
        headers.remove(name: "ACCESS-TOKEN")
        headers.add(HTTPHeader(name: "REFRESH-TOKEN", value: refreshToken))

        request(endpoint: "/auth/token", method: .post, responseType: ResponseMessage<UserData>.self) { result in
            switch result {
            case .success(let responseMessage):
                if let userData = responseMessage.data {
                    print("최신 유저 정보 들어옴")
                    print(userData)
                    // 유저 정보 저장
                    APIManager.shared.userState.user_idx = userData.user_idx
                    APIManager.shared.userState.email = userData.email
                    APIManager.shared.userState.nickname = userData.nickname
                    APIManager.shared.userState.emoji = userData.emoji
                    APIManager.shared.setAccessToken(at: userData.accessToken, rt: userData.refreshToken)
                }
                completion(.success(()))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }

    // API 요청 함수
    func request<T: Decodable>(
        endpoint: String,
        method: HTTPMethod = .get,
        parameters: Parameters? = nil,
        responseType: T.Type,
        completion: @escaping (Result<T, AFError>) -> Void
    ) {
        if let token = getAccessToken() {
            headers.add(HTTPHeader(name: "ACCESS-TOKEN", value: "\(token)"))
        } else {
            headers.remove(name: "ACCESS-TOKEN")
        }

        // Get일 경우, 파라미터를 쿼리로 전환
        let encoding: ParameterEncoding = method == .get ? URLEncoding.default : JSONEncoding.default
        
        // Alamofire 요청 URL 생성
        let url = try! encoding.encode(URLRequest(url: URL(string: baseURL + endpoint)!), with: parameters).url!

        // URL 출력
        print("Request URL: \(url)")

        AF.request(baseURL + endpoint, method: method, parameters: parameters, encoding: encoding, headers: headers).validate().response { response in
            switch response.result {
            case .success(let data):
                if responseType == EmptyResponse.self {
                    completion(.success(EmptyResponse() as! T))
                } else {
                    let decoder = JSONDecoder()
                    if let data = data, let decodedData = try? decoder.decode(T.self, from: data) {
                        completion(.success(decodedData))
                    } else {
                        completion(.failure(AFError.responseSerializationFailed(reason: .inputDataNilOrZeroLength)))
                    }
                }
            case .failure(let error):
                print("에러메시지 시작")
                if let statusCode = response.response?.statusCode, statusCode == 401 {
                    print("\(statusCode) 토큰 만료")
                    // 401 에러 처리: 엑세스 토큰 갱신 및 요청 재시도
                    self.refreshAccessToken { result in
                        switch result {
                        case .success:
                            // 토큰 갱신 성공 후 요청 재시도 - 재귀
                            print("토큰 재발급 완료, 재요청")
                            self.request(endpoint: endpoint, method: method, parameters: parameters, responseType: responseType, completion: completion)
                        case .failure(let error):
                            // 토큰 갱신 실패
                            completion(.failure(error))
                        }
                    }
                } else {
                    // 다른 에러 처리
                    if let data = response.data {
                        do {
                            let decoder = JSONDecoder()
                            let errorResponse = try decoder.decode(ResponseMessage<EmptyResponse>.self, from: data)
                            print("Error Message: \(errorResponse.message), Status: \(errorResponse.status)")
                        } catch {
                            print("Error Decoding Error Message")
                        }
                    } else {
                        if let afError = error as? AFError {
                            switch afError {
                            case .invalidURL(let url):
                                print("Invalid URL: \(url)")
                            case .parameterEncodingFailed(let reason):
                                print("Parameter encoding failed: \(reason)")
                            case .multipartEncodingFailed(let reason):
                                print("Multipart encoding failed: \(reason)")
                            case .responseValidationFailed(let reason):
                                print("Response validation failed: \(reason)")
                            case .responseSerializationFailed(let reason):
                                print("Response serialization failed: \(reason)")
                            default:
                                print("Unknown error: \(error)")
                            }
                        } else {
                            print("Non-AFError: \(error)")
                        }
                    }
                    completion(.failure(error))
                }

            }
        }
    }

}
