import SwiftUI


class AlertCardViewModel: ObservableObject {
    @Published var contents: String = "누군가 회원님의 생각을 응원해요"
    @Published var reactionNum: Int
    @Published var time: Date
    @Published var reactionType = ["fire", "heart", "tear", "thumbsUp"]

    init(reactionNum: Int, time: Date) {
        self.reactionNum = reactionNum
        self.time = time
    }

    func timeDifference(from date: Date) -> String {
        let now = Date()
        let components = Calendar.current.dateComponents([.second, .minute, .hour, .day], from: date, to: now)
        
        if let days = components.day, days > 0 {
            return "\(days)일 전"
        } else if let hours = components.hour, hours > 0 {
            return "\(hours)시간 전"
        } else if let minutes = components.minute, minutes > 0 {
            return "\(minutes)분 전"
        } else {
            return "방금전"
        }
    }
}


struct AlertCard: View {
    @ObservedObject var viewModel: AlertCardViewModel
    
    var body: some View {
        VStack {
            HStack {
                Image(viewModel.reactionType[viewModel.reactionNum])
                    .scaleEffect(0.8)
                    .frame(width: 40, height: 40)
                Text(viewModel.contents)
                    .font(.customBody2)
                    .padding(EdgeInsets(top: 4, leading: 16, bottom: 16, trailing: 16))
                Spacer()
                Text(viewModel.timeDifference(from: viewModel.time))
                    .font(.customOverline)
                    .padding(.top, 16)
            }
            .padding(.bottom, 8)
            Divider()
        }
        .padding(.horizontal, 16)
        .padding(.top, 8)
    }
}

struct AlertCard_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            AlertCard(viewModel: AlertCardViewModel(reactionNum: 2, time: Date()))
            AlertCard(viewModel: AlertCardViewModel(reactionNum: 1, time: Date()))
        }
    }
}

