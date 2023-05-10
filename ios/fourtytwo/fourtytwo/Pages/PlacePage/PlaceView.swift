import SwiftUI
import MapKit

// MVVM 패턴
// M - Model
// TimelineView의 MessageInfo 공유

// VM - ViewModel
class PlaceViewModel: ObservableObject {
    
    @Published var messageInfoList: [MessageInfo] = []
    @Published var location: (latitude: Double, longitude: Double)?
    @Published var placeName: String?

    
    func removeMicroseconds(_ dateString: String) -> String {
        if let dotIndex = dateString.lastIndex(of: ".") {
            return String(dateString[..<dotIndex])
        } else {
            return dateString
        }
    }
}

// V - View
struct PlaceView: View {
    @EnvironmentObject var placeViewState: PlaceViewState
    
    @StateObject private var viewModel = PlaceViewModel()
    
    @State var toggleHeight: CGFloat = 160
    @State private var refreshing: Bool = false

    @Environment(\.presentationMode) var presentationMode
    @Environment(\.scenePhase) private var scenePhase
    
    @State var currentPage = 0
    
    @State private var showAlert = false
    @State private var alertText = ""
    
    @State private var isLoading = false // 로딩 상태를 추적하는 상태 변수

    var body: some View {
        ZStack {
            VStack {
                if let location = viewModel.location {
                    PlaceMapView(viewModel: viewModel, location: CLLocationCoordinate2D(latitude: location.latitude, longitude: location.longitude), toggleHeight: $toggleHeight)
                }


                GeometryReader { geometry in
                    ScrollView {
                        VStack(alignment: .leading, spacing: 16) {
                        
                            
                            if viewModel.messageInfoList.count < 1 {
                                Spacer()
                                    .frame(height: geometry.size.height / 2 - 90)
                                HStack {
                                    Spacer()
                                    Text("아직 인연이 없어요")
                                    Spacer()
                                }
                                Spacer()
                            } else {
                                ForEach(viewModel.messageInfoList.indices, id: \.self) { index in
                                    NavigationLink(destination: PersonView(location: CLLocationCoordinate2D(latitude: viewModel.location?.latitude ?? 0, longitude: viewModel.location?.longitude ?? 0), userIdx: viewModel.messageInfoList[index].userIdx)) {
                                        HStack(alignment: .center, spacing: 8) {
                                            MessageCard(messageInfo: viewModel.messageInfoList[index])
                                        }
                                    }
                                    .padding(.bottom, 16)
                                }
                            }
                        }
                        .padding(.top, 16)
                        .padding()
                    }
                    .modifier(RefreshableModifier(isRefreshing: $refreshing, action: {
                        getPlaceFeed(currentPage)
                        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                            refreshing = false
                        }
                    }))
                    
                }
                
                HStack {
                    // 이전 페이지 버튼
                    if currentPage > 0 {
                        Button(action: {
                            currentPage -= 1
                            getPlaceFeed(currentPage)
                        }) {
                            Text("이전 페이지")
                        }
                        .disabled(isLoading) // 로딩 중이면 버튼을 비활성화합니다.
                    }
                    
                    Spacer()
                    
                    // 다음 페이지 버튼
                    if viewModel.messageInfoList.count == 10 {
                        Button(action: {
                            currentPage += 1
                            getPlaceFeed(currentPage)
                        }) {
                            Text("다음 페이지")
                        }
                        .disabled(isLoading) // 로딩 중이면 버튼을 비활성화합니다.
                    }
                }
                .padding()
            }
            
            // 로딩 상태 표시
            if isLoading {
                Color.black.opacity(0.4)
                    .edgesIgnoringSafeArea(.all)
                ActivityIndicator(style: .large)
            }

        }
        .onAppear {
            getPlaceFeed(currentPage)
        }
        .onChange(of: scenePhase) { newScenePhase in
            if newScenePhase == .active {
                // foreground로 전환될 때 데이터를 새로 고칩니다.
                getPlaceFeed(currentPage)
            }
        }
        .background(Color.backgroundPrimary.edgesIgnoringSafeArea(.all))
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .regular))
                        .foregroundColor(Color("Text"))
                }
            }
            ToolbarItem(placement: .principal) {
                Text(viewModel.placeName ?? "장소")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }
        .alert(isPresented: $showAlert) {
             Alert(title: Text("알림"), message: Text(alertText), dismissButton: .default(Text("확인")))
        }

    }
}

// 전역변수 참조 함수 따로 관리
extension PlaceView {
    func getPlaceFeed(_ pageCnt: Int) {
        
        isLoading = true
        
        let queryData: [String: Any] = [
            "placeIdx": placeViewState.selectedPlaceID!,
            "time": viewModel.removeMicroseconds(placeViewState.placeDate!),
            "page": pageCnt,
            "size": 10
        ]
        
        FeedService.getPlaceFeed(data: queryData) { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    
                    if let placeFeeds = response.data {
                        // 장소 이름 수납
                        viewModel.placeName = placeFeeds.placeWithTimeAndGpsInfo.placeName
                        
                        // 위치 데이터 수납
                        viewModel.location = (placeFeeds.placeWithTimeAndGpsInfo.placeLatitude, placeFeeds.placeWithTimeAndGpsInfo.placeLongitude)
                        
                        // 메세지 데이터 수납
                        let messages = placeFeeds.messagesInfo.map { placeFeed in

                            // placeFeed를 MessageInfo로 변환
                            return MessageInfo(
                                profileImage: placeFeed.emoji!,
                                stack: placeFeed.brushCnt,
                                nickname: placeFeed.nickname,
                                contents: placeFeed.content,
                                placeIdx: nil,
                                placeName: placeFeeds.placeWithTimeAndGpsInfo.placeName,
                                hour: placeFeeds.placeWithTimeAndGpsInfo.time,
                                hasMultiple: (placeFeed.brushCnt > 1),
                                cardColor: CardColor(rawValue: placeFeed.color) ?? .red,
                                messageIdx: placeFeed.messageIdx,
                                emotion: placeFeed.emotion ?? "delete",
                                userIdx: placeFeed.userIdx
                            )
                            
                        }
                        
                        if messages.isEmpty {
                            // 요청한 페이지가 비어 있음
                            if currentPage > 0 {
                                currentPage -= 1
                            }
                            showAlert = true
                            alertText = "다음 페이지가 존재하지 않습니다."
                            
                            DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                showAlert = false
                            }
                        } else {
                            viewModel.messageInfoList = messages
                        }
                    }
                    
                    isLoading = false
                }

            case .failure(let error):
                print("Error fetching recent feeds: \(error)")
            }
        }
    }
}


