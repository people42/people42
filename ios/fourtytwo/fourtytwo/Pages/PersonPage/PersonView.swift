import SwiftUI
import MapKit
import CoreLocation

// 자료 구조
//struct PersonPlaces: Codable {
//    let brushCnt: Int
//    let userIdx: Int
//    let nickname: String
//    let placeResDtos: [PlaceResDtos]
//}
//
//struct PlaceResDtos: Codable {
//    let placeIdx: Int
//    let placeName: String
//    let placeLatitude: Int
//    let placeLongitude: Int
//    let brushCnt: Int
//}

struct PersonView: View {
    @Environment(\.presentationMode) var presentationMode
    @Environment(\.scenePhase) private var scenePhase
    
    @State var location: CLLocationCoordinate2D
    @State var personPlaces: PersonPlaces?
    
    let userIdx: Int?
    
    
    var body: some View {
        ZStack {
            
            map
                .ignoresSafeArea()
            

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
                Text("NickName")
                    .font(.system(size: 18))
                    .fontWeight(.semibold)
            }
        }
        .onAppear()
    }
    
    private var map: some View {
        Map(coordinateRegion: .constant(MKCoordinateRegion(center: location, latitudinalMeters: 500, longitudinalMeters: 500)), interactionModes: [.pan, .zoom], showsUserLocation: false)
    }


}

extension PersonView {
    private func getPersonFeed() {
        if let userIdx = userIdx {
            let data: [String: Int] = ["userIdx": userIdx]
            FeedService.getPersonFeed(data: data) { result in
                switch result {
                case .success(let response):
                    if let responseData = response.data {
                        DispatchQueue.main.async {
                            personPlaces = responseData
                        }
                    }
                case .failure(let error):
                    print(error.localizedDescription)
                }
            }
        }
    }
}

struct PersonView_Previews: PreviewProvider {
    static var previews: some View {
        PersonView(location: CLLocationCoordinate2D(latitude: 37.5665, longitude: 126.9780), userIdx: 1)
    }
}
