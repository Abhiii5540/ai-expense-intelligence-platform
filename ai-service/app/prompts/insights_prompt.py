import secrets

from app.schemas.insights import Expense

INSIGHTS_SYSTEM_INSTRUCTION = """You are a financial analyst assistant.
Analyze expense data and produce concise, actionable insights for the user.
Each insight must be one clear sentence. Avoid generic advice without referencing the data.
All monetary values are Indian Rupees. Use the ₹ symbol for money and never use $, dollars, or USD.
When the same expense data is analyzed again, vary the wording, emphasis, and analytical angle while staying factually correct."""

INSIGHTS_ANALYSIS_AREAS = [
    "highest spending category",
    "spending patterns",
    "budgeting opportunities",
    "unusual spending",
    "savings opportunities",
    "expense concentration",
    "lifestyle spending habits",
    "category trends",
]

INSIGHTS_STYLE_GUIDES = [
    "Lead with category concentration and compare it with smaller categories.",
    "Lead with budgeting opportunities and practical savings tradeoffs.",
    "Lead with lifestyle spending habits and convenience-driven expenses.",
    "Lead with unusual or standout spending and explain why it matters.",
    "Lead with spending patterns across categories and their relative weight.",
    "Lead with savings opportunities while keeping the tone analytical.",
]

INSIGHTS_RESPONSE_FORMAT = """Return ONLY a valid JSON array with exactly 3 to 5 concise insights and no markdown:
[
  "Insight 1",
  "Insight 2",
  "Insight 3"
]"""

INSIGHTS_PROMPT_TEMPLATE = """Analyze the expenses below and generate financial insights.

Currency rules:
- Treat every amount as Indian Rupees (INR).
- Use the ₹ symbol for every monetary value mentioned.
- Never use $, dollars, or USD.

Vary each response:
- Use the request variation id only to diversify wording and emphasis; do not mention it.
- If this same data was analyzed before, provide a fresh perspective with different phrasing.
- Prioritize the focus lenses in their listed order when possible, but keep every insight grounded in the expense data.
- Follow the response style for this request and avoid repeating the same sentence openings.

Request variation id: {variation_id}
Response style: {style_guide}

Focus lenses:
{analysis_areas}

{response_format}

Expenses:
{expense_lines}"""


def format_expense_lines(expenses: list[Expense]) -> str:
    return "\n".join(
        f"- {expense.category}: ₹{expense.amount:,.2f} ({expense.description})"
        for expense in expenses
    )


def build_insights_prompt(expenses: list[Expense]) -> str:
    start_index = secrets.randbelow(len(INSIGHTS_ANALYSIS_AREAS))
    rotated_areas = INSIGHTS_ANALYSIS_AREAS[start_index:] + INSIGHTS_ANALYSIS_AREAS[:start_index]
    analysis_areas = "\n".join(f"- {area}" for area in rotated_areas)
    return INSIGHTS_PROMPT_TEMPLATE.format(
        variation_id=secrets.token_hex(4),
        style_guide=secrets.choice(INSIGHTS_STYLE_GUIDES),
        analysis_areas=analysis_areas,
        response_format=INSIGHTS_RESPONSE_FORMAT,
        expense_lines=format_expense_lines(expenses),
    )
