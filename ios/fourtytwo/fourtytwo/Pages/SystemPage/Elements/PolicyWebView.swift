import SwiftUI

struct PolicyWebView: View {
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        VStack{
            WebView(url: "https://people42.com/policy?nav=false")
        }
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
                Text("설정")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }
    }
}

struct PolicyWebView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView{
            PolicyWebView()
        }
    }
}
