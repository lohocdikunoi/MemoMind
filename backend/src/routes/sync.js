const router = require('express').Router();
const auth = require('../middleware/auth');
const syncController = require('../controllers/syncController');

router.use(auth);

router.post('/push', syncController.push);
router.get('/pull', syncController.pull);

module.exports = router;