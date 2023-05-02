import Alamofire

class LocationService {
    private init() {}  // 외부에서의 해당 클래스의 인스턴스를 직접 생산 불가능하게 막는다.

    static func sendLocation(latitude: Double, longitude: Double, completion: @escaping (Result<Bool, AFError>) -> Void) {
        let endpoint = "/background"
        let parameters: Parameters = [
            "latitude": latitude,
            "longitude": longitude
        ]

        APIManager.shared.request(endpoint: endpoint, method: .post, parameters: parameters, responseType: APIManager.EmptyResponse.self) { result in
            switch result {
            case .success:
                completion(.success(true))
            case .failure(let error):
                completion(.failure(error))
            }
        }
    }
}

// 위는 static로 선언에서 LocationService내에 전역으로 사용가능하게 만든 함수이다.
// 클래스 인스턴스가 필요없는 상황에서 좋다. 그냥 가져다 쓰고 말 때, 좋다는 말.
// --------------------------
// 아래는 싱글톤 패턴으로 정의,
// (1) shared를 이용하면 단일 인스턴스를 생성하고 공유할 수 있다.
// (2) LocationService2.shared.sendLocation 클래스에 접근해서 단일 인스턴스를 생성하는 방식이다.
// shared는 클래스 내에 정적(static) 속성으로 선언되며, 클래스 내부에서 단 한 번만 초기화됩니다. 이후에 클래스의 shared 속성을 통해 인스턴스에 접근하게 되면, 동일한 인스턴스가 반환된다.
// (3) 외부에서의 해당 클래스의 인스턴스를 직접 생산 불가능하게 막는다. 고로, 오로지 shared로만 선언이 가능하게 된다. 완전한 싱글톤 유지.

//class LocationService2 {
//    static let shared = LocationService()  // (1)
//    private init() {}  // (3)
//
//    func sendLocation(latitude: Double, longitude: Double, completion: @escaping (Result<Bool, AFError>) -> Void) {
//        let endpoint = "/api/v1/background"
//        let parameters: Parameters = [
//            "latitude": latitude,
//            "longitude": longitude
//        ]
//
//        APIManager.shared.request(endpoint: endpoint, method: .post, parameters: parameters, responseType: APIManager.EmptyResponse.self) { result in
//            switch result {
//            case .success:
//                completion(.success(true))
//            case .failure(let error):
//                completion(.failure(error))
//            }
//        }
//    }
//}

// 사용법
//LocationService2.shared.sendLocation(latitude: latitude, longitude: longitude) { result in  // (2)
//    switch result {
//    case .success:
//        print("Location sent successfully")
//    case .failure(let error):
//        print("Error sending location: \(error)")
//    }
//}
