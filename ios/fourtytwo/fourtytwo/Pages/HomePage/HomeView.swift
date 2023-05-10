import SwiftUI
import MapKit
import Combine

class HomeViewModel: ObservableObject {
    @Published var message: String = "아직 적은 글이 없어요."
    @Published var hasMultiple: Bool = false
    
    @Published var reactionCounts: [String: Int] = ["fire_circle": 0, "heart_circle": 0, "tear_circle": 0, "thumbsUp_circle": 0]
    
    func getMyinfo() {
        AccountService.getMyinfo { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    if let data = response.data {
                        self.message = data.message ?? "아직 적은 글이 없어요."
                        self.hasMultiple = data.messageCnt > 1
                        
                        self.reactionCounts["fire_circle"] = data.fire
                        self.reactionCounts["heart_circle"] = data.heart
                        self.reactionCounts["tear_circle"] = data.tear
                        self.reactionCounts["thumbsUp_circle"] = data.thumbsUp
                    }
                    
                }
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
}


struct HomeView: View {
    @StateObject private var viewModel = HomeViewModel()
    @EnvironmentObject var placeViewState: PlaceViewState
    @Environment(\.scenePhase) private var scenePhase
    
    var body: some View {
        ZStack {
            OverView()
                .offset(y: 16)
            
            VStack {
                NavBar()
                
                NavigationLink(destination: PlaceView(), isActive: $placeViewState.navigateToPlaceView) {
                    EmptyView()
                }
                
                NavigationLink(destination: MyMindView()) {
                    MyMessageCard(cardType: .displayMessage(viewModel.message, viewModel.reactionCounts), hasMultiple: viewModel.hasMultiple, onSend: {})
                }
                .background(Color.clear)
                
                Spacer()
            }
            
            CustomBottomSheet()
        }
        .background(Color.backgroundPrimary.edgesIgnoringSafeArea(.all))
        .onAppear {
            viewModel.getMyinfo()
        }
        .onChange(of: scenePhase) { newScenePhase in
            if newScenePhase == .active {
                // foreground로 전환될 때 데이터를 새로 고칩니다.
                viewModel.getMyinfo()
            }
        }
    }
}

