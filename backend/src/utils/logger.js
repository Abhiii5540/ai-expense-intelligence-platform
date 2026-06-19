function formatMeta(meta) {
  if (!meta) return '';
  if (meta instanceof Error) return `\n${meta.stack || meta.message}`;
  try {
    return `\n${JSON.stringify(meta)}`;
  } catch {
    return `\n${String(meta)}`;
  }
}

function log(level, message, meta) {
  const ts = new Date().toISOString();
  const line = `[${ts}] ${level.toUpperCase()} ${message}${formatMeta(meta)}`;
  const stream = level === 'error' || level === 'warn' ? process.stderr : process.stdout;

  stream.write(`${line}\n`);
}

const logger = {
  info: (msg, meta) => log('info', msg, meta),
  warn: (msg, meta) => log('warn', msg, meta),
  error: (msg, meta) => log('error', msg, meta),
};

module.exports = { logger };
