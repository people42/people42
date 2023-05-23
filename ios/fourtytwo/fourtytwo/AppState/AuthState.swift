import SwiftUI

class SignUpState: ObservableObject {
    private let keychain = KeychainSwift()
    @Published var email: String = ""
    @Published var nickname: String = ""
    @Published var oAuthToken: String = ""
    @Published var emoji: String = ""
    @Published var loginType: LoginType = .none {
        didSet {
            saveLoginType()
        }
    }
    
    init() {
        loadLoginType()
    }
    
    enum LoginType: String {
        case google
        case apple
        case none
    }

    // Save the current loginType to keychain
    private func saveLoginType() {
        keychain.set(loginType.rawValue, forKey: "loginType")
    }

    // Load loginType from keychain
    private func loadLoginType() {
        if let storedLoginType = keychain.get("loginType"), let loginType = LoginType(rawValue: storedLoginType) {
            self.loginType = loginType
        }
    }
}

class UserState: ObservableObject {
    
    private let keychain = KeychainSwift()
    private let accessTokenKey = "access_token"
    private let refreshTokenKey = "refresh_token"
    private let userIdxKey = "user_idx"
    private let emailKey = "email"
    private let nicknameKey = "nickname"
    private let emojiKey = "emoji"

    @Published var user_idx: Int? {
        didSet {
            if let user_idx = user_idx {
                keychain.set(String(user_idx), forKey: userIdxKey)
            } else {
                keychain.delete(userIdxKey)
            }
        }
    }

    @Published var email: String? {
        didSet {
            if let email = email {
                keychain.set(email, forKey: emailKey)
            } else {
                keychain.delete(emailKey)
            }
        }
    }

    @Published var nickname: String? {
        didSet {
            if let nickname = nickname {
                keychain.set(nickname, forKey: nicknameKey)
            } else {
                keychain.delete(nicknameKey)
            }
        }
    }
    
    @Published var emoji: String? {
        didSet {
            if let emoji = emoji {
                keychain.set(emoji, forKey: emojiKey)
            } else {
                keychain.delete(emojiKey)
            }
        }
    }

    @Published var accessToken: String = "" {
        didSet {
            keychain.set(accessToken, forKey: accessTokenKey)
        }
    }

    @Published var refreshToken: String = "" {
        didSet {
            keychain.set(refreshToken, forKey: refreshTokenKey)
        }
    }
    
    init() {
        accessToken = keychain.get(accessTokenKey) ?? ""
        refreshToken = keychain.get(refreshTokenKey) ?? ""
        user_idx = Int(keychain.get(userIdxKey) ?? "")
        email = keychain.get(emailKey)
        nickname = keychain.get(nicknameKey)
        emoji = keychain.get(emojiKey)
    }
    
    func update() {
        accessToken = keychain.get(accessTokenKey) ?? ""
        refreshToken = keychain.get(refreshTokenKey) ?? ""
        user_idx = Int(keychain.get(userIdxKey) ?? "")
        email = keychain.get(emailKey)
        nickname = keychain.get(nicknameKey)
        emoji = keychain.get(emojiKey)
    }
}


