export const socketInit = (
  type: "guest" | "user",
  userData: TSocketUserData,
  onMessage: Function,
  userIdx?: number
) => {
  const socket: WebSocket = new WebSocket(
    `wss://www.people42.com/be42/socket?type=${type}${
      userIdx ? `&user_idx=${userIdx}` : null
    }`
  );

  socket.onopen = () => {
    console.log("Connected to server");
    sendMessage(socket, "INIT", userData);
  };

  socket.onmessage = (event: any) => {
    console.log("Received data:", event.data);
    onMessage(event.data);
  };

  socket.onerror = (error: any) => {
    console.error("WebSocket error:", error);
  };

  socket.onclose = (event: any) => {
    console.log("Disconnected from server:", event);
  };

  return socket;
};

export const sendMessage = (socket: WebSocket, method: any, data: any) => {
  const payload = {
    method: method,
    ...data,
  };
  console.log(payload);
  socket.send(JSON.stringify(payload));
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
