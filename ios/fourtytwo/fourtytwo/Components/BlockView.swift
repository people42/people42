import SwiftUI


struct BlockView: View {
    @Environment(\.presentationMode) var presentationMode

    @State var blockReason: String = ""
    
    @State private var showAlert: Bool = false
    @State private var alertMessage: String = ""
    
    let nickname: String
    let userIdx: Int
    
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
                            .foregroundColor(Color("Text"))
                        HStack {
                            Text("유저를")
                                .font(.customHeader6)
                                .foregroundColor(Color("Text"))
                            (
                            Text("차단")
                                .font(.customHeader6)
                                .foregroundColor(Color(.systemRed)) +
                            Text("하시겠습니까?")
                                .font(.customHeader6)
                                .foregroundColor(Color("Text"))
                            )
                        }
                    }
                    .padding()
                    Spacer()
                }

                HStack {
                    VStack(alignment: .leading) {
                        Text("이후 해당 유저를 식별하기 위해 차단 사유를 메모하세요.")
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
                    TopAlignedTextEditor(text: $blockReason)
                        .padding()
                        .background(Color(.systemBackground))
                        .cornerRadius(24)
                        .overlay(
                            Text("차단 사유를 메모하세요.")
                                .foregroundColor(blockReason.isEmpty ? .gray : .clear)
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

                    Button("차단") {
                        // 차단 기능 구현
                        blockUser()
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
    
    private func blockUser() {
        let data = ["userIdx": userIdx]
        AccountService.blockUser(data: data) { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    
                    if response.status == 409 {
                        alertMessage = "이미 차단된 사용자입니다."
                    } else {
                        alertMessage = "차단이 완료되었습니다."
                    }
                    showAlert = true // 알림창을 표시
                    
                }
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
    
}

struct BlockView_Previews: PreviewProvider {
    static var previews: some View {
        BlockView(nickname: "사람", userIdx: 1)
    }
}
