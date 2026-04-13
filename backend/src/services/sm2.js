function sm2(quality, repetitions, easeFactor, interval) {
  if (quality < 0 || quality > 5) {
    throw new Error('Quality must be between 0 and 5');
  }

  let newRepetitions = repetitions;
  let newInterval = interval;
  let newEaseFactor = easeFactor;

  if (quality >= 3) {
    if (newRepetitions === 0) {
      newInterval = 1;
    } else if (newRepetitions === 1) {
      newInterval = 6;
    } else {
      newInterval = Math.round(newInterval * newEaseFactor);
    }
    newRepetitions++;
  } else {
    newRepetitions = 0;
    newInterval = 1;
  }

  newEaseFactor = newEaseFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
  if (newEaseFactor < 1.3) {
    newEaseFactor = 1.3;
  }

  const nextReviewDate = new Date();
  nextReviewDate.setDate(nextReviewDate.getDate() + newInterval);

  return {
    repetitions: newRepetitions,
    easeFactor: Math.round(newEaseFactor * 100) / 100,
    interval: newInterval,
    nextReviewDate,
  };
}

module.exports = sm2;