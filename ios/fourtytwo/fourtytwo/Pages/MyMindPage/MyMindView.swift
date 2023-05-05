import SwiftUI

struct HistoryData {
    let messageIdx: Int
    let contents: String
    let reactionCnt: [Int]
    let time: Date?
}

struct MyMindView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var userState: UserState
    @State var myHistoryData: [HistoryData]?
    
    var body: some View {
        VStack {
            MyMessageCard(cardType: .writeMessage, hasMultiple: false, onSend: {getMyHistory()})
            ScrollView {
                LazyVStack {
                    if let history = myHistoryData {
                        ForEach(history.indices, id: \.self) { index in
                            let data = history[index]
                            DragToDeleteView(content: MyMassageHistory(contents: data.contents, reactionCnt: data.reactionCnt, time: data.time), onDelete: {
                                deleteMessage(at: index)
                            })
                            .padding(.bottom, 8)
                        }
                    } else {
                        Text("생각을 추가해주세요")
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                    }
                }
            }
        }
        .onTapGesture {
             // 키보드가 표시된 상태라면 키보드를 닫도록 함
             UIApplication.shared.endEditing()
         }
        .padding(.top, 0)
        .background(Color.backgroundPrimary.edgesIgnoringSafeArea(.all))
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(Color("Text"))
                }
            }
            ToolbarItem(placement: .principal) {
                Text("나의 생각 기록")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }
        .onAppear{
            getMyHistory()
        }
    }
}

extension MyMindView {
    private func getMyHistory() {
        print("getMyHistory")
        let currentDate = Date()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        let dateString = dateFormatter.string(from: currentDate)
        let data : [String : String] = ["date" : dateString]
        
        AccountService.getMyHistory(data: data) { result in
            switch result {
            case .success(let response):
                if let data = response.data {
                    
                    DispatchQueue.main.async {
                        myHistoryData = data.map { el in
                            print(el)
                            let dateString = el.createdAt // createdAt: "2023-04-28T09:44:55.238698"
                            
                            let dateFormatter = DateFormatter()
                            dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
                            
                            let date = dateFormatter.date(from: dateString)
                            
                            return HistoryData(messageIdx: el.messageIdx, contents: el.content, reactionCnt: [el.fire, el.heart, el.tear, el.thumbsUp], time: date)
                        }
                    }

                }
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
    
    private func deleteMessage(at index: Int) {
        print("삭제!")
        guard let historyIdx = myHistoryData?[index].messageIdx else { return }
        
        let data: [String: Any] = ["messageIdx": historyIdx]

        AccountService.deleteMessage(data: data) { result in
            switch result {
            case .success(_):
                print("Message deleted!")
                DispatchQueue.main.async {
                    myHistoryData?.remove(at: index)
                }
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
}

// UIApplication extension을 사용하여 키보드를 닫는 함수 추가
extension UIApplication {
    func endEditing() {
        sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}

struct MyMindView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            MyMindView()
        }
    }
}
