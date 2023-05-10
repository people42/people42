import { userLocationUpdateState } from "../../recoil/location/selectors";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useRecoilState } from "recoil";
import styled from "styled-components";

type locationManageProps = {};

function LocationManage({}: locationManageProps) {
  const data = [
    {
      name: "멀티캠퍼스",
      lat: 37.5039,
      lng: 127.0428,
    },
    {
      name: "대전",
      lat: 36.3553309,
      lng: 127.2980942,
    },
    {
      name: "광주",
      lat: 35.2044,
      lng: 126.8066,
    },
    {
      name: "구미",
      lat: 36.1006,
      lng: 128.3913,
    },
    {
      name: "부울경",
      lat: 35.0955,
      lng: 128.856,
    },
  ];
  const [userLocation, setUserLocation] = useRecoilState<TLocation | null>(
    userLocationUpdateState
  );
  const navigate = useNavigate();
  const [selectedidx, setSelectedIdx] = useState(0);
  useEffect(() => {
    const keyEventHandler = (e: KeyboardEvent) => {
      switch (e.key) {
        case "ArrowUp":
          if (0 < selectedidx) {
            setSelectedIdx(selectedidx - 1);
          }
          break;
        case "ArrowDown":
          if (selectedidx < 4) {
            setSelectedIdx(selectedidx + 1);
          }
          break;
        case "Enter":
          setUserLocation({
            latitude: data[selectedidx].lat,
            longitude: data[selectedidx].lng,
          });
          navigate("/");
          break;
      }
    };
    window.addEventListener("keyup", keyEventHandler);
    return () => {
      window.removeEventListener("keyup", keyEventHandler);
    };
  });
  return (
    <StyledLocationManage>
      <h1>위치변경...</h1>
      {data.map((value, idx) => (
        <div>
          <span>{selectedidx === idx ? "☛ " : ""}</span>
          <span>{value.name}</span>
        </div>
      ))}
    </StyledLocationManage>
  );
}

export default LocationManage;

const StyledLocationManage = styled.main`
  box-sizing: border-box;
  padding: 48px;
  background-color: black;
  width: 100vw;
  height: 100vh;
  & > h1 {
    margin-bottom: 8px;
    color: #00ff00;
  }
  & div {
    margin-bottom: 8px;
    & > span {
      color: #00ff00;
    }
  }
`;
