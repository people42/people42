import SwiftUI
import UIKit
import ImageIO
import UniformTypeIdentifiers

// UIkit을 이용해서 투명한 배경을 유지한 상태로 gif를 재생합니다.

// 1. UIkit을 이용해서 WKWebView보다 빠릅니다.
// 2. GIF 스틸컷 기능이 존재합니다. GifImage(name, animated: false)

// 단점. 최소 크기가 좋아함 .frame으로 완전 작게가 불가능하다.

struct GifUIkit: UIViewRepresentable {
    private let name: String
    private let isAnimated: Bool

    init(_ name: String, isAnimated: Bool = true) {
        self.name = name
        self.isAnimated = isAnimated
    }

    func makeUIView(context: Context) -> UIImageView {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        imageView.image = isAnimated ? loadGifImage(name: name) : loadFirstFrame(name: name)
        return imageView
    }

    func updateUIView(_ uiView: UIImageView, context: Context) {
        uiView.image = loadGifImage(name: name)
    }


    func loadGifImage(name: String) -> UIImage? {
        guard let url = Bundle.main.url(forResource: name, withExtension: "gif") else {
            return nil
        }

        guard let imageData = try? Data(contentsOf: url) else {
            return nil
        }

        let options: NSDictionary = [
            kCGImageSourceShouldCache: NSNumber(value: true),
            kCGImageSourceTypeIdentifierHint: UTType.gif.identifier
        ]

        guard let imageSource = CGImageSourceCreateWithData(imageData as CFData, options) else {
            return nil
        }

        let frameCount = CGImageSourceGetCount(imageSource)
        var frames: [UIImage] = []
        var gifDuration = 0.0

        for i in 0..<frameCount {
            if let imageRef = CGImageSourceCreateImageAtIndex(imageSource, i, options) {
                if let properties = CGImageSourceCopyPropertiesAtIndex(imageSource, i, nil) as? NSDictionary,
                    let frameProperties = properties[kCGImagePropertyGIFDictionary] as? NSDictionary,
                    let frameDuration = frameProperties[kCGImagePropertyGIFDelayTime] as? Double {

                    gifDuration += frameDuration
                    frames.append(UIImage(cgImage: imageRef, scale: UIScreen.main.scale, orientation: .up))
                }
            }
        }

        return UIImage.animatedImage(with: frames, duration: gifDuration)
    }


    func loadFirstFrame(name: String) -> UIImage? {
        guard let url = Bundle.main.url(forResource: name, withExtension: "gif") else {
            return nil
        }
        
        guard let imageData = try? Data(contentsOf: url) else {
            return nil
        }
        
        let options: NSDictionary = [
            kCGImageSourceShouldCache: NSNumber(value: true),
            kCGImageSourceTypeIdentifierHint: UTType.gif.identifier
        ]
        
        guard let imageSource = CGImageSourceCreateWithData(imageData as CFData, options) else {
            return nil
        }
        
        if let imageRef = CGImageSourceCreateImageAtIndex(imageSource, 0, options) {
            return UIImage(cgImage: imageRef, scale: UIScreen.main.scale, orientation: .up)
        } else {
            return nil
        }
    }
}
