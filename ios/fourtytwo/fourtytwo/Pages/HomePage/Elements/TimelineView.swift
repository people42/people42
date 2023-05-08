import SwiftUI
import SwiftUIX
import Combine

// MVVM 패턴
// M - Model
struct MessageInfo {
    let profileImage: String
    let stack: Int
    let nickname: String
    let contents: String
    let placeIdx: Int?
    let placeName: String
    let hour: String
    let hasMultiple: Bool
    let cardColor: CardColor
    let messageIdx: Int
    let emotion: String
    let userIdx: Int
}

// VM - ViewModel
class TimelineViewModel: ObservableObject {
    @Published var messageInfoList: [MessageInfo] = []
    
    private var cancellables = Set<AnyCancellable>()

    func fetchRecentFeed() {
        FeedService.getRecentFeed { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    
                    if let recentFeeds = response.data {
                        self.messageInfoList = []
                        self.messageInfoList = recentFeeds.map { recentFeed in
                            
                            // RecentFeed를 MessageInfo로 변환
                            return MessageInfo(
                                profileImage: recentFeed.recentMessageInfo.emoji!,
                                stack: recentFeed.recentMessageInfo.brushCnt,
                                nickname: recentFeed.recentMessageInfo.nickname,
                                contents: recentFeed.recentMessageInfo.content,
                                placeIdx: recentFeed.placeWithTimeInfo.placeIdx,
                                placeName: recentFeed.placeWithTimeInfo.placeName,
                                hour: recentFeed.placeWithTimeInfo.time,
                                hasMultiple: (recentFeed.recentMessageInfo.brushCnt > 1),
                                cardColor: CardColor(rawValue: recentFeed.recentMessageInfo.color) ?? .red,
                                messageIdx: recentFeed.recentMessageInfo.messageIdx,
                                emotion: recentFeed.recentMessageInfo.emotion ?? "delete",
                                userIdx: recentFeed.recentMessageInfo.userIdx
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

// V - View
struct TimelineView: View {
    @StateObject private var viewModel = TimelineViewModel()
    
    @EnvironmentObject var placeViewState: PlaceViewState
    
    @Environment(\.scenePhase) private var scenePhase
    
    @State private var refreshing: Bool = false

    var body: some View {
        ZStack {
            TimelineBackgroundView(messageInfoList: viewModel.messageInfoList)
            
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    if viewModel.messageInfoList.isEmpty {
                        Spacer()
                        HStack {
                            Spacer()
                            Text("아직 인연이 없어요")
                            Spacer()
                        }
                        Spacer()
                    } else {
                        ForEach(viewModel.messageInfoList.indices, id: \.self) { index in
                            MessageRow(messageInfo: viewModel.messageInfoList[index], onTap: {
                                placeViewState.selectedPlaceID = viewModel.messageInfoList[index].placeIdx
                                placeViewState.navigateToPlaceView = true
                                placeViewState.placeDate = viewModel.messageInfoList[index].hour
                            })
                            .padding(.bottom, index == viewModel.messageInfoList.count - 1 ? 100 : 16)
                            .padding(.trailing, 16)
                        }
                    }
                }
                .padding()
                .padding(.top, 16)
            }
            .onAppear {
                viewModel.fetchRecentFeed()
            }
            .onChange(of: scenePhase) { newScenePhase in
                if newScenePhase == .active {
                    viewModel.fetchRecentFeed()
                }
            }
            .modifier(RefreshableModifier(isRefreshing: $refreshing, action: {
                viewModel.fetchRecentFeed()
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    refreshing = false
                }
            }))
        }
    }
}



struct TimelineBackgroundView: View {
    let messageInfoList: [MessageInfo]
    
    var body: some View {
        HStack {
            if !messageInfoList.isEmpty {
                Rectangle()
                    .fill(Color.gray.opacity(0.3))
                    .frame(width: 2, height: UIScreen.main.bounds.height)
                    .padding(.horizontal, 22.5)
            }
            Spacer()
        }
    }
}

struct MessageRow: View {
    let messageInfo: MessageInfo
    let onTap: () -> Void
    
    var body: some View {
        HStack(alignment: .center, spacing: 8) {
            ZStack(alignment: .center) {
                TimelinePoint()
            }
            MessageCard(messageInfo: messageInfo)
                .onTapGesture(perform: onTap)
        }
    }
}

struct TimelinePoint: View {
    var body: some View {
        Circle()
            .fill(Color.gray)
            .frame(width: 16, height: 16)
    }
}

struct TimelineView_Previews: PreviewProvider {
    static var previews: some View {
        TimelineView()
            .environmentObject(TimelineViewModel())
    }
}
