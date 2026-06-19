const { PrismaClient } = require('@prisma/client');

// Prevent exhausting DB connections in dev with nodemon reloads.
const globalForPrisma = global;

if (!globalForPrisma.__prisma) {
  globalForPrisma.__prisma = new PrismaClient();
}

const prisma = globalForPrisma.__prisma;

module.exports = { prisma };

