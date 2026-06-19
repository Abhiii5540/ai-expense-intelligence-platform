const aiService = require('../services/ai.service');

async function getInsights(req, res, next) {
  try {
    const result = await aiService.getInsights(req.user.id);
    res.status(200).json(result);
  } catch (err) {
    next(err);
  }
}

module.exports = { getInsights };
