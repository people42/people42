import SwiftUI
import MapKit
import CoreLocation

struct EmojiAnnotation: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
    let image: String
    let userIdx: Int
    let placeIdx: Int
    let placeName: String
    let brushCnt: Int

    var annotation: some View {
        ZStack {
            GifImage(image)
                .frame(width: 40, height: 40)
            
            ZStack {
                Circle()
                    .fill(Color.red)
                    .frame(width: 20, height: 20)
                Text("\(brushCnt)")
                    .foregroundColor(.white)
            }
            .offset(x: 15, y: -15) // 원 위치 조정
        }
    }
}

struct PersonView: View {
    @Environment(\.presentationMode) var presentationMode
    @Environment(\.scenePhase) private var scenePhase
    
    @State var location: CLLocationCoordinate2D
    @State var personPlaces: PersonPlaces?
    
    @State private var emojiAnnotations: [EmojiAnnotation] = []
    @State private var mapBoundary: (minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) = (0, 0, 0, 0)
    
    @State private var selectedAnnotation: EmojiAnnotation?
    
    private var showPlacePersonView: Bool {
        selectedAnnotation != nil
    }
    
    @State private var selectedUserIdx: Int = 0
    @State private var selectedPlaceIdx: Int = 0
    @State private var selectedPlaceName: String = ""

    let userIdx: Int?
    
    var body: some View {
        ZStack {
            VStack {
                header
                map
                    .edgesIgnoringSafeArea(.bottom)
            }

        }
        .background(Color.backgroundPrimary.edgesIgnoringSafeArea(.all))
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(Color("Text"))
                }
            }
            ToolbarItem(placement: .principal) {
                Text(personPlaces?.nickname ?? "nickname")
                    .font(.system(size: 18))
                .fontWeight(.semibold)
            }
        }
        .onAppear {
            getPersonPlaces()
        }

    }
    
    private var header: some View {
        return Group {
            if let brushCount = personPlaces?.brushCnt {
                VStack {
                    HStack(alignment: .bottom) {
                        Spacer()
                        Text("\(brushCount)")
                            .font(.customHeader4)
                            .foregroundColor(Color("Text"))
                        Text("번의 인연")
                            .font(.system(size: 14))
                            .fontWeight(.medium)
                            .padding(.bottom, 6)
                        Spacer()
                    }
                    .frame(height:40)
                }
            }
        }
    }
    
    private var map: some View {
        let mapSpan = calculateMapSpan(minLat: mapBoundary.minLat, maxLat: mapBoundary.maxLat, minLon: mapBoundary.minLon, maxLon: mapBoundary.maxLon)
        let coordinateRegion = MKCoordinateRegion(center: location, span: mapSpan)

        return Map(coordinateRegion: .constant(coordinateRegion), interactionModes: [.pan, .zoom], showsUserLocation: false, annotationItems: emojiAnnotations) { annotation in
            MapAnnotation(coordinate: annotation.coordinate, content: {
                annotation.annotation
                    .onTapGesture {
                        selectedAnnotation = nil
                        selectedAnnotation = annotation
                    }
            })
        }
        .sheet(isPresented: Binding(get: { showPlacePersonView }, set: { newValue in
            if !newValue {
                selectedAnnotation = nil
            }
        })) {
            if let personPlaces = personPlaces,
               let selectedAnnotation = selectedAnnotation {
                PlacePersonView(userIdx: selectedAnnotation.userIdx, placeIdx: selectedAnnotation.placeIdx, placeName: selectedAnnotation.placeName, profileImage: personPlaces.emoji, nickname: personPlaces.nickname)
            }
        }

    }


}

extension PersonView {
    private func getPersonPlaces() {
        if let userIdx = userIdx {
            let data: [String: Int] = ["userIdx": userIdx]
            FeedService.getPersonFeed(data: data) { result in
                switch result {
                case .success(let response):
                    if let responseData = response.data {
                        DispatchQueue.main.async {
                            personPlaces = responseData
                            emojiAnnotations = responseData.placeResDtos.map { place in
                                EmojiAnnotation(coordinate: CLLocationCoordinate2D(latitude: CLLocationDegrees(place.placeLatitude), longitude: CLLocationDegrees(place.placeLongitude)), image: personPlaces?.emoji ?? "defaultEmoji", userIdx: responseData.userIdx, placeIdx: place.placeIdx, placeName: place.placeName, brushCnt: place.brushCnt)
                            }

                            // 지도 중심 및 축척 업데이트
                            let mapBoundary = findMapBoundary(annotations: emojiAnnotations)
                            let mapCenter = calculateMapCenter(minLat: mapBoundary.minLat, maxLat: mapBoundary.maxLat, minLon: mapBoundary.minLon, maxLon: mapBoundary.maxLon)
                            let mapSpan = calculateMapSpan(minLat: mapBoundary.minLat, maxLat: mapBoundary.maxLat, minLon: mapBoundary.minLon, maxLon: mapBoundary.maxLon)
                            self.mapBoundary = mapBoundary
                            self.location = mapCenter
                        }
                    }
                case .failure(let error):
                    print(error.localizedDescription)
                }
            }
        }
    }
    
    // 최대/최소 위도, 경도 게산
    private func findMapBoundary(annotations: [EmojiAnnotation]) -> (minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) {
        var minLat = 90.0
        var maxLat = -90.0
        var minLon = 180.0
        var maxLon = -180.0

        for annotation in annotations {
            let lat = annotation.coordinate.latitude
            let lon = annotation.coordinate.longitude

            minLat = min(minLat, lat)
            maxLat = max(maxLat, lat)
            minLon = min(minLon, lon)
            maxLon = max(maxLon, lon)
        }

        return (minLat, maxLat, minLon, maxLon)
    }
    
    // 지도의 중심 좌표 계산
    private func calculateMapCenter(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) -> CLLocationCoordinate2D {
        let centerLat = (minLat + maxLat) / 2
        let centerLon = (minLon + maxLon) / 2

        return CLLocationCoordinate2D(latitude: centerLat, longitude: centerLon)
    }
    
    // 지도의 축척 조정
    private func calculateMapSpan(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) -> MKCoordinateSpan {
        let latDelta = maxLat - minLat + 0.05 // 0.05를 추가하여 마커 주변에 여유 공간을 확보합니다.
        let lonDelta = maxLon - minLon + 0.05

        return MKCoordinateSpan(latitudeDelta: latDelta, longitudeDelta: lonDelta)
    }


}




struct PersonView_Previews: PreviewProvider {
    static var previews: some View {
        PersonView(location: CLLocationCoordinate2D(latitude: 37.5665, longitude: 126.9780), personPlaces: samplePersonPlaces, userIdx: 1)
    }

    static let samplePersonPlaces = PersonPlaces(
        brushCnt: 5,
        userIdx: 1,
        nickname: "Sample User",
        emoji: "sampleEmoji",
        placeResDtos: [
            PlaceResDtos(placeIdx: 1, placeName: "Sample Place 1", placeLatitude: 37.5665, placeLongitude: 126.9780, brushCnt: 3),
            PlaceResDtos(placeIdx: 2, placeName: "Sample Place 2", placeLatitude: 37.5675, placeLongitude: 126.9790, brushCnt: 2),
            PlaceResDtos(placeIdx: 3, placeName: "Sample Place 3", placeLatitude: 37.5685, placeLongitude: 126.9800, brushCnt: 1)
        ]
    )
}

