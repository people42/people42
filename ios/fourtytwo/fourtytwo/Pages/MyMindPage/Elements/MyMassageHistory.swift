import SwiftUI

struct MyMassageHistory: View {
    var contents: String
    var reactionCnt: [Int]
    var time: Date?
    
    let reactionType = ["fire", "heart", "tear", "thumbsUp"]

    
    func formattedDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy년 MM월 dd일 HH시 mm분"
        return formatter.string(from: date)
    }
    
    var body: some View {
        ZStack {
            ZStack {
                RoundedRectangle(cornerRadius: 24)
                    .foregroundColor(Color("BgSecondary"))
                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: -2, y: -1)
            }

            HStack {
                VStack {
                    
                    VStack(alignment: .leading, spacing: 16) {
                        
                        if let time = time {
                            Text(formattedDate(time))
                                .font(.customOverline)
                        } else {
                            Text("시간 없음")
                        }
                        
                        Text(contents)
                            .font(.customBody2)
                            .lineLimit(nil)
                            .fixedSize(horizontal: false, vertical: true)
                            .padding(.bottom, 16)
                        
                        HStack(spacing: 40) {
                            ForEach(0..<4, id: \.self) { i in
                                HStack(spacing: 16) {
                                    Image(reactionType[i])
                                        .scaleEffect(0.3)
                                        .frame(width: 12, height: 10)
                                    Text("\(reactionCnt[i])")
                                        .font(.customOverline)
                                }
                            }
                        }
                    }
                    .padding(.vertical, 16)
                    .padding(.horizontal, 24)
                    
                }
                Spacer()
            }
        }
        .padding(16)
    }
}

struct MyMassageHistory_Previews: PreviewProvider {
    static var previews: some View {
        MyMassageHistory(contents: "ContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContentContent", reactionCnt: [2,2,2,2], time: Date())
    }
}
