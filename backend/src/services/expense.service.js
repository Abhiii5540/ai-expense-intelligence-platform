const { prisma } = require('../config/prisma');
const { ApiError } = require('../utils/errors');

function parsePagination({ page, limit }) {
  const p = Number(page);
  const l = Number(limit);
  const pageNum = Number.isFinite(p) && p > 0 ? Math.floor(p) : 1;
  const limitNumRaw = Number.isFinite(l) && l > 0 ? Math.floor(l) : 10;
  const limitNum = Math.min(limitNumRaw, 100);
  return { page: pageNum, limit: limitNum, skip: (pageNum - 1) * limitNum, take: limitNum };
}

function normalizeExpenseInput({ amount, description, category, expenseDate }, { partial = false } = {}) {
  const data = {};

  if (!partial || amount !== undefined) {
    const n = Number(amount);
    if (!Number.isFinite(n) || n <= 0) throw new ApiError('Amount must be a positive number', 400);
    data.amount = n.toFixed(2); // Prisma Decimal accepts string
  }

  if (!partial || expenseDate !== undefined) {
    const d = new Date(expenseDate);
    if (!(d instanceof Date) || Number.isNaN(d.getTime())) throw new ApiError('expenseDate must be a valid date', 400);
    data.expenseDate = d;
  }

  if (description !== undefined) data.description = description === null ? null : String(description);
  if (category !== undefined) data.category = category === null ? null : String(category);

  return data;
}

async function createExpense(userId, payload) {
  const data = normalizeExpenseInput(payload, { partial: false });
  const expense = await prisma.expense.create({
    data: { ...data, userId },
  });
  return expense;
}

async function listExpenses(userId, query) {
  const { page, limit, skip, take } = parsePagination(query);
  const where = { userId };
  if (query.category) where.category = String(query.category);

  const [items, total] = await Promise.all([
    prisma.expense.findMany({
      where,
      orderBy: { expenseDate: 'desc' },
      skip,
      take,
    }),
    prisma.expense.count({ where }),
  ]);

  return {
    items,
    page,
    limit,
    total,
    totalPages: Math.max(1, Math.ceil(total / limit)),
  };
}

async function getExpenseById(userId, expenseId) {
  const expense = await prisma.expense.findFirst({
    where: { id: expenseId, userId },
  });
  if (!expense) throw new ApiError('Expense not found', 404);
  return expense;
}

async function updateExpense(userId, expenseId, payload) {
  // Validate ownership + existence first
  await getExpenseById(userId, expenseId);

  const data = normalizeExpenseInput(payload, { partial: true });
  if (Object.keys(data).length === 0) {
    throw new ApiError('No fields to update', 400);
  }

  const expense = await prisma.expense.update({
    where: { id: expenseId },
    data,
  });

  // Safety check: never allow cross-user update even if id leaked
  if (expense.userId !== userId) throw new ApiError('Expense not found', 404);
  return expense;
}

async function deleteExpense(userId, expenseId) {
  // Validate ownership + existence first
  await getExpenseById(userId, expenseId);

  const expense = await prisma.expense.delete({ where: { id: expenseId } });
  if (expense.userId !== userId) throw new ApiError('Expense not found', 404);
  return expense;
}

module.exports = {
  createExpense,
  listExpenses,
  getExpenseById,
  updateExpense,
  deleteExpense,
};

