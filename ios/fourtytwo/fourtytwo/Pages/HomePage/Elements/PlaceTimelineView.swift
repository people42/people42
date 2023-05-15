import SwiftUI

struct PlaceTimelineView: View {
    @EnvironmentObject var placeViewState: PlaceViewState
    
    @Environment(\.scenePhase) private var scenePhase
    
    @State var locationCardsData: [LocationCardData] = []
    @State private var refreshing: Bool = false

    var body: some View {
        ZStack {
            HStack {
                if !locationCardsData.isEmpty {
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: 2, height: UIScreen.main.bounds.height)
                        .padding(.horizontal, 22.5)
                }
                Spacer()
            }
            
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    if locationCardsData.isEmpty {
                        Spacer()
                        HStack {
                            Spacer()
                            Text("아직 인연이 없어요")
                            Spacer()
                        }
                        Spacer()
                    } else {
                        ForEach(locationCardsData.indices, id: \.self) { index in
                            HStack(alignment: .center, spacing: 8) {
                                ZStack(alignment: .center) {
                                    TimelinePoint()
                                }
                                LocationCard(locationCardData: locationCardsData[index])
                            }
                            .padding(.bottom, index == locationCardsData.count - 1 ? 100 : 16)
                        }
                    }
                }
                .padding()
            }
            .onAppear {
                getLocationCardsData()
            }
            .onChange(of: scenePhase) { newScenePhase in
                if newScenePhase == .active {
                    getLocationCardsData()
                }
            }
            .modifier(RefreshableModifier(isRefreshing: $refreshing, action: {
                getLocationCardsData()
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    refreshing = false
                }
            }))
        }
    }
}

extension PlaceTimelineView {
    private func getLocationCardsData() {
        FeedService.getNewFeed { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    
                    if let newFeeds = response.data {
                        self.locationCardsData = []
                        self.locationCardsData = newFeeds.map { newFeed in
                            
                            // RecentFeed를 MessageInfo로 변환
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
                    }
                }
 

            case .failure(let error):
                print("Error fetching recent feeds: \(error)")
            }
        }
    }
}

struct PlaceTimelineView_Previews: PreviewProvider {
    static var previews: some View {
        PlaceTimelineView()
    }
}
