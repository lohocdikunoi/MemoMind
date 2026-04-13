const mongoose = require('mongoose');

const cardSchema = new mongoose.Schema({
  deckId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Deck',
    required: true,
    index: true,
  },
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true,
  },
  front: {
    type: String,
    required: true,
  },
  back: {
    type: String,
    required: true,
  },
  easeFactor: {
    type: Number,
    default: 2.5,
  },
  interval: {
    type: Number,
    default: 0,
  },
  repetitions: {
    type: Number,
    default: 0,
  },
  nextReviewDate: {
    type: Date,
    default: Date.now,
  },
  lastReviewDate: {
    type: Date,
    default: null,
  },
}, { timestamps: true });

module.exports = mongoose.model('Card', cardSchema);