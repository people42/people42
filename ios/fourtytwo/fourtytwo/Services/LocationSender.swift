import BackgroundTasks

class LocationSender {
    private var timer: Timer?
    private let locationManager: LocationManager

    init(locationManager: LocationManager) {
        self.locationManager = locationManager
    }

    func startSendingLocations() {
        timer = Timer.scheduledTimer(withTimeInterval: 60, repeats: true) { _ in
            self.sendLocationToServer()
        }
        registerBackgroundTask()
    }

    func stopSendingLocations() {
        timer?.invalidate()
        timer = nil
    }

    private func sendLocationToServer() {
        if let location = locationManager.currentLocation {
            let latitude = location.coordinate.latitude
            let longitude = location.coordinate.longitude
            
            print("-------------------")
            print("현재 위치")
            print("lat : \(latitude)")
            print("log : \(longitude)")
            print("-------------------")

            LocationService.sendLocation(latitude: latitude, longitude: longitude) { result in
                switch result {
                case .success(let response):
                    if response.data != nil {
                        print("위치전송 성공")
                    }

                case .failure(let error):
                    print("Error sending location: \(error)")
                }
            }
        } else {
            print("Failed to get current location")
        }
    }


    private func registerBackgroundTask() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "com.example.app.sendLocation", using: nil) { task in
            self.handleAppRefresh(task: task as! BGAppRefreshTask)
        }
    }

    private func handleAppRefresh(task: BGAppRefreshTask) {
        task.expirationHandler = {
            // 태스크가 만료되면 수행할 작업
        }

        sendLocationToServer()

        let request = BGAppRefreshTaskRequest(identifier: "com.example.app.sendLocation")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 300)
        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Could not schedule app refresh: \(error)")
        }
    }
}

// 앱에서 LocationSender 인스턴스를 생성하고, startSendingLocations() 메서드 호출
