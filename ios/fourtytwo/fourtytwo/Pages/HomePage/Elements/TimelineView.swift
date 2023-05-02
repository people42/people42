import SwiftUI
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
                                emotion: recentFeed.recentMessageInfo.emotion ?? "delete"
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
    @EnvironmentObject var reactionState: ReactionState
    
    @Environment(\.scenePhase) private var scenePhase
    
    @State private var refreshing: Bool = false

    var body: some View {
        if #available(iOS 15.0, *) {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    if viewModel.messageInfoList.count < 1 {
                        Spacer()
                        HStack {
                            Spacer()
                            Text("아직 인연이 없어요")
                            Spacer()
                        }
                        Spacer()
                    } else {
                        ForEach(viewModel.messageInfoList.indices, id: \.self) { index in
                            HStack(alignment: .center, spacing: 8) {
                                ZStack(alignment: .center) {
                                    if index < viewModel.messageInfoList.count - 1 {
                                        DashedLine()
                                    }
                                    TimelinePoint()
                                }
                                .frame(width: 16)
                                // 누르면 PlaceView로 이동
                                MessageCard(messageInfo: viewModel.messageInfoList[index])
                                    .onTapGesture {
                                        placeViewState.selectedPlaceID = viewModel.messageInfoList[index].placeIdx
                                        placeViewState.navigateToPlaceView = true
                                        placeViewState.placeDate = viewModel.messageInfoList[index].hour
                                     }
                            }
                            .padding(.bottom, 16)
                        }
                    }
                }
                .padding()
                .padding(.top, 16)
            }
            .refreshable {
                withAnimation {
                    refreshing = true
                    viewModel.fetchRecentFeed()
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                        refreshing = false
                    }
                }
            }
            .onAppear {
                viewModel.fetchRecentFeed()
            }
            .onChange(of: scenePhase) { newScenePhase in
                if newScenePhase == .active {
                    // foreground로 전환될 때 데이터를 새로 고칩니다.
                    viewModel.fetchRecentFeed()
                }
            }
            .onChange(of: reactionState.reaction) { newValue in
                viewModel.fetchRecentFeed()
            }
        } else {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    if viewModel.messageInfoList.count < 1 {
                        Spacer()
                        HStack {
                            Spacer()
                            Text("아직 인연이 없어요")
                            Spacer()
                        }
                        Spacer()
                    } else {
                        ForEach(viewModel.messageInfoList.indices, id: \.self) { index in
                            HStack(alignment: .center, spacing: 8) {
                                ZStack(alignment: .center) {
                                    if index < viewModel.messageInfoList.count - 1 {
                                        DashedLine()
                                    }
                                    TimelinePoint()
                                }
                                .frame(width: 16)
                                // 누르면 PlaceView로 이동
                                MessageCard(messageInfo: viewModel.messageInfoList[index])
                                    .onTapGesture {
                                        placeViewState.selectedPlaceID = viewModel.messageInfoList[index].placeIdx
                                        placeViewState.navigateToPlaceView = true
                                        placeViewState.placeDate = viewModel.messageInfoList[index].hour
                                     }
                            }
                            .padding(.bottom, 16)
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
                    // foreground로 전환될 때 데이터를 새로 고칩니다.
                    viewModel.fetchRecentFeed()
                }
            }
            .onChange(of: reactionState.reaction) { newValue in
                viewModel.fetchRecentFeed()
            }
        }
    }
}

struct TimelinePoint: View {
    var body: some View {
        Circle()
            .fill(Color.gray.opacity(0.7))
            .frame(width: 16, height: 16)
    }
}

struct DashedLine: View {
    var body: some View {
        Path { path in
            path.move(to: CGPoint(x: 8, y: 92))
            path.addLine(to: CGPoint(x: 8, y: 292))
        }
        .stroke(style: StrokeStyle(lineWidth: 5, dash: [0.1]))
        .foregroundColor(Color.gray.opacity(0.3))
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct TimelineView_Previews: PreviewProvider {
    static var previews: some View {
        TimelineView()
            .environmentObject(TimelineViewModel())
    }
}

