import SwiftUI

struct NavLink<Content: View>: View {
    var title: String
    var page: Content

    var body: some View {
        NavigationLink(destination: page) {
            HStack {
                Text(title)
                    .foregroundColor(Color("Text"))
                Spacer()
            }
        }
    }
}
