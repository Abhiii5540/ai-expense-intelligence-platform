const express = require('express');
const { authRouter } = require('./auth.routes');
const { expenseRouter } = require('./expense.routes');
const { aiRouter } = require('./ai.routes');

const apiRouter = express.Router();

apiRouter.get('/health', (req, res) => {
  res.status(200).json({
    status: 'ok',
    uptime: process.uptime(),
    timestamp: new Date().toISOString(),
  });
});

apiRouter.use('/api/auth', authRouter);
apiRouter.use('/api/expenses', expenseRouter);
apiRouter.use('/api/ai', aiRouter);

module.exports = { apiRouter };

