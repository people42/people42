import Foundation
import CoreLocation
import UIKit

class WebSocketManager: NSObject, ObservableObject {
    
    // 싱글톤 인스턴스 생성
    static let shared = WebSocketManager()
    
    // APIManager와 UserState 인스턴스를 공유
    let userState = APIManager.shared.userState
    
    // LocationManager 인스턴스 생성
    private let locationManager = LocationManager()
    
    private var task: URLSessionWebSocketTask?
    @Published var isConnected: Bool = false
    private var selfClose: Bool = false // 내가 연결을 해제했는지
    
    // 추가: 웹소켓 연결 시도 횟수를 추적하는 속성
    private var reconnectAttempts = 0
    
    // 현재 유저 데이터
    func getCurrentUserData() -> [String: Any]? {
        guard let location = locationManager.currentLocation else {
            print("Current location not available.")
            return nil
        }
        
        let userData: [String: Any] = [
            "latitude": location.coordinate.latitude,
            "longitude": location.coordinate.longitude,
            "status": "watching"
        ]
        
        return userData
    }
    
    // 웹소켓 연결 메서드
    func connect() {
        print("웹소켓 연결 시도")
        guard let userIdx = userState.user_idx else {
            print("비로그인 유저, 웹소켓 연결 취소")
            return
        }
        print("로그인 유저 확인, 웹소켓 연결 시도")

        if WebSocketManager.shared.isConnected {
            print("이미 소켓과 연결 중")
            return
        }
        
        let urlString = "wss://www.people42.com/be42/socket?type=user&user_idx=\(userIdx)"
        print("웹소켓 URL : \(urlString)")
        guard let url = URL(string: urlString) else {
            print("Invalid URL")
            return
        }
        
        selfClose = false
        // URLSessionWebSocketTask를 사용하여 웹소켓 연결을 시작합니다.
        let session = URLSession(configuration: .default, delegate: self, delegateQueue: OperationQueue())
        task = session.webSocketTask(with: url)
        task?.resume()
    }
    
    // 웹소켓 연결을 재시도하는 메서드
    func reconnect() {
        DispatchQueue.main.async { [weak self] in
            // 백그라운드로 진입한 경우, 재연결을 시도하지 않음
            if UIApplication.shared.applicationState == .background {
                print("백그라운드에서는 웹소켓 연결을 유지하지 않음")
                return
            }
            
            // 연결 시도 횟수를 증가시키고 5회를 초과하면 재연결을 중단합니다.
            self?.reconnectAttempts += 1
            if self?.reconnectAttempts ?? 0 > 5 {
                print("웹소켓 재연결 시도를 중단합니다.")
                return
            }
            
            print("웹소켓 재연결 시도: \(self?.reconnectAttempts ?? 0)")
            DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                self?.connect()
            }
        }
    }
    
    // 웹소켓 연결 해제 메서드
    func disconnect() {
        selfClose = true
        task?.cancel(with: .goingAway, reason: nil)
        nearUsers = [:]
    }
    
    // 웹소켓을 통해 메시지를 보내는 메서드
    func sendMessage(method: String, data: [String: Any]) {
        var payload = data
        payload["method"] = method

        do {
            let jsonData = try JSONSerialization.data(withJSONObject: payload, options: [])
            if let jsonString = String(data: jsonData, encoding: .utf8) {
                task?.send(.string(jsonString)) { error in
                    if let error = error {
                        print("Error sending message: \(error.localizedDescription)")
                    }
                }
            } else {
                print("Error converting jsonData to jsonString")
            }
        } catch {
            print("Error encoding JSON: \(error.localizedDescription)")
        }
    }

    
    // INIT 메시지 처리 메서드
    func handleInit() {
        print("INIT 실행")
        guard let userData = getCurrentUserData() else { return }
        print(userData)
        sendMessage(method: "INIT", data: userData)
    }
    
    // CHANGE_STATUS 메시지 처리 메서드
    func handleChangeStatusWirte() {
        guard var updatedUserData = getCurrentUserData() else { return }
        print("status - writing")
        updatedUserData["status"] = "writing"
        sendMessage(method: "CHANGE_STATUS", data: updatedUserData)
    }
    
    // CHANGE_STATUS 메시지 처리 메서드
    func handleChangeStatusWatch() {
        guard var updatedUserData = getCurrentUserData() else { return }
        print("status - watching")
        updatedUserData["status"] = "watching"
        sendMessage(method: "CHANGE_STATUS", data: updatedUserData)
    }
    
    // MESSAGE_CHANGED 메시지 처리 메서드
    func handleMessageChanged(newMessage: String) {
        guard var updatedUserData = getCurrentUserData() else { return }
        print("socket - newMessage : \(newMessage)")
        updatedUserData["message"] = newMessage
        sendMessage(method: "MESSAGE_CHANGED", data: updatedUserData)
    }
    
    // MOVE 메시지 처리 메서드
    func handleMove(newLatitude: Double, newLongitude: Double) {
        guard var updatedUserData = getCurrentUserData() else { return }
        updatedUserData["latitude"] = newLatitude
        updatedUserData["longitude"] = newLongitude
        sendMessage(method: "MOVE", data: updatedUserData)
    }
    
    // 웹소켓에서 메시지를 수신하는 메서드
    private func receiveMessage() {
        guard isConnected else {
            print("웹소켓이 연결되어 있지 않습니다.")
            reconnect()
            return
        }
        
        print("소켓 리시버 장착")
        task?.receive { [weak self] result in
            switch result {
            case .success(let message):
                switch message {
                case .string(let text):
                    print("Received text: \(text)")
                    self?.handleMessage(message: text)
                case .data(let data):
                    print("Received data: \(data)")
                @unknown default:
                    print("Unknown message received")
                }
                
            case .failure(let error):
                print("Error receiving message: \(error.localizedDescription)")
            }
            self?.receiveMessage()
        }
    }
    
    // 근처 유저 목록
    var nearUsers: [Int: [String: Any]] = [:]

    // 응답 메시지 처리
    func handleMessage(message: String) {
        guard let data = message.data(using: .utf8),
              let json = try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any],
              let method = json["method"] as? String else {
            print("Invalid message format")
            return
        }

        switch method {
        case "INFO":
            handleInfoMessage(json)
        case "NEAR":
            handleNearMessage(json)
        case "CLOSE":
            handleCloseMessage(json)
        case "CHANGE_STATUS":
            handleChangeStatusMessage(json)
        case "MESSAGE_CHANGED":
            handleMessageChangedMessage(json)
        default:
            print("Unhandled method: \(method)")
        }
    }

    // 메서드 Info 처리
    private func handleInfoMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [String: Any],
              let nearUsers = data["nearUsers"] as? [[String: Any]] else {
            print("Invalid INFO message format")
            return
        }
        
        // 유저 목록 초기화
        self.nearUsers = [:]

        for user in nearUsers {
            guard let userIdx = user["userIdx"] as? Int,
                  let type = user["type"] as? String,
                  type != "guest" else {
                continue // type이 "guest"인 경우 건너뜀
            }

            // 위치 비교를 위해 새로운 변수 생성
            var updatedUser = user

            // latitude와 longitude 정보가 존재할 경우, 중앙과 겹치지 않도록 새로운 위치를 가져옴
            if let latitude = user["latitude"] as? Double,
               let longitude = user["longitude"] as? Double,
               isOverlappingWithCenter(newLatitude: latitude, newLongitude: longitude) {
                let newLocation = getNewLocation(currentLatitude: latitude, currentLongitude: longitude)
                updatedUser["latitude"] = newLocation.latitude
                updatedUser["longitude"] = newLocation.longitude
            }
            
            DispatchQueue.main.async {
                self.nearUsers[userIdx] = updatedUser
            }
            
        }
        print("!!!!!!!!!!!!!!!!!!!!!!!")
        print(self.nearUsers) // 수정된 위치 정보 출력
    }


    // 메서드 Near 처리
    private func handleNearMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [String: Any],
              let type = data["type"] as? String,
              let userIdx = data["userIdx"] as? Int else {
            print("Invalid NEAR message format")
            return
        }
        
        // type이 "guest"이면 무시
        guard type != "guest" else {
            print("Guest user")
            return
        }
        
        // 새로운 유저 추가 혹은 기존 유저 정보 갱신
        var updatedData = data
        
        // latitude와 longitude 정보가 존재할 경우, 중앙이나 다른 사용자와 겹치지 않도록 새로운 위치를 가져옴
        if let latitude = data["latitude"] as? Double,
           let longitude = data["longitude"] as? Double,
           isOverlappingWithOtherUsers(newLatitude: latitude, newLongitude: longitude) ||
           isOverlappingWithCenter(newLatitude: latitude, newLongitude: longitude) {
            let newLocation = getNewLocation(currentLatitude: latitude, currentLongitude: longitude)
            updatedData["latitude"] = newLocation.latitude
            updatedData["longitude"] = newLocation.longitude
        }
        
        DispatchQueue.main.async {
            self.nearUsers[userIdx] = updatedData
        }
        
        print("User \(userIdx) updated or added")
    }


    // 메서드 Close 처리
    private func handleCloseMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [String: Any],
              let userIdx = data["userIdx"] as? Int else {
            print("Invalid CLOSE message format")
            return
        }

        // 유저 제거
        DispatchQueue.main.async {
            self.nearUsers.removeValue(forKey: userIdx)
        }
        
        print("User removed: \(userIdx)")
    }
    
    // 메서드 Change Status 처리
    private func handleChangeStatusMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [String: Any],
              let userIdx = data["userIdx"] as? Int,
              let status = data["status"] as? String else {
            print("Invalid CHANGE_STATUS message format")
            return
        }
        
        // 유저 상태 변경
        DispatchQueue.main.async {
            self.nearUsers[userIdx]?["status"] = status
        }
        
        print("User \(userIdx) status changed to \(status)")
    }

    // 메서드 Message Changed 처리
    private func handleMessageChangedMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [String: Any],
              let userIdx = data["userIdx"] as? Int,
              let message = data["message"] as? String else {
            print("Invalid MESSAGE_CHANGED message format")
            return
        }
        
        // 유저 메시지 변경
        DispatchQueue.main.async {
            self.nearUsers[userIdx]?["message"] = message
        }
        
        print("User \(userIdx) message changed to \(message)")
    }
}

// 위치 재정렬 메서드들
extension WebSocketManager {

    // 위치 중복 검사 메서드 (다른 사용자)
    func isOverlappingWithOtherUsers(newLatitude: Double, newLongitude: Double) -> Bool {
        let newLocation = CLLocation(latitude: newLatitude, longitude: newLongitude)
        
        for (_, userInfo) in nearUsers {
            if let userLatitude = userInfo["latitude"] as? Double,
               let userLongitude = userInfo["longitude"] as? Double {
                let userLocation = CLLocation(latitude: userLatitude, longitude: userLongitude)
                let distance = newLocation.distance(from: userLocation)
                if distance <= 10.0 { // 10미터 이내에 있는 경우
                    return true
                }
            }
        }
        return false
    }

    // 위치 중복 검사 메서드 (중앙 위치)
    func isOverlappingWithCenter(newLatitude: Double, newLongitude: Double) -> Bool {
        guard let myLocation = locationManager.currentLocation else {
            print("My location not available.")
            return false
        }
        let newLocation = CLLocation(latitude: newLatitude, longitude: newLongitude)
        let distance = newLocation.distance(from: myLocation)
        return distance <= 20.0 // 10미터 이내에 있는 경우
    }

    // 새 위치 생성 메서드
    func getNewLocation(currentLatitude: Double, currentLongitude: Double, bearing: Double, distance: Double) -> (latitude: Double, longitude: Double) {
        let earthRadius = 6371.0 // 지구의 반지름 (킬로미터 단위)
        let radianDistance = distance / earthRadius // 거리를 라디안 단위로 변환
        let radianBearing = bearing * .pi / 180.0 // 방향을 라디안 단위로 변환
        
        let radianLatitude = currentLatitude * .pi / 180.0 // 현재 위도를 라디안 단위로 변환
        let radianLongitude = currentLongitude * .pi / 180.0 // 현재 경도를 라디안 단위로 변환
        
        let newRadianLatitude = asin(sin(radianLatitude) * cos(radianDistance) + cos(radianLatitude) * sin(radianDistance) * cos(radianBearing))
        var newRadianLongitude = radianLongitude + atan2(sin(radianBearing) * sin(radianDistance) * cos(radianLatitude), cos(radianDistance) - sin(radianLatitude) * sin(newRadianLatitude))
        
        // 새로운 경도가 -π보다 작거나 π보다 큰 경우, 적절한 범위(-π ~ π)로 조정
        if newRadianLongitude < -Double.pi {
            newRadianLongitude += 2.0 * .pi
        } else if newRadianLongitude > Double.pi {
            newRadianLongitude -= 2.0 * .pi
        }
        
        let newLatitude = newRadianLatitude * 180.0 / .pi // 새 위도를 도 단위로 변환
        let newLongitude = newRadianLongitude * 180.0 / .pi // 새 경도를 도 단위로 변환
        
        return (newLatitude, newLongitude)
    }
    
    func getNewLocation(currentLatitude: Double, currentLongitude: Double) -> (latitude: Double, longitude: Double) {
        let randomBearing = Double(arc4random_uniform(360)) // 0 ~ 360도

        // 0.02km(= 20m) ~ 0.25km(= 250m) 사이의 랜덤 거리를 생성합니다.
        let minDistance = 0.02 // 20 meters in km
        let maxDistance = 0.22 // 220 meters in km
        let randomDistance = minDistance + (maxDistance - minDistance) * (Double(arc4random()) / Double(UInt32.max))
        
        return getNewLocation(currentLatitude: currentLatitude, currentLongitude: currentLongitude, bearing: randomBearing, distance: randomDistance)
    }

}

// URLSessionWebSocketDelegate를 구현하여 웹소켓의 연결 및 해제 상태를 처리합니다.
extension WebSocketManager: URLSessionWebSocketDelegate {
    func urlSession(_ session: URLSession, webSocketTask: URLSessionWebSocketTask, didOpenWithProtocol protocol: String?) {
        
        DispatchQueue.main.async {
            
            self.isConnected = true
            print("웹소켓 연결 성공")
            
            // 추가: 연결 성공 시 재연결 시도 횟수를 초기화합니다.
            self.reconnectAttempts = 0
            
            self.receiveMessage()
            
            self.handleInit()
            
        }
    }

    func urlSession(_ session: URLSession, webSocketTask: URLSessionWebSocketTask, didCloseWith closeCode: URLSessionWebSocketTask.CloseCode, reason: Data?) {
        
        DispatchQueue.main.async {
            
            self.isConnected = false
            print("웹소켓 연결 해제: \(closeCode)")
            
            // 내가 종료한게 맞다면 재연결하지 않음
            if self.selfClose == true { return }
            
            // 추가: 웹소켓이 비정상적으로 종료된 경우에만 재연결을 시도합니다.
            if closeCode != .normalClosure {
                self.reconnect()
            }
        }
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        if let error = error {
            print("웹소켓 연결 에러: \(error.localizedDescription)")
            
            // 추가: 웹소켓 연결에 실패한 경우 재연결을 시도합니다.
            reconnect()
        }
    }
}
