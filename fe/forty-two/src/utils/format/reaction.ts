interface RatingObject {
  fire: number;
  heart: number;
  thumbsUp: number;
  tear: number;
}

export function relativeReactionObject(obj: RatingObject): RatingObject {
  const max = Math.max(...Object.values(obj));
  const min = Math.min(...Object.values(obj));
  const result: RatingObject = { fire: 0, heart: 0, thumbsUp: 0, tear: 0 };

  for (let key in obj) {
    result[key as keyof RatingObject] = Number(
      (obj[key as keyof RatingObject] > 0
        ? ((obj[key as keyof RatingObject] - min) / (max - min)) * 0.6 + 0.4
        : 0
      ).toFixed(2)
    );
  }

  return result;
}
