import { MODE_OPTIONS, REJECT_FEEDBACK_OPTIONS, STATUS_OPTIONS, EMPTY_FOOD_FORM } from './constants';

export function formatTime(value) {
  if (!value) {
    return '-';
  }
  return String(value).replace('T', ' ').slice(0, 19);
}

export function formatPrice(value) {
  if (value === null || value === undefined || value === '') {
    return '-';
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

export function normalizeFoodForm(food = {}) {
  return {
    id: food.id || null,
    name: food.name || '',
    emoji: food.emoji || '',
    steps: food.steps || '',
    imageUrl: food.imageUrl || '',
    orderUrl: food.orderUrl || '',
    weight: food.weight ?? 80,
    referencePrice: food.referencePrice ?? 20,
    tagIds: food.tagIds || []
  };
}

export function resetFoodForm() {
  return JSON.parse(JSON.stringify(EMPTY_FOOD_FORM));
}

export function getTagNames(tags, tagIds) {
  if (!Array.isArray(tagIds) || tagIds.length === 0) {
    return [];
  }
  return tagIds.map((tagId) => tags.find((tag) => tag.id === tagId)?.name || `标签${tagId}`);
}

export function buildOrderKeyword(item) {
  if (!item?.foodName && !item?.name) {
    return '';
  }
  const name = item.foodName || item.name;
  const price = formatPrice(item.referencePrice);
  return price === '-' ? name : `${name} ${price}元`;
}
