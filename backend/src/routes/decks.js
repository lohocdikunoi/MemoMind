const router = require('express').Router();
const auth = require('../middleware/auth');
const deckController = require('../controllers/deckController');

router.use(auth);

router.get('/', deckController.getAll);
router.post('/', deckController.create);
router.put('/:id', deckController.update);
router.delete('/:id', deckController.remove);

module.exports = router;