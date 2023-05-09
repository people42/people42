import Foundation
import CoreLocation
import UIKit

class WebSocketManager: NSObject, ObservableObject {
    
    // 나의 위치를 저장하는 프로퍼티 추가
    var myLocation: CLLocation?
    
    // 싱글톤 인스턴스 생성
    static let shared = WebSocketManager()
    
    // APIManager와 UserState 인스턴스를 공유
    let userState = APIManager.shared.userState
    
    // LocationManager 인스턴스 생성
    private let locationManager = LocationManager()
    
    private var task: URLSessionWebSocketTask?
    @Published var isConnected: Bool = false
    
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
        task?.cancel(with: .goingAway, reason: nil)
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
    
    // WRITING_MESSAGE 메시지 처리 메서드
    func handleWritingMessage() {
        guard let userData = getCurrentUserData() else { return }
        sendMessage(method: "WRITING_MESSAGE", data: userData)
    }
    
    // MESSAGE_CHANGED 메시지 처리 메서드
    func handleMessageChanged(newMessage: String) {
        guard var updatedUserData = getCurrentUserData() else { return }
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
    // 기존 코드
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
            guard let userIdx = user["userIdx"] as? Int else {
                continue
            }

            self.nearUsers[userIdx] = user
        }
        print("!!!!!!!!!!!!!!!!!!!!!!!")
        print(nearUsers)
    }
    
    // 메서드 Near 처리
    private func handleNearMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [String: Any],
              let type = data["type"] as? String,
              let userIdx = data["userIdx"] as? Int,
              type != "guest" else { // type이 "guest"이면 무시
            print("Invalid NEAR message format or guest user")
            return
        }
        
        // 새로운 유저 추가 혹은 기존 유저 정보 갱신
        nearUsers[userIdx] = data
        
        print("User \(userIdx) updated or added")
    }

    // 변경코드
    // 메서드 Info 처리
    private func handleInfoMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [String: Any],
              let userIdx = data["userIdx"] as? Int,
              let latitude = data["latitude"] as? Double,
              let longitude = data["longitude"] as? Double else {
            print("Invalid INFO message format")
            return
        }
        
        var updatedLatitude = latitude
        var updatedLongitude = longitude
        
        if isOverlappingWithCenter(newLatitude: updatedLatitude, newLongitude: updatedLongitude) || isOverlappingWithCenter(newLatitude: updatedLatitude, newLongitude: updatedLongitude) {
            print("위치가 중앙과 10미터 이내. 새로운 위치로 이동합니다.")
            let newLocation = getNewLocation(currentLatitude: updatedLatitude, currentLongitude: updatedLongitude)
            updatedLatitude = newLocation.latitude
            updatedLongitude = newLocation.longitude
        }

        if isOverlappingWithOtherUsers(newLatitude: updatedLatitude, newLongitude: updatedLongitude) || isOverlappingWithCenter(newLatitude: updatedLatitude, newLongitude: updatedLongitude) {
            print("위치가 10미터 이내. 새로운 위치로 이동합니다.")
            let newLocation = getNewLocation(currentLatitude: updatedLatitude, currentLongitude: updatedLongitude)
            updatedLatitude = newLocation.latitude
            updatedLongitude = newLocation.longitude
        }
        
        // 유저 추가
        nearUsers[userIdx] = ["latitude": updatedLatitude, "longitude": updatedLongitude]
        
        print("User added: \(userIdx), location: (\(updatedLatitude), \(updatedLongitude))")
    }

    // 메서드 Near 처리
    private func handleNearMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [[String: Any]] else {
            print("Invalid NEAR message format")
            return
        }

        for userData in data {
            guard let userIdx = userData["userIdx"] as? Int,
                  let latitude = userData["latitude"] as? Double,
                  let longitude = userData["longitude"] as? Double else {
                print("Invalid user data in NEAR message")
                continue
            }

            var updatedLatitude = latitude
            var updatedLongitude = longitude
            
            if isOverlappingWithCenter(newLatitude: updatedLatitude, newLongitude: updatedLongitude) || isOverlappingWithCenter(newLatitude: updatedLatitude, newLongitude: updatedLongitude) {
                print("위치가 중앙과 10미터 이내. 새로운 위치로 이동합니다.")
                let newLocation = getNewLocation(currentLatitude: updatedLatitude, currentLongitude: updatedLongitude)
                updatedLatitude = newLocation.latitude
                updatedLongitude = newLocation.longitude
            }

            if isOverlappingWithOtherUsers(newLatitude: updatedLatitude, newLongitude: updatedLongitude) || isOverlappingWithCenter(newLatitude: updatedLatitude, newLongitude: updatedLongitude) {
                print("위치가 10미터 이내. 새로운 위치로 이동합니다.")
                let newLocation = getNewLocation(currentLatitude: updatedLatitude, currentLongitude: updatedLongitude)
                updatedLatitude = newLocation.latitude
                updatedLongitude = newLocation.longitude
            }

            nearUsers[userIdx] = ["latitude": updatedLatitude, "longitude": updatedLongitude]
        }
        
        print("NEAR message processed, \(data.count) users added/updated")
    }


    // 메서드 Close 처리
    private func handleCloseMessage(_ json: [String: Any]) {
        guard let data = json["data"] as? [String: Any],
              let userIdx = data["userIdx"] as? Int else {
            print("Invalid CLOSE message format")
            return
        }

        // 유저 제거
        nearUsers.removeValue(forKey: userIdx)
        
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
        nearUsers[userIdx]?["status"] = status
        
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
        nearUsers[userIdx]?["message"] = message
        
        print("User \(userIdx) message changed to \(message)")
    }
}

// 위치 재정렬 메서드들
extension WebSocketManager {
    // ------------------ 위치 재정렬 메서드들

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
        guard let myLocation = myLocation else {
            print("My location not available.")
            return false
        }
        let newLocation = CLLocation(latitude: newLatitude, longitude: newLongitude)
        let distance = newLocation.distance(from: myLocation)
        return distance <= 10.0 // 10미터 이내에 있는 경우
    }

    // 새 위치 생성 메서드
    func getNewLocation(currentLatitude: Double, currentLongitude: Double) -> (latitude: Double, longitude: Double) {
        // 임의의 방향과 거리를 이용하여 새 위치 생성
        // 이 예제에서는 임의의 값으로 0.001도를 사용합니다.
        let randomAngle = Double(arc4random_uniform(360)) * Double.pi / 180.0 // 0 ~ 360도
        let distance = 0.001 * 0.5 // 약 111미터 = 0.001
        let newLatitude = currentLatitude + distance * cos(randomAngle)
        let newLongitude = currentLongitude + distance * sin(randomAngle)
        return (newLatitude, newLongitude)
    }

}

// URLSessionWebSocketDelegate를 구현하여 웹소켓의 연결 및 해제 상태를 처리합니다.
extension WebSocketManager: URLSessionWebSocketDelegate {
    func urlSession(_ session: URLSession, webSocketTask: URLSessionWebSocketTask, didOpenWithProtocol protocol: String?) {
        isConnected = true
        print("웹소켓 연결 성공")
        
        // 추가: 연결 성공 시 재연결 시도 횟수를 초기화합니다.
        reconnectAttempts = 0
        
        receiveMessage()
        
        handleInit()
    }

    func urlSession(_ session: URLSession, webSocketTask: URLSessionWebSocketTask, didCloseWith closeCode: URLSessionWebSocketTask.CloseCode, reason: Data?) {
        isConnected = false
        print("웹소켓 연결 해제: \(closeCode)")

        // 추가: 웹소켓이 비정상적으로 종료된 경우에만 재연결을 시도합니다.
        if closeCode != .normalClosure {
            reconnect()
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

extension WebSocketManager: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        myLocation = location
    }
}
