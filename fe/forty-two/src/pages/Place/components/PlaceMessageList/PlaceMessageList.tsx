import { MessageCard } from "../../../../components";
import { placeState } from "../../../../recoil/place/atoms";
import Skeleton from "react-loading-skeleton";
import { useNavigate } from "react-router";
import { useRecoilState, useRecoilValue } from "recoil";
import styled from "styled-components";

type placeMessageListProps = {};

function PlaceMessageList({}: placeMessageListProps) {
  const placeData = useRecoilValue(placeState);
  const navigate = useNavigate();

  return (
    <StyledPlaceMessageList>
      
      {placeData ? (
        placeData?.messagesInfo.map((data, idx) => (
          <MessageCard
            key={`place-message-card-${idx}`}
            onClick={() => navigate(`/user/${data.userIdx}`)}
            props={{
              recentMessageInfo: data,
              placeWithTimeInfo: {
                placeIdx: placeData.placeWithTimeAndGpsInfo.placeIdx,
                placeName: placeData.placeWithTimeAndGpsInfo.placeName,
                time: placeData.placeWithTimeAndGpsInfo.time,
              },
            }}
            idx={idx}
          ></MessageCard>
        ))
      ) : (
        <Skeleton
          baseColor="#8686861f"
          highlightColor="#86868622"
          width={200}
          height={80}
          borderRadius={24}
        ></Skeleton>
      )}
      
    </StyledPlaceMessageList>
  );
}

export default PlaceMessageList;

const StyledPlaceMessageList = styled.div`
  & > div {
    padding-block: 4px;
  }
`;
