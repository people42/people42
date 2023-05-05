import SwiftUI

struct AlertView: View {
    @Environment(\.presentationMode) var presentationMode
    
    // Dummy data
    let dummyData = [
        (contents: "First alert", reactionNum: 0, time: Date()),
        (contents: "Second alert", reactionNum: 1, time: Date()),
        (contents: "Third alert", reactionNum: 2, time: Date()),
        (contents: "Fourth alert", reactionNum: 3, time: Date()),
        (contents: "Fifth alert", reactionNum: 0, time: Date())
    ]

    var body: some View {
        VStack {
            ScrollView {
                VStack {
                    ForEach(dummyData, id: \.contents) { data in
                        AlertCard(viewModel: AlertCardViewModel(reactionNum: data.reactionNum, time: data.time))
                    }
                }
            }
        }
        .padding(.horizontal, 8)
        .background(Color.backgroundPrimary.edgesIgnoringSafeArea(.all))
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .regular))
                        .foregroundColor(Color("Text"))
                }
            }
            ToolbarItem(placement: .principal) {
                Text("새로운 알림 \(dummyData.count)건")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }
    }
}

struct AlertView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            AlertView()
        }
    }
}
