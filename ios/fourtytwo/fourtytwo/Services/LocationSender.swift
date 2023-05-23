import BackgroundTasks

class LocationSender {
    // 타이머 인스턴스를 사용하여 주기적으로 위치를 전송합니다.
    private var timer: Timer?
    
    // LocationManager 인스턴스를 통해 현재 위치 정보를 가져옵니다.
    private let locationManager: LocationManager

    // 생성자에서 locationManager를 초기화합니다.
    init(locationManager: LocationManager) {
        self.locationManager = locationManager
    }

    // 위치 전송을 시작하는 메서드입니다.
    func startSendingLocations() {

        // 60초마다 sendLocationToServer() 메서드를 호출하는 타이머를 생성합니다.
        timer = Timer.scheduledTimer(withTimeInterval: 5, repeats: true) { _ in
//            print("timer 전송으로 가동중")
            self.sendLocationToServer()
        }

        // 백그라운드 작업을 등록합니다.
        registerBackgroundTask()
    }

    // 위치 전송을 중지하는 메서드입니다.
    func stopSendingLocations() {
        // 타이머를 중지하고 nil로 설정합니다.
        timer?.invalidate()
        timer = nil
    }

    // 현재 위치를 서버에 전송하는 메서드입니다.
    private func sendLocationToServer() {
        // LocationManager로부터 현재 위치를 가져옵니다.
        if let location = locationManager.currentLocation {
            let latitude = location.coordinate.latitude
            let longitude = location.coordinate.longitude
            
            // 현재 위치를 콘솔에 출력합니다.
//            print("-------------------")
//            print("현재 위치")
//            print("lat : \(latitude)")
//            print("log : \(longitude)")
//            print("-------------------")

            // 현재 위치를 서버에 전송합니다.
            LocationService.sendLocation(latitude: latitude, longitude: longitude) { result in
                switch result {
                case .success(let response):
                    if response.data != nil {
//                        print("위치전송 성공")
                    }

                case .failure(let error):
                    print("Error sending location: \(error)")
                }
            }
            
            // 웹소켓 현재 위치 전송
            WebSocketManager.shared.handleMove(newLatitude: latitude, newLongitude: longitude)
            
        } else {
            print("Failed to get current location")
        }
    }

    // 백그라운드 작업을 등록하는 메서드입니다.
    private func registerBackgroundTask() {
        print("백그라운드 작업 등록")
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "com.people42.app.sendLocation", using: nil) { task in
            self.handleAppRefresh(task: task as! BGAppRefreshTask)
        }
    }

    // 백그라운드 작업이 실행될 때 호출되는 메서드입니다.
    private func handleAppRefresh(task: BGAppRefreshTask) {
        // Set up an expiration handler for the task
        task.expirationHandler = {
            // This block is called when the task is about to expire
            // You must set the task to complete before the system terminates your app
            task.setTaskCompleted(success: false)
        }

        // Perform the task
        sendLocationToServer()

        // Inform the system that the task is complete
        task.setTaskCompleted(success: true)

        // Schedule a new app refresh task
        scheduleAppRefresh()
    }
    
    private func scheduleAppRefresh() {
        let request = BGAppRefreshTaskRequest(identifier: "com.people42.app.sendLocation")
        // Set the earliestBeginDate to be the current time plus 15 minutes
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60)
        
        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Could not schedule app refresh: \(error)")
        }
    }


}

// 앱에서 LocationSender 인스턴스를 생성하고, startSendingLocations() 메서드 호출
