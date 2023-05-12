import Alamofire


struct UserData: Codable {
    let user_idx: Int?
    let email: String
    let nickname: String?
    let accessToken: String?
    let refreshToken: String?
    let emoji: String?
}

// -----------------------

struct RandomNicname: Codable {
    let nickname: String
}

// -----------------------

struct Empty: Codable {}


struct UserService {
    private init() {}
    
    // 구글 로그인 및 회원 체크
    static func loginGoogle(data: [String : Any], completion: @escaping (Result<ResponseMessage<UserData>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/auth/check/google", method: .post, parameters: data, responseType: ResponseMessage<UserData>.self, completion: completion)
    }
    // 구글 계정 회원가입
    static func signupGoogle(data: [String : Any], completion: @escaping (Result<ResponseMessage<UserData>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/auth/signup/google", method: .post, parameters: data, responseType: ResponseMessage<UserData>.self, completion: completion)
    }
    // 애플 로그인 및 회원 체크
    static func loginApple(data: [String : Any], completion: @escaping (Result<ResponseMessage<UserData>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/auth/check/apple", method: .post, parameters: data, responseType: ResponseMessage<UserData>.self, completion: completion)
    }
    // 애플 계정 회원가입
    static func signupApple(data: [String : Any], completion: @escaping (Result<ResponseMessage<UserData>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/auth/signup/apple", method: .post, parameters: data, responseType: ResponseMessage<UserData>.self, completion: completion)
    }
    // 랜덤 닉네임 생성
    static func randomNickname(completion: @escaping (Result<ResponseMessage<RandomNicname>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/auth/nickname", method: .get, responseType: ResponseMessage<RandomNicname>.self, completion: completion)
    }
    
    // 로그아웃
    static func logout(completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/logout", method: .delete, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
    // 회원 탈퇴
    static func withdrawal(completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/withdrawal", method: .delete, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
    // 애플 회원 탈퇴
    static func withdrawalApple(data: [String: String], completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/withdrawal/apple", method: .delete, parameters: data, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
    
    // FCM 토큰 갱신
    static func postFCMToken(data: [String: String], completion: @escaping (Result<ResponseMessage<Empty>, AFError>) -> Void) {
        APIManager.shared.request(endpoint: "/account/fcm_token", method: .post, parameters: data, responseType: ResponseMessage<Empty>.self, completion: completion)
    }
}





// 사용방법

// Get
//UserService.getUsers { result in
//    switch result {
//    case .success(let users):
//        print("Users: \(users)")
//    case .failure(let error):
//        print("Error: \(error.localizedDescription)")
//    }
//}
//
// Post
//let newUser = User(id: nil, name: "John Doe", email: "john.doe@example.com")
//UserService.createUser(user: newUser) { result in
//    switch result {
//    case .success(let user):
//        print("Created user: \(user)")
//    case .failure(let error):
//        print("Error: \(error.localizedDescription)")
//    }
//}
//
// Put
//let updatedUser = User(id: 1, name: "Jane Doe", email: "jane.doe@example.com")
//UserService.updateUser(user: updatedUser) { result in
//    switch result {
//    case .success(let user):
//        print("Updated user: \(user)")
//    case .failure(let error):
//        print("Error: \(error.localizedDescription)")
//    }
//}
//
// Delete
//let userId = 1
//UserService.deleteUser(userId: userId) { result in
//    switch result {
//    case .success(let emptyResponse):
//        print("User deleted")
//    case .failure(let error):
//        print("Error: \(error.localizedDescription)")
//    }
//}
