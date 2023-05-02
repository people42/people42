import SwiftUI
import UIKit

struct CustomTextField: UIViewRepresentable {
    @Binding var text: String
    var isFirstResponder: Bool = false

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIView(context: Context) -> UITextField {
        let textField = UITextField()
        textField.delegate = context.coordinator
        return textField
    }

    func updateUIView(_ uiView: UITextField, context: Context) {
        uiView.text = text
        if isFirstResponder && !context.coordinator.didBecomeFirstResponder {
            uiView.becomeFirstResponder()
            context.coordinator.didBecomeFirstResponder = true
        }
    }

    class Coordinator: NSObject, UITextFieldDelegate {
        var parent: CustomTextField
        var didBecomeFirstResponder = false

        init(_ textField: CustomTextField) {
            self.parent = textField
        }

        func textFieldDidChangeSelection(_ textField: UITextField) {
            parent.text = textField.text ?? ""
        }
    }
}
