from pydantic import BaseModel


class Expense(BaseModel):
    amount: float
    category: str
    description: str


class InsightsRequest(BaseModel):
    expenses: list[Expense]


class InsightsResponse(BaseModel):
    insights: list[str]
