import { MessageCard } from "../../../../components";
import { locationInfoState } from "../../../../recoil/location/atoms";
import { formatMessageDate } from "../../../../utils";
import HomeTimelineCard from "./HomeTimelineCard";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useRecoilValue } from "recoil";
import styled from "styled-components";

interface homeTimeLineGroupProps {
  props: TFeed["new"];
  idx: number;
}

function HomeTimelineGroup({ props, idx }: homeTimeLineGroupProps) {
  const [isActive, setIsActive] = useState(false);
  const locationInfo = useRecoilValue<TLocationInfo | null>(locationInfoState);
  useEffect(() => {
    if (
      locationInfo?.placeIdx &&
      props?.placeWithTimeInfo.placeIdx &&
      locationInfo?.placeIdx == props?.placeWithTimeInfo.placeIdx &&
      idx == 0
    ) {
      setIsActive(true);
    }
  }, [locationInfo]);

  const navigate = useNavigate();
  const onClickCard = () => {
    navigate("place", {
      state: {
        placeIdx: props?.placeWithTimeInfo.placeIdx,
        time: props?.placeWithTimeInfo.time,
        size: 10,
      },
    });
  };

  return (
    <StyledHomeTimelineGroup isActive={isActive}>
      <div className="location">
        <div
          className="location-info"
          style={{ animationDelay: `${0.1 * idx}s` }}
        >
          <p className="location-info-name">
            {props ? `${props.placeWithTimeInfo.placeName} 근처` : ""}
          </p>
          <p className="location-info-time">
            {props
              ? isActive
                ? "현재 위치"
                : formatMessageDate(props.placeWithTimeInfo.time)
              : ""}
          </p>
        </div>
        {props ? (
          <div
            className="location-dot"
            style={{
              animationDelay: `${0.1 * idx}s`,
            }}
          ></div>
        ) : (
          <div
            className="location-dot"
            style={{
              animationDelay: `${0.1 * idx}s`,
              backgroundColor: "gray",
            }}
          ></div>
        )}
      </div>
      {props?.recentUsersInfo ? (
        <HomeTimelineCard
          idx={idx}
          props={props}
          onClick={onClickCard}
        ></HomeTimelineCard>
      ) : (
        <div className="not-message">
          <p>아직 메시지가 없습니다.</p>
        </div>
      )}
    </StyledHomeTimelineGroup>
  );
}

export default HomeTimelineGroup;

const StyledHomeTimelineGroup = styled.article<{ isActive: boolean }>`
  display: flex;
  align-items: start;
  margin-left: 16px;
  margin-block: 16px;

  .location {
    flex-shrink: 0;
    margin-top: 18px;
    display: flex;
    text-align: end;

    &-info {
      animation: floatingLeft 0.3s;
      animation-fill-mode: both;
      &-name {
        flex-shrink: 0;
        ${({ theme }) => theme.text.overline}
        width: 96px;
        flex-grow: 1;
        word-wrap: break-word;
        word-break: keep-all;
        color: ${(props) =>
          props.isActive
            ? props.theme.color.text.primary
            : props.theme.color.text.secondary};
      }
      &-time {
        width: 96px;
        flex-shrink: 0;
        ${({ theme }) => theme.text.body2}
        color: ${(props) =>
          props.isActive
            ? props.theme.color.text.primary
            : props.theme.color.text.secondary};
      }
    }

    &-dot {
      animation: popIn 0.3s;
      animation-fill-mode: both;
      flex-shrink: 0;
      margin: 8px;
      width: 16px;
      height: 16px;
      border: white 2px solid;
      border-radius: 12px;
      background-color: ${({ theme }) => theme.color.brand.blue};
      ${({ theme }) => theme.shadow.iconShadow};
    }
  }

  .not-message {
    & > p {
      color: ${({ theme }) => theme.color.text.secondary};
      margin-block: 8px;
    }
    & > a {
      color: ${({ theme }) => theme.color.text.secondary};
    }
    height: 64px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: start;
  }
`;
