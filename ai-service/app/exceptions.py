class GeminiConfigurationError(Exception):
    """Raised when required Gemini configuration is missing or invalid."""


class InsightsGenerationError(Exception):
    """Raised when insight generation or response parsing fails."""
