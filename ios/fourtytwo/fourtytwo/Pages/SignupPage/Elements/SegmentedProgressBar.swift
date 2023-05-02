import SwiftUI

struct SegmentedProgressBar: View {
    let segments: Int
    let progress: CGFloat
    let availableWidth: CGFloat
    
    var body: some View {
        GeometryReader { geometry in
            HStack(spacing: 20) {
                ForEach(0..<self.segments) { index in
                    Capsule()
                        .fill(self.getSegmentColor(index: index))
                        .frame(width: self.getSegmentWidth(), height: 4)
                }
            }
        }
        .frame(height: 10)
    }
    
    func getSegmentColor(index: Int) -> Color {
        if CGFloat(index + 1) <= self.progress {
            return Color.blue
        } else {
            return Color.gray
        }
    }
    
    func getSegmentWidth(geometry: GeometryProxy) -> CGFloat {
        let segmentWidth = (geometry.size.width - CGFloat(self.segments - 1) * 4) / CGFloat(self.segments)
        return segmentWidth
    }
    
    func getOffset(geometry: GeometryProxy) -> CGFloat {
        let segmentWidth = (geometry.size.width - CGFloat(self.segments - 1) * 4) / CGFloat(self.segments)
        let offset = segmentWidth * (self.progress - 1) + CGFloat(self.progress - 1) * 4
        return offset
    }
    
    func getSegmentWidth() -> CGFloat {
        let segmentWidth = (availableWidth - 20 * CGFloat(segments - 1)) / CGFloat(segments)
        return segmentWidth
    }
}
