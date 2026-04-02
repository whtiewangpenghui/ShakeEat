import { MODE_OPTIONS, REJECT_FEEDBACK_OPTIONS, STATUS_OPTIONS } from "./constants";

export function formatTime(value) {
  if (!value) {
    return "-";
  }
  return value.replace("T", " ").slice(0, 19);
}

export function formatPrice(value) {
  if (value === null || value === undefined || value === "") {
    return "-";
  }
  const number = Number(value);
  return Number.isNaN(number) ? value : number.toFixed(0);
}

export function mapModeLabel(mode) {
  return MODE_OPTIONS.find((item) => item.value === mode)?.label || mode;
}

export function mapStatusLabel(status) {
  return STATUS_OPTIONS.find((item) => item.value === status)?.label || status;
}

export function mapRejectFeedbackLabel(code) {
  return REJECT_FEEDBACK_OPTIONS.find((item) => item.value === code)?.label || code;
}

export function normalizeFoodForm(food) {
  return {
    id: food.id || null,
    name: food.name || "",
    emoji: food.emoji || "",
    steps: food.steps || "",
    imageUrl: food.imageUrl || "",
    orderUrl: food.orderUrl || "",
    weight: food.weight ?? 80,
    referencePrice: food.referencePrice ?? 20,
    tagIds: food.tagIds || []
  };
}

export function getTagNames(tags, tagIds) {
  if (!tagIds || tagIds.length === 0) {
    return [];
  }
  return tagIds.map((tagId) => tags.find((tag) => tag.id === tagId)?.name || `标签${tagId}`);
}
