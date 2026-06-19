const http = require('http');
const { createApp } = require('./app');
const { env } = require('./config/env');
const { logger } = require('./utils/logger');

async function start() {
  const app = createApp();
  const server = http.createServer(app);

  server.listen(env.PORT, () => {
    logger.info(`API listening on port ${env.PORT} (${env.NODE_ENV})`);
  });

  const shutdown = async (signal) => {
    try {
      logger.info(`Received ${signal}. Shutting down...`);
      server.close(() => process.exit(0));
    } catch (err) {
      logger.error('Error during shutdown', err);
      process.exit(1);
    }
  };

  process.on('SIGTERM', () => shutdown('SIGTERM'));
  process.on('SIGINT', () => shutdown('SIGINT'));
}

start().catch((err) => {
  logger.error('Failed to start API server', err);
  process.exit(1);
});
