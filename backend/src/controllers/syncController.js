const Deck = require('../models/Deck');
const Card = require('../models/Card');

exports.push = async (req, res) => {
  try {
    const { decks = [], cards = [], deletedDeckIds = [], deletedCardIds = [] } = req.body;

    const results = { decks: [], cards: [] };

    for (const deck of decks) {
      if (deck.serverId) {
        const updated = await Deck.findOneAndUpdate(
          { _id: deck.serverId, userId: req.userId },
          { name: deck.name, description: deck.description, cardCount: deck.cardCount },
          { new: true }
        );
        if (updated) results.decks.push(updated);
      } else {
        const created = await Deck.create({
          userId: req.userId,
          name: deck.name,
          description: deck.description || '',
          cardCount: deck.cardCount || 0,
        });
        results.decks.push({ ...created.toObject(), localId: deck.localId });
      }
    }

    for (const card of cards) {
      if (card.serverId) {
        const updated = await Card.findOneAndUpdate(
          { _id: card.serverId, userId: req.userId },
          {
            front: card.front,
            back: card.back,
            easeFactor: card.easeFactor,
            interval: card.interval,
            repetitions: card.repetitions,
            nextReviewDate: card.nextReviewDate,
            lastReviewDate: card.lastReviewDate,
          },
          { new: true }
        );
        if (updated) results.cards.push(updated);
      } else {
        const deck = await Deck.findOne({ _id: card.deckServerId, userId: req.userId });
        if (!deck) continue;

        const created = await Card.create({
          deckId: deck._id,
          userId: req.userId,
          front: card.front,
          back: card.back,
          easeFactor: card.easeFactor || 2.5,
          interval: card.interval || 0,
          repetitions: card.repetitions || 0,
          nextReviewDate: card.nextReviewDate || new Date(),
          lastReviewDate: card.lastReviewDate || null,
        });
        results.cards.push({ ...created.toObject(), localId: card.localId });
      }
    }

    for (const id of deletedDeckIds) {
      await Deck.findOneAndDelete({ _id: id, userId: req.userId });
      await Card.deleteMany({ deckId: id });
    }
    for (const id of deletedCardIds) {
      await Card.findOneAndDelete({ _id: id, userId: req.userId });
    }

    res.json(results);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

exports.pull = async (req, res) => {
  try {
    const since = req.query.since ? new Date(req.query.since) : new Date(0);

    const decks = await Deck.find({
      userId: req.userId,
      updatedAt: { $gt: since },
    });

    const cards = await Card.find({
      userId: req.userId,
      updatedAt: { $gt: since },
    });

    res.json({ decks, cards, serverTime: new Date().toISOString() });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};