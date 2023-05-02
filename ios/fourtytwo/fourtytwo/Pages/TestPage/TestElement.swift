import SwiftUI

struct TestElement: View {
    var text: String = "희상"
    
    var body: some View {
        Text(text)
    }
}

struct TestElement_Previews: PreviewProvider {
    static var previews: some View {
        TestElement(text: "바보")
    }
}
