const Card = require('../models/Card');
const Deck = require('../models/Deck');
const sm2 = require('../services/sm2');

exports.getByDeck = async (req, res) => {
  try {
    const cards = await Card.find({
      deckId: req.params.deckId,
      userId: req.userId,
    }).sort({ createdAt: -1 });
    res.json(cards);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.create = async (req, res) => {
  try {
    const { front, back } = req.body;
    if (!front || !back) {
      return res.status(400).json({ error: 'Mặt trước và mặt sau là bắt buộc' });
    }

    const deck = await Deck.findOne({ _id: req.params.deckId, userId: req.userId });
    if (!deck) {
      return res.status(404).json({ error: 'Bộ thẻ không tồn tại' });
    }

    const card = await Card.create({
      deckId: req.params.deckId,
      userId: req.userId,
      front,
      back,
    });

    await Deck.findByIdAndUpdate(req.params.deckId, { $inc: { cardCount: 1 } });

    res.status(201).json(card);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.update = async (req, res) => {
  try {
    const { front, back } = req.body;
    const card = await Card.findOneAndUpdate(
      { _id: req.params.id, userId: req.userId },
      { front, back },
      { new: true }
    );
    if (!card) {
      return res.status(404).json({ error: 'Thẻ không tồn tại' });
    }
    res.json(card);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.remove = async (req, res) => {
  try {
    const card = await Card.findOneAndDelete({ _id: req.params.id, userId: req.userId });
    if (!card) {
      return res.status(404).json({ error: 'Thẻ không tồn tại' });
    }
    await Deck.findByIdAndUpdate(card.deckId, { $inc: { cardCount: -1 } });
    res.json({ message: 'Đã xóa thẻ' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.getReviewCards = async (req, res) => {
  try {
    const cards = await Card.find({
      deckId: req.params.deckId,
      userId: req.userId,
      nextReviewDate: { $lte: new Date() },
    }).sort({ nextReviewDate: 1 });
    res.json(cards);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.submitReview = async (req, res) => {
  try {
    const { quality } = req.body;
    if (quality === undefined || quality < 0 || quality > 5) {
      return res.status(400).json({ error: 'Quality phải từ 0-5' });
    }

    const card = await Card.findOne({ _id: req.params.id, userId: req.userId });
    if (!card) {
      return res.status(404).json({ error: 'Thẻ không tồn tại' });
    }

    const result = sm2(quality, card.repetitions, card.easeFactor, card.interval);

    card.repetitions = result.repetitions;
    card.easeFactor = result.easeFactor;
    card.interval = result.interval;
    card.nextReviewDate = result.nextReviewDate;
    card.lastReviewDate = new Date();
    await card.save();

    res.json(card);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};