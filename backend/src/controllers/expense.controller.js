const expenseService = require('../services/expense.service');

async function create(req, res, next) {
  try {
    const expense = await expenseService.createExpense(req.user.id, req.body || {});
    res.status(201).json({ success: true, data: { expense } });
  } catch (err) {
    next(err);
  }
}

async function list(req, res, next) {
  try {
    const result = await expenseService.listExpenses(req.user.id, req.query || {});
    res.status(200).json({ success: true, data: result });
  } catch (err) {
    next(err);
  }
}

async function getById(req, res, next) {
  try {
    const expense = await expenseService.getExpenseById(req.user.id, req.params.id);
    res.status(200).json({ success: true, data: { expense } });
  } catch (err) {
    next(err);
  }
}

async function update(req, res, next) {
  try {
    const expense = await expenseService.updateExpense(req.user.id, req.params.id, req.body || {});
    res.status(200).json({ success: true, data: { expense } });
  } catch (err) {
    next(err);
  }
}

async function remove(req, res, next) {
  try {
    await expenseService.deleteExpense(req.user.id, req.params.id);
    res.status(200).json({ success: true, data: { deleted: true } });
  } catch (err) {
    next(err);
  }
}

module.exports = { create, list, getById, update, remove };

