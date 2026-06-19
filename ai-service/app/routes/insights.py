from fastapi import APIRouter

from app.schemas.insights import InsightsRequest, InsightsResponse
from app.services.gemini_service import generate_expense_insights

router = APIRouter()


@router.post("/insights", response_model=InsightsResponse)
async def generate_insights(request: InsightsRequest) -> InsightsResponse:
    insights = await generate_expense_insights(request.expenses)
    return InsightsResponse(insights=insights)
