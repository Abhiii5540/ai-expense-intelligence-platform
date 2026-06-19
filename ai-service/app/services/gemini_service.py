import asyncio
import json
import logging
import re

import google.generativeai as genai
from google.api_core import exceptions as google_exceptions

from app.config import GEMINI_API_KEY, GEMINI_MODEL
from app.exceptions import GeminiConfigurationError, InsightsGenerationError
from app.prompts.insights_prompt import INSIGHTS_SYSTEM_INSTRUCTION, build_insights_prompt
from app.schemas.insights import Expense

logger = logging.getLogger(__name__)

MIN_INSIGHTS = 3
MAX_INSIGHTS = 5


def _validate_configuration() -> None:
    if not GEMINI_API_KEY:
        raise GeminiConfigurationError("GEMINI_API_KEY is not configured")
    if not GEMINI_MODEL:
        raise GeminiConfigurationError("GEMINI_MODEL is not configured")


def _extract_insights(payload: object) -> list[str]:
    if isinstance(payload, list):
        return [str(insight).strip() for insight in payload if str(insight).strip()]

    if isinstance(payload, dict):
        insights = payload.get("insights")
        if isinstance(insights, list):
            return [str(insight).strip() for insight in insights if str(insight).strip()]

    raise InsightsGenerationError("Gemini response must be a JSON array of insights")


def _sanitize_currency_terms(insight: str) -> str:
    text = re.sub(r"\bU\.?S\.?\s*dollars?\b", "₹", insight, flags=re.IGNORECASE)
    text = re.sub(r"\bUSD\b", "₹", text, flags=re.IGNORECASE)
    return text.replace("$", "₹")


def _parse_insights_response(raw_text: str) -> list[str]:
    text = raw_text.strip()
    if not text:
        raise InsightsGenerationError("Gemini returned an empty response")

    code_block_match = re.search(r"```(?:json)?\s*([\s\S]*?)```", text)
    if code_block_match:
        text = code_block_match.group(1).strip()

    try:
        payload = json.loads(text)
    except json.JSONDecodeError as exc:
        raise InsightsGenerationError("Failed to parse Gemini response as JSON") from exc

    cleaned = [_sanitize_currency_terms(insight) for insight in _extract_insights(payload)]
    if len(cleaned) < MIN_INSIGHTS:
        raise InsightsGenerationError(
            f"Expected at least {MIN_INSIGHTS} insights, received {len(cleaned)}"
        )

    return cleaned[:MAX_INSIGHTS]


def _generate_expense_insights_sync(expenses: list[Expense]) -> list[str]:
    _validate_configuration()

    if not expenses:
        raise InsightsGenerationError("At least one expense is required")

    genai.configure(api_key=GEMINI_API_KEY)
    model = genai.GenerativeModel(
        model_name=GEMINI_MODEL,
        system_instruction=INSIGHTS_SYSTEM_INSTRUCTION,
    )

    try:
        response = model.generate_content(build_insights_prompt(expenses))
    except google_exceptions.GoogleAPIError as exc:
        logger.exception("Gemini API request failed")
        raise InsightsGenerationError("Gemini API request failed") from exc
    except Exception as exc:
        logger.exception("Unexpected error during Gemini request")
        raise InsightsGenerationError("Unexpected error during insight generation") from exc

    if not response.text:
        raise InsightsGenerationError("Gemini returned no text content")

    return _parse_insights_response(response.text)


async def generate_expense_insights(expenses: list[Expense]) -> list[str]:
    return await asyncio.to_thread(_generate_expense_insights_sync, expenses)
