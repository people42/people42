import SwiftUI


struct ReportView: View {
    @Environment(\.presentationMode) var presentationMode

    @State var reportReason: String = ""
    
    @State private var showAlert: Bool = false
    @State private var alertMessage: String = ""
    
    let nickname: String
    let messageIdx: Int
    
    var body: some View {
        ZStack {
            Color("BgPrimary")
                .edgesIgnoringSafeArea(.bottom)
                .onTapGesture {
                    hideKeyboard()
                }
            
            VStack {
                HStack {
                    VStack(alignment: .leading){
                        Text("\(nickname )")
                            .font(.customHeader6)
                        HStack {
                            Text("유저의 글을")
                                .font(.customHeader6)
                            (
                            Text("신고")
                                .font(.customHeader6)
                                .foregroundColor(Color(.systemRed)) +
                            Text("하시겠습니까?")
                                .font(.customHeader6)
                            )
                        }
                    }
                    .foregroundColor(Color("Text"))
                    .padding()
                    Spacer()
                }

                HStack {
                    VStack(alignment: .leading) {
                        Text("부적절한 게시물을 판별하기 위해 신고 사유를 메모하세요.")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                            .multilineTextAlignment(.leading)

                        Text("메모된 내용은 공유되지 않습니다.")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                            .multilineTextAlignment(.leading)
                    }
                    .padding(.leading, 16)
                    Spacer()
                }

                ZStack {
                    TopAlignedTextEditor(text: $reportReason)
                        .padding()
                        .background(Color("BgSecondary"))
                        .cornerRadius(24)
                        .overlay(
                            Text("신고 사유를 메모하세요.")
                                .foregroundColor(reportReason.isEmpty ? .gray : .clear)
                                .padding(.all, 8)
                        )
                }
                .padding()

                Spacer()

                HStack {
                    
                    Button("취소") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .font(.customButton)
                    .padding(12)
                    .padding(.horizontal, 24)
                    .background(Color(.systemGray5))
                    .cornerRadius(32)
                    .foregroundColor(.white)

                    Button("신고") {
                        // 신고 기능 구현
                        reportMessage()
                    }
                    .font(.customButton)
                    .padding(12)
                    .padding(.horizontal, 24)
                    .background(Color(.systemRed))
                    .cornerRadius(32)
                    .foregroundColor(.white)
                }
                .padding(.horizontal)
                .padding(.bottom)
            }
            .padding(.top)
            .frame(maxHeight: 600)
        }
        .onAppear() {
            UITextView.appearance().backgroundColor = .clear
        }
        .alert(isPresented: $showAlert) {
            Alert(title: Text(alertMessage), dismissButton: .default(Text("확인")) {
                presentationMode.wrappedValue.dismiss()
            })
        }
    }
    
    private func hideKeyboard() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
    
    private func reportMessage() {
        let data: [String: Any] = [
            "messageIdx": messageIdx,
            "content": reportReason
        ]
        AccountService.reportMessage(data: data) { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    
                    if response.status == 409 {
                        alertMessage = "이미 신고된 게시글입니다."
                    } else {
                        alertMessage = "신고가 완료되었습니다."
                    }
                    showAlert = true
                    
                }
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
    
}

struct ReportView_preview: PreviewProvider {
    static var previews: some View {
        ReportView(nickname: "사람", messageIdx: 1)
    }
}
