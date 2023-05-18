import SwiftUI
import WebKit

// WKWebView를 이용해서 GIF를 표시합니다.

// 1. GIF는 WKWebView를 이용하기 때문에 흰색 배경이 존재합니다. 투명하게 변경되었음.
// 2. GIF 스틸컷 기능이 존재합니다. GifImage(name, animated: false)

// 단점. GifUIkit보다 로딩되는 속도가 느립니다. 웹뷰라서 성능저하가 있음

struct GIFImageTest: View {
    let emojis = ["alien", "clown-face", "angry-face"]

    var body: some View {
        VStack {
            Text("이모지야 나와라!")
            HStack {
                ForEach(emojis, id: \.self) { el in
                    GifImage(el, isAnimated: false)
                        .frame(width: 40, height: 40)
                }
            }
            HStack {
                ForEach(emojis, id: \.self) { el in
                    GifImage(el, isAnimated: true)
                        .frame(width: 40, height: 40)
                }
            }
        }
    }
}

struct GifImage: View {
    private let name: String
    private let isAnimated: Bool
    
    let baseUrl = "https://peoplemoji.s3.ap-northeast-2.amazonaws.com/emoji/"
    
    init(_ name: String, isAnimated: Bool = true) {
        self.name = name
        self.isAnimated = isAnimated
    }
    
    var body: some View {
        if isAnimated {
            GifImageRepresentable(baseUrl + "animate/" + name + ".gif")
        } else {
            GifImageRepresentable(baseUrl + "static/" + name + ".png")
        }
    }
}

struct GifImageRepresentable: UIViewRepresentable {
    private let imgUrl: String
    
    init(_ imgUrl: String) {
        self.imgUrl = imgUrl
    }
    
    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.backgroundColor = .clear
        webView.scrollView.backgroundColor = .clear
        webView.isOpaque = false
        webView.scrollView.isScrollEnabled = false
        webView.scrollView.bounces = false
        if let url = URL(string: self.imgUrl) {
            let request = URLRequest(url: url)
            webView.load(request)
        }
        return webView
    }


    func updateUIView(_ uiView: WKWebView, context: Context) {
        DispatchQueue.main.async {
            if let url = URL(string: self.imgUrl) {
                let request = URLRequest(url: url)
                uiView.load(request)
            }
        }
    }

}


struct GIFImageTest_Previews: PreviewProvider {
    static var previews: some View {
        GIFImageTest()

    }
}

let profileImgName = ["alien", "angry-face", "anguished-face", "anxious-face-with-sweat", "beaming-face-with-smiling-eyes", "cat-with-tears-of-joy", "cat-with-wry-smile", "clown-face", "cold-face", "confounded-face", "confused-face", "cowboy-hat-face", "crying-cat", "crying-face", "disappointed-face", "disguised-face", "dizzy-face", "downcast-face-with-sweat", "drooling-face", "exploding-head", "face-blowing-a-kiss", "face-exhaling", "face-in-clouds", "face-savoring-food", "face-screaming-in-fear", "face-vomiting", "face-with-hand-over-mouth", "face-with-head-bandage", "face-with-medical-mask", "face-with-monocle", "face-with-open-mouth", "face-with-raised-eyebrow", "face-with-rolling-eyes", "face-with-spiral-eyes", "face-with-steam-from-nose", "face-with-symbols-on-mouth", "face-with-tears-of-joy", "face-with-thermometer", "face-with-tongue", "face-without-mouth", "fearful-face", "flushed-face", "frowning-face-with-open-mouth", "frowning-face", "ghost", "grimacing-face", "grinning-cat-with-smiling-eyes", "grinning-cat", "grinning-face-with-big-eyes", "grinning-face-with-smiling-eyes", "grinning-face-with-sweat", "grinning-face", "grinning-squinting-face", "hot-face", "hugging-face", "hushed-face", "kissing-cat", "kissing-face-with-closed-eyes", "kissing-face-with-smiling-eyes", "kissing-face", "loudly-crying-face", "lying-face", "money-mouth-face", "nauseated-face", "nerd-face", "neutral-face", "partying-face", "pensive-face", "persevering-face", "pile-of-poo", "pleading-face", "pouting-face", "purple-monster", "relieved-face", "robot", "rolling-on-the-floor-laughing", "sad-but-relieved-face", "shushing-face", "skull", "sleeping-face", "sleepy-face", "slightly-frowning-face", "slightly-smiling-face", "smiling-cat-with-heart-eyes", "smiling-face-with-halo", "smiling-face-with-heart-eyes", "smiling-face-with-hearts", "smiling-face-with-horns", "smiling-face-with-smiling-eyes", "smiling-face-with-sunglasses", "smiling-face-with-tear", "smiling-face", "smirking-face", "sneezing-face", "squinting-face-with-tongue", "star-struck", "thinking-face", "tired-face", "unamused-face", "upside-down-face", "weary-cat", "weary-face", "winking-face-with-tongue", "winking-face", "woozy-face", "worried-face", "yawning-face", "zany-face", "zipper-mouth-face"]
