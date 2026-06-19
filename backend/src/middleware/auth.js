const { prisma } = require('../config/prisma');
const { ApiError } = require('../utils/errors');
const { verifyAccessToken } = require('../utils/jwt');

async function authenticate(req, res, next) {
  try {
    const header = req.headers.authorization || '';
    const [scheme, token] = header.split(' ');

    if (scheme !== 'Bearer' || !token) {
      throw new ApiError('Unauthorized', 401);
    }

    const decoded = verifyAccessToken(token);
    const userId = decoded.sub;

    if (!userId) {
      throw new ApiError('Unauthorized', 401);
    }

    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: { id: true, name: true, email: true, createdAt: true, updatedAt: true },
    });

    if (!user) {
      throw new ApiError('Unauthorized', 401);
    }

    req.user = user;
    next();
  } catch (err) {
    next(err);
  }
}

module.exports = { authenticate };

