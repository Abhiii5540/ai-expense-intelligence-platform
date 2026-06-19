const express = require('express');

const expenseController = require('../controllers/expense.controller');
const { authenticate } = require('../middleware/auth');

const router = express.Router();

router.use(authenticate);

router.post('/', expenseController.create);
router.get('/', expenseController.list);
router.get('/:id', expenseController.getById);
router.put('/:id', expenseController.update);
router.delete('/:id', expenseController.remove);

module.exports = { expenseRouter: router };

