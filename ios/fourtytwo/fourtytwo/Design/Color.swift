import SwiftUI

// Color가 아닌 View를 반환하기 때문에 기본은 background에만 적용 가능하다.
struct DynamicColor: View {
    @Environment(\.colorScheme) var colorScheme

    var light: Color
    var dark: Color

    var body: some View {
        colorScheme == .dark ? dark : light
    }
}

// DynamicColor가 Color를 반환하도록 color() 메서드를 추가한다. ColorScheme 반응을 안함
extension DynamicColor {
    func color(for colorScheme: ColorScheme) -> Color {
        colorScheme == .dark ? dark : light
    }
}


// 첫 번째 확장: Color 초기화에 사용되는 init(hex:alpha:)를 정의합니다.
extension Color {
    init(hex: String, alpha: Double = 1.0) {
        let scanner = Scanner(string: hex)
        var rgbValue: UInt64 = 0
        scanner.scanHexInt64(&rgbValue)

        self.init(
            .sRGB,
            red: Double((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: Double((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: Double(rgbValue & 0x0000FF) / 255.0,
            opacity: alpha
        )
    }
}

// 두 번째 확장: 컬러 목록을 정의합니다.
extension Color {
    
    // 고정 컬러
    
    // 모노톤
    static let monotoneLight = Color(hex: "FFFFFF")
    static let monotoneLightGray = Color(hex: "EFEFEF")
    static let monotoneGray = Color(hex: "A8A8A8")
    static let monotoneDark = Color(hex: "6E6E6E")
    static let monotoneLightTranslucent = Color(hex: "FFFFFF", alpha: 0.5)
    static let monotoneTextTranslucent = Color(hex: "151515", alpha: 0.35)
    
    // 프라이머리
    static let PrimaryBlue = Color(hex: "3644FC")
    static let PrimaryRed = Color(hex: "FF375B")
    static let PrimaryYellow = Color(hex: "FBFF32")
    
    // 퍼스널
    static let PersonalWhite = Color(hex: "FFFFFF", alpha: 0.2)
    static let PersonalGray = Color(hex: "E4E4E4", alpha: 0.2)
    
    // 퍼스널 라이트
    static let PersonalRedLight = Color(hex: "FB4C4C", alpha: 0.6)
    static let PersonalOrangeLight = Color(hex: "F59626", alpha: 0.6)
    static let PersonalYellowLight = Color(hex: "FFF500", alpha: 0.6)
    static let PersonalGreenLight = Color(hex: "00C637", alpha: 0.6)
    static let PersonalSkyLight = Color(hex: "0EC1CC", alpha: 0.6)
    static let PersonalBlueLight = Color(hex: "167BD8", alpha: 0.6)
    static let PersonalPurpleLight = Color(hex: "A344DE", alpha: 0.6)
    static let PersonalPinkLight = Color(hex: "DE44A0", alpha: 0.6)
    
    // 퍼스널 다크
    static let PersonalRedDark = Color(hex: "4E1C25", alpha: 0.6)
    static let PersonalOrangeDark = Color(hex: "4E3D25", alpha: 0.6)
    static let PersonalYellowDark = Color(hex: "4E4D25", alpha: 0.6)
    static let PersonalGreenDark = Color(hex: "1B4F3C", alpha: 0.6)
    static let PersonalSkyDark = Color(hex: "1B4364", alpha: 0.6)
    static let PersonalBlueDark = Color(hex: "1B2D58", alpha: 0.6)
    static let PersonalPurpleDark = Color(hex: "3B1C58", alpha: 0.6)
    static let PersonalPinkDark = Color(hex: "4E1C47", alpha: 0.6)
    
    
    // 변동 컬러 - 백그라운드
    static let backgroundPrimary = DynamicColor(light: Color(hex: "FAFAFA"), dark: Color(hex: "22232E"))
    static let backgroundSecondary = DynamicColor(light: Color(hex: "FFFFFF"), dark: Color(hex: "404151"))
    static let text = DynamicColor(light: Color(hex: "151515"), dark: Color(hex: "FFFFFF"))
    static let cardRed = DynamicColor(light: Color(hex: "FB4C4C"), dark: Color(hex: "4E1C25"))
    static let cardOrange = DynamicColor(light: Color(hex: "F59626"), dark: Color(hex: "4E3D25"))
    static let cardYellow = DynamicColor(light: Color(hex: "FFF500"), dark: Color(hex: "4E4D25"))
    static let cardGreen = DynamicColor(light: Color(hex: "00C637"), dark: Color(hex: "1B4F3C"))
    static let cardSky = DynamicColor(light: Color(hex: "0EC1CC"), dark: Color(hex: "1B4364"))
    static let cardBlue = DynamicColor(light: Color(hex: "167BD8"), dark: Color(hex: "1B2D58"))
    static let cardPurple = DynamicColor(light: Color(hex: "A344DE"), dark: Color(hex: "3B1C58"))
    static let cardPink = DynamicColor(light: Color(hex: "DE44A0"), dark: Color(hex: "4E1C47"))


}


// 사용법
//Text("Hello, World!")
//    .foregroundColor(Color.myColor.colo())
//
//Text("Another text")
//    .backgroundColor(Color.mySecondColor)
