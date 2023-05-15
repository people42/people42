export const socketInit = (
  type: "guest" | "user",
  userData: TSocketUserData,
  onMessage: Function,
  userIdx?: number
) => {
  const socket: WebSocket = new WebSocket(
    `wss://www.people42.com/be42/socket?type=${type}${
      userIdx ? `&user_idx=${userIdx}` : ""
    }`
  );

  socket.onopen = () => {
    if (process.env.NODE_ENV === "development") {
      console.log("socket onopen");
    }
    sendMessage(socket, "INIT", userData);
  };

  socket.onmessage = (event: any) => {
    if (process.env.NODE_ENV === "development") {
      console.log("socket onmessage", JSON.parse(event.data));
    }
    onMessage(JSON.parse(event.data));
  };

  socket.onerror = (error: any) => {
    console.error("WebSocket error:", error);
  };

  socket.onclose = (event: any) => {
    if (process.env.NODE_ENV === "development") {
      console.log("socket onclose", JSON.parse(event.data));
    }
  };

  return socket;
};

export const sendMessage = (socket: WebSocket, method: any, data: any) => {
  const payload = {
    method: method,
    ...data,
  };
  socket.send(JSON.stringify(payload));
};

export const sendPong = (socket: WebSocket) => {
  socket.send(
    JSON.stringify({
      method: "PONG",
    })
  );
};

export const changeStatus = (socket: WebSocket, userData: TSocketUserData) => {
  sendMessage(socket, "CHANGE_STATUS", userData);
};

export const handleClose = (socket: WebSocket) => {
  sendMessage(socket, "CLOSE", null);
};

export const handleMessageChanged = (
  socket: WebSocket,
  userData: TSocketUserData
) => {
  sendMessage(socket, "MESSAGE_CHANGED", userData);
};

export const handleMove = (socket: WebSocket, userData: TSocketUserData) => {
  userData.latitude = userData.latitude;
  userData.longitude = userData.longitude;
  sendMessage(socket, "MOVE", userData);
};

export const socketInfoReceive = (data: TSocketReceive) => {
  let nearUserMap = new Map<number, TSocketNearUser>();
  let guestCnt = 0;
  data.data.nearUsers.forEach((value: TSocketNearUser) => {
    switch (value.type) {
      case "user":
        nearUserMap.set(value.userIdx, value);
        break;

      default:
        guestCnt++;
        break;
    }
  });
  return { nearUserMap, guestCnt };
};
export const socketNearReceive = (data: TSocketReceive) => {
  console.log("Receive Near");
};
export const socketFarReceive = (data: TSocketReceive) => {
  console.log("Receive Far");
};
export const socketChangeStatusReceive = (data: TSocketReceive) => {
  console.log("Receive Change Status");
};
