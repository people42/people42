import SwiftUI

extension Font {
    static let customHeader1 = Font.system(size: 96, weight: .bold, design: .default)
    static let customHeader2 = Font.system(size: 60, weight: .bold, design: .default)
    static let customHeader3 = Font.system(size: 48, weight: .bold, design: .default)
    static let customHeader4 = Font.system(size: 34, weight: .bold, design: .default)
    static let customHeader5 = Font.system(size: 24, weight: .bold, design: .default)
    static let customHeader6 = Font.system(size: 20, weight: .bold, design: .default)
    static let customSubtitle1 = Font.system(size: 16, weight: .medium, design: .default)
    static let customSubtitle2 = Font.system(size: 14, weight: .medium, design: .default)
    static let customBody1 = Font.system(size: 16, weight: .regular, design: .default)
    static let customBody2 = Font.system(size: 14, weight: .regular, design: .default)
    static let customButton = Font.system(size: 14, weight: .bold, design: .default)
    static let customCaption = Font.system(size: 12, weight: .regular, design: .default)
    static let customOverline = Font.system(size: 10, weight: .bold, design: .default)
}

// 사용법
//Text("Headline")
//    .font(.customHeadline)
//
//Text("Body text")
//    .font(.customBody)
//
//Text("Caption")
//    .font(.customCaption)
