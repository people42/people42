import SwiftUI

struct LocationCardData {
    let time: String
    let nickname: String
    let userCnt: Int
    let placeIdx: Int
    let placeName: String
    let old: [String]
    let new: [String]
}

struct LocationCard: View {
    @EnvironmentObject var placeViewState: PlaceViewState
    
    let oldContent = ["우리 만난 적이 있을지도?", "또, 너냐?", "안녕하세요", "오랜만이예요", "지금, 뭐하세요?", "정들었을지도 모르겠어", "왜 여기 있어?"]
    let newContent = ["초면이네요. 안녕하신가요.", "안녕하세요", "이 어플 재밌다.", "제가 여기 있어요.", "여길 보세요", "새로운 얼굴이구만", "또 봅시다. 그럼 이만!"]
    
    @State var locationCardData: LocationCardData
    
    @State private var isHighlighted = false
    @State private var isClicked = false
    
    var body: some View {
        ZStack {
            ZStack {
                RoundedRectangle(cornerRadius: 24)
                    .foregroundColor(Color("BgSecondary"))
                    .shadow(color: Color.black.opacity(0.2), radius: 4, x: 4, y: 4)
                    .shadow(color: Color.black.opacity(0.1), radius: 4, x: -2, y: -1)
            }

            VStack {
                VStack(alignment: .leading, spacing: 16) {
                    
                    HStack {
                        
                        Text(getTimeStringFromISODate(locationCardData.time))
                            .font(.customOverline)
                        
                        Spacer()
                        
                        Text(locationCardData.placeName)
                            .font(.customOverline)
                        
                    }
                    
                    HStack {
                        Spacer()
                        
                        if locationCardData.userCnt-1 == 0 {
                            Text("\(locationCardData.nickname)님과 마주쳤어요.")
                                .font(.customBody2)
                                .lineLimit(1)
                            .padding(.bottom, 16)
                        } else {
                            Text("\(locationCardData.nickname)님 외 \(locationCardData.userCnt-1)명과 마주쳤어요.")
                                .font(.customBody2)
                                .lineLimit(1)
                            .padding(.bottom, 16)
                        }
                    }
                    
                    if !locationCardData.old.isEmpty {
                        VStack(alignment: .leading) {
                            Text(oldContent.randomElement()!)
                                .font(.system(size: 12))
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(RoundedRectangle(cornerRadius: 32)
                                    .foregroundColor(Color.gray).opacity(0.3))
                            
                            HStack(spacing: 16) {
                                ForEach(0..<min(locationCardData.old.count, 7), id: \.self) { i in
                                    GifImage(locationCardData.old[i], isAnimated: false)
                                        .frame(width: 20, height: 18)
                                }
                            }
                            .padding(.horizontal, 4)
                        }
                    }

                    if !locationCardData.new.isEmpty {
                        VStack(alignment: .leading) {
                            Text(newContent.randomElement()!)
                                .font(.system(size: 12))
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(RoundedRectangle(cornerRadius: 32)
                                    .foregroundColor(Color.gray).opacity(0.3))
                            
                            HStack(spacing: 16) {
                                ForEach(0..<min(locationCardData.new.count, 7), id: \.self) { i in
                                    GifImage(locationCardData.new[i], isAnimated: false)
                                        .frame(width: 20, height: 18)
                                }
                            }
                            .padding(.horizontal, 4)
                        }
                    }
                    
                    HStack {
                        Spacer()
                        
                        if !isClicked {
                            Text("눌러서 확인하기")
                                .font(.customOverline)
                                .foregroundColor(.gray.opacity(isHighlighted ? 1.0 : 0.3))
                                .opacity(isHighlighted ? 1.0 : 0.3)
                                .animation(Animation.easeInOut(duration: 1.0).repeatForever(autoreverses: true))
                                .onAppear() {
                                    self.isHighlighted.toggle()
                                }
                        }
                        
                    }
                    .frame(height: 0)

                }
                .padding(.vertical, 16)
                .padding(.horizontal, 32)
                
            }
        }
        .onTapGesture(perform: {
            isClicked = true
            placeViewState.selectedPlaceID = locationCardData.placeIdx
            placeViewState.navigateToPlaceView = true
            placeViewState.placeDate = locationCardData.time
        })
        .padding(16)
    }
}

