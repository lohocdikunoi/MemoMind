const Deck = require('../models/Deck');
const Card = require('../models/Card');

exports.getAll = async (req, res) => {
  try {
    const decks = await Deck.find({ userId: req.userId }).sort({ updatedAt: -1 });
    res.json(decks);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.create = async (req, res) => {
  try {
    const { name, description } = req.body;
    if (!name) {
      return res.status(400).json({ error: 'Tên bộ thẻ là bắt buộc' });
    }

    const deck = await Deck.create({
      userId: req.userId,
      name,
      description: description || '',
    });
    res.status(201).json(deck);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.update = async (req, res) => {
  try {
    const { name, description } = req.body;
    const deck = await Deck.findOneAndUpdate(
      { _id: req.params.id, userId: req.userId },
      { name, description },
      { new: true }
    );
    if (!deck) {
      return res.status(404).json({ error: 'Bộ thẻ không tồn tại' });
    }
    res.json(deck);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.remove = async (req, res) => {
  try {
    const deck = await Deck.findOneAndDelete({ _id: req.params.id, userId: req.userId });
    if (!deck) {
      return res.status(404).json({ error: 'Bộ thẻ không tồn tại' });
    }
    await Card.deleteMany({ deckId: deck._id });
    res.json({ message: 'Đã xóa bộ thẻ' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};