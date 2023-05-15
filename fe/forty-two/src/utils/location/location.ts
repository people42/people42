/**
 * Remove Refresh Token in Cookie
 */
export const getUserLocation = async () => {
  return new Promise((success) => {
    navigator.geolocation.getCurrentPosition(success, () => null, {
      enableHighAccuracy: true,
      timeout: 5000,
      maximumAge: 0,
    });
  });
};
