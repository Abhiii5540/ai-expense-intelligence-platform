const bcrypt = require('bcryptjs');
const validator = require('validator');

const { prisma } = require('../config/prisma');
const { ApiError } = require('../utils/errors');
const { signAccessToken } = require('../utils/jwt');

const PASSWORD_MIN_LEN = 6;

function toPublicUser(user) {
  return {
    id: user.id,
    name: user.name,
    email: user.email,
    createdAt: user.createdAt,
    updatedAt: user.updatedAt,
  };
}

async function signup({ name, email, password }) {
  if (!email || !validator.isEmail(String(email))) {
    throw new ApiError('Invalid email', 400);
  }
  if (!password || String(password).length < PASSWORD_MIN_LEN) {
    throw new ApiError(`Password must be at least ${PASSWORD_MIN_LEN} characters`, 400);
  }

  const normalizedEmail = validator.normalizeEmail(String(email)) || String(email).toLowerCase();

  const existing = await prisma.user.findUnique({ where: { email: normalizedEmail }, select: { id: true } });
  if (existing) {
    throw new ApiError('User already exists', 409);
  }

  const passwordHash = await bcrypt.hash(String(password), 12);

  const user = await prisma.user.create({
    data: {
      name: name ? String(name) : null,
      email: normalizedEmail,
      password: passwordHash,
    },
  });

  const token = signAccessToken({ sub: user.id, email: user.email });

  return { user: toPublicUser(user), token };
}

async function login({ email, password }) {
  if (!email || !validator.isEmail(String(email))) {
    throw new ApiError('Invalid email', 400);
  }
  if (!password) {
    throw new ApiError('Password is required', 400);
  }

  const normalizedEmail = validator.normalizeEmail(String(email)) || String(email).toLowerCase();

  const user = await prisma.user.findUnique({ where: { email: normalizedEmail } });
  if (!user) {
    throw new ApiError('Invalid email or password', 401);
  }

  const ok = await bcrypt.compare(String(password), user.password);
  if (!ok) {
    throw new ApiError('Invalid email or password', 401);
  }

  const token = signAccessToken({ sub: user.id, email: user.email });

  return { user: toPublicUser(user), token };
}

module.exports = { signup, login };

