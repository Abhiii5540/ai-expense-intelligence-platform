const authService = require('../services/auth.service');

async function signup(req, res, next) {
  try {
    const { name, email, password } = req.body || {};
    const result = await authService.signup({ name, email, password });
    res.status(201).json({ success: true, data: result });
  } catch (err) {
    next(err);
  }
}

async function login(req, res, next) {
  try {
    const { email, password } = req.body || {};
    const result = await authService.login({ email, password });
    res.status(200).json({ success: true, data: result });
  } catch (err) {
    next(err);
  }
}

async function me(req, res) {
  res.status(200).json({ success: true, data: { user: req.user } });
}

module.exports = { signup, login, me };

