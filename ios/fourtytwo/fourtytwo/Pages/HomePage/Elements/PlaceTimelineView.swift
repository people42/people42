import SwiftUI

class PlaceTimelineViewModel: ObservableObject {
    @Published var locationCardsData: [LocationCardData] = []
    
    func getLocationCardsData() {
        FeedService.getNewFeed { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    if let newFeeds = response.data {
                        let newLocationCardsData = newFeeds.map { newFeed in
                            return LocationCardData(
                                time: newFeed.placeWithTimeInfo.time,
                                nickname: newFeed.recentUsersInfo.nickname,
                                userCnt: newFeed.recentUsersInfo.userCnt,
                                placeIdx: newFeed.placeWithTimeInfo.placeIdx,
                                placeName: newFeed.placeWithTimeInfo.placeName,
                                old: newFeed.recentUsersInfo.repeatUserEmojis,
                                new: newFeed.recentUsersInfo.firstTimeUserEmojis
                            )
                        }
                        
                        if self.locationCardsData != newLocationCardsData {
                            self.locationCardsData = newLocationCardsData
                        }
                    }
                }
            case .failure(let error):
                print("Error fetching recent feeds: \(error)")
            }
        }
    }
}

struct PlaceTimelineView: View {
    @StateObject private var viewModel = PlaceTimelineViewModel()
    @Environment(\.scenePhase) private var scenePhase
    @State private var refreshing: Bool = false
    
    var body: some View {
        ZStack {
            HStack {
                if !viewModel.locationCardsData.isEmpty {
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: 2, height: UIScreen.main.bounds.height)
                        .padding(.horizontal, 22.5)
                }
                Spacer()
            }
            
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    if viewModel.locationCardsData.isEmpty {
                        VStack {
                            Spacer()
                            HStack {
                                Spacer()
                                Text("아직 인연이 없어요")
                                Spacer()
                            }
                            Spacer()
                        }
                        .frame(height: UIScreen.main.bounds.height - 200)
                    } else {
                        ForEach(viewModel.locationCardsData.indices, id: \.self) { index in
                            HStack(alignment: .center, spacing: 8) {
                                ZStack(alignment: .center) {
                                    TimelinePoint()
                                }
                                LocationCard(locationCardData: viewModel.locationCardsData[index])
                            }
                            .padding(.bottom, index == viewModel.locationCardsData.count - 1 ? 200 : 16)
                        }
                    }
                }
                .padding()
            }
            .id(UUID())
            .onAppear {
                viewModel.getLocationCardsData()
                Timer.scheduledTimer(withTimeInterval: 10, repeats: true) { _ in
                    viewModel.getLocationCardsData()
                }
            }
            .onChange(of: scenePhase) { newScenePhase in
                if newScenePhase == .active {
                    viewModel.getLocationCardsData()
                }
            }
            .modifier(RefreshableModifier(isRefreshing: $refreshing, action: {
                viewModel.getLocationCardsData()
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    refreshing = false
                }
            }))
        }
    }
}


struct PlaceTimelineView_Previews: PreviewProvider {
    static var previews: some View {
        PlaceTimelineView()
    }
}
