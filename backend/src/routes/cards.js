const router = require('express').Router();
const auth = require('../middleware/auth');
const cardController = require('../controllers/cardController');

router.use(auth);

router.get('/decks/:deckId/cards', cardController.getByDeck);
router.post('/decks/:deckId/cards', cardController.create);
router.get('/decks/:deckId/review', cardController.getReviewCards);
router.put('/cards/:id', cardController.update);
router.delete('/cards/:id', cardController.remove);
router.post('/cards/:id/review', cardController.submitReview);

module.exports = router;