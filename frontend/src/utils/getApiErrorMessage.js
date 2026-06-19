export function getApiErrorMessage(error) {
  const message = error?.response?.data?.error?.message;
  if (message) return message;
  if (error?.message) return error.message;
  return 'Something went wrong. Please try again.';
}
