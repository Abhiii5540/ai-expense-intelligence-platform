const axios = require('axios');

const { prisma } = require('../config/prisma');
const { env } = require('../config/env');
const { ApiError } = require('../utils/errors');

function toFastApiExpense(expense) {
  return {
    amount: Number(expense.amount),
    category: expense.category || 'Uncategorized',
    description: expense.description || '',
  };
}

async function fetchUserExpenses(userId) {
  return prisma.expense.findMany({
    where: { userId },
    orderBy: { expenseDate: 'desc' },
  });
}

async function getInsights(userId) {
  const expenses = await fetchUserExpenses(userId);

  if (!expenses.length) {
    throw new ApiError('At least one expense is required to generate insights', 400);
  }

  const payload = {
    expenses: expenses.map(toFastApiExpense),
  };

  try {
    const response = await axios.post(`${env.AI_SERVICE_URL}/ai/insights`, payload, {
      timeout: 30000,
      headers: { 'Content-Type': 'application/json' },
    });

    const insights = response.data?.insights;
    if (!Array.isArray(insights)) {
      throw new ApiError('AI service returned an invalid response', 502);
    }

    return { insights };
  } catch (err) {
    if (err instanceof ApiError) {
      throw err;
    }

    if (axios.isAxiosError(err)) {
      const status = err.response?.status;
      const detail = err.response?.data?.detail;

      if (err.code === 'ECONNREFUSED' || err.code === 'ENOTFOUND') {
        throw new ApiError('AI service is unavailable', 503);
      }

      if (status === 503) {
        throw new ApiError(detail || 'AI service is not configured', 503);
      }

      if (status === 502) {
        throw new ApiError(detail || 'Failed to generate insights', 502);
      }

      if (status === 422) {
        throw new ApiError('Invalid expense data sent to AI service', 400);
      }

      throw new ApiError(detail || 'AI service request failed', status || 502);
    }

    throw new ApiError('Unexpected error while generating insights', 500);
  }
}

module.exports = { getInsights };
