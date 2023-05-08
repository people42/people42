import Foundation

class WebSocketManager: NSObject, ObservableObject {
    
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
            task?.send(.data(jsonData)) { error in
                if let error = error {
                    print("Error sending message: \(error.localizedDescription)")
                }
            }
        } catch {
            print("Error encoding JSON: \(error.localizedDescription)")
        }
    }
    
    // INIT 메시지 처리 메서드
    func handleInit() {
        guard let userData = getCurrentUserData() else { return }
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
        print("소켓 리시버 장착")
        task?.receive { [weak self] result in
            switch result {
            case .success(let message):
                switch message {
                case .string(let text):
                    print("Received text: \(text)")
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
    
    // 추가: 웹소켓 연결을 재시도하는 메서드
    func reconnect() {
        // 연결 시도 횟수를 증가시키고 5회를 초과하면 재연결을 중단합니다.
        reconnectAttempts += 1
        if reconnectAttempts > 5 {
            print("웹소켓 재연결 시도를 중단합니다.")
            return
        }
        
        print("웹소켓 재연결 시도: \(reconnectAttempts)")
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) { [weak self] in
            self?.connect()
        }
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

