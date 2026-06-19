const { logger } = require('../utils/logger');
const { env } = require('../config/env');

function errorHandler(err, req, res, next) {
  // eslint-disable-next-line no-unused-vars
  const _ = next;

  const statusCode = Number(err.statusCode || err.status || 500);
  const isServerError = statusCode >= 500;

  const payload = {
    success: false,
    error: {
      message: err.message || 'Internal Server Error',
      ...(env.NODE_ENV !== 'production' ? { stack: err.stack, requestId: req.id } : { requestId: req.id }),
    },
  };

  if (isServerError) {
    logger.error(`Unhandled error (${req.method} ${req.originalUrl})`, err);
  } else {
    logger.warn(`Request error (${req.method} ${req.originalUrl})`, err.message);
  }

  res.status(statusCode).json(payload);
}

module.exports = { errorHandler };

