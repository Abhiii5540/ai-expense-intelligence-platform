import logging

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse

from app.exceptions import GeminiConfigurationError, InsightsGenerationError
from app.routes import insights

logging.basicConfig(level=logging.INFO)

app = FastAPI(
    title="AI Expense Insights Service",
    version="1.0.0",
    description="AI-powered expense analysis and insights",
)

app.include_router(insights.router, prefix="/ai", tags=["insights"])


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.exception_handler(GeminiConfigurationError)
async def gemini_configuration_error_handler(
    _request: Request, exc: GeminiConfigurationError
) -> JSONResponse:
    return JSONResponse(status_code=503, content={"detail": str(exc)})


@app.exception_handler(InsightsGenerationError)
async def insights_generation_error_handler(
    _request: Request, exc: InsightsGenerationError
) -> JSONResponse:
    return JSONResponse(status_code=502, content={"detail": str(exc)})
