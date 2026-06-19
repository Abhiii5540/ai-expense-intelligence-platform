const express = require('express');
const cors = require('cors');
const morgan = require('morgan');

const { env } = require('./config/env');
const { requestId } = require('./middleware/requestId');
const { apiRouter } = require('./routes');
const { notFound } = require('./middleware/notFound');
const { errorHandler } = require('./middleware/errorHandler');

function createApp() {
  const app = express();

  app.disable('x-powered-by');
  app.set('trust proxy', true);

  app.use(express.json({ limit: '1mb' }));
  app.use(express.urlencoded({ extended: true }));

  app.use(requestId);
  app.use(
    morgan(env.NODE_ENV === 'production' ? 'combined' : 'dev', {
      skip: (req) => req.path === '/health',
    })
  );

  app.use(
    cors({
      origin: env.CORS_ORIGIN?.length ? env.CORS_ORIGIN : true,
      credentials: true,
    })
  );

  app.use('/', apiRouter);

  app.use(notFound);
  app.use(errorHandler);

  return app;
}

module.exports = { createApp };

