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

    @Environment(\.presentationMode) var presentationMode
    @Environment(\.scenePhase) private var scenePhase

    var body: some View {
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
                
            }
        }
        .onAppear {
            getPlaceFeed()
        }
        .onChange(of: scenePhase) { newScenePhase in
            if newScenePhase == .active {
                // foreground로 전환될 때 데이터를 새로 고칩니다.
                getPlaceFeed()
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
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(Color("Text"))
                }
            }
            ToolbarItem(placement: .principal) {
                Text(viewModel.placeName ?? "장소")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }

    }
}

// 전역변수 참조 함수 따로 관리
extension PlaceView {
    func getPlaceFeed() {
        
        let queryData: [String: Any] = [
            "placeIdx": placeViewState.selectedPlaceID!,
            "time": viewModel.removeMicroseconds(placeViewState.placeDate!),
            "page": 0,
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
                        viewModel.messageInfoList = placeFeeds.messagesInfo.map { placeFeed in

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
                    }
                }
 

            case .failure(let error):
                print("Error fetching recent feeds: \(error)")
            }
        }
    }
}

struct PlaceView_Previews: PreviewProvider {
    static var previews: some View {
        let dummyMessageInfoList: [MessageInfo] = [
            MessageInfo(profileImage: "robot", stack: 1, nickname: "사용자1", contents: "안녕하세요!", placeIdx: nil, placeName: "장소1", hour: "오늘 15시쯤", hasMultiple: false, cardColor: .red, messageIdx: 1, emotion: "happy", userIdx: 1),
            MessageInfo(profileImage: "alien", stack: 1, nickname: "사용자2", contents: "여기 정말 좋네요!", placeIdx: nil, placeName: "장소1", hour: "오늘 16시쯤", hasMultiple: false, cardColor: .blue, messageIdx: 2, emotion: "happy", userIdx: 1)
        ]
        
        let viewModel = PlaceViewModel()
        viewModel.messageInfoList = dummyMessageInfoList
        viewModel.placeName = "더미 장소"
        
        return PlaceView()
            .environmentObject(PlaceViewState())
    }
}

