export const NAVS = [
  { key: "shake", label: "摇一摇", hint: "今晚先把结果摇出来" },
  { key: "preference", label: "偏好", hint: "口味和预算在这里收口" },
  { key: "tag", label: "标签", hint: "维护标签池和推荐语义" },
  { key: "food", label: "菜单", hint: "维护食物库和标签绑定" },
  { key: "stats", label: "统计", hint: "看接受率和决策分布" },
  { key: "history", label: "历史", hint: "回看每次决策记录" }
];

export const MODE_OPTIONS = [
  { value: "LAZY", label: "懒人快决策", desc: "优先找外卖和低执行成本" },
  { value: "CLEAR", label: "清库存", desc: "先消化家里已有食材" },
  { value: "SPICY", label: "今天要点刺激", desc: "重口和辣味优先" },
  { value: "LIGHT", label: "轻负担", desc: "轻食和健康优先" }
];

export const STATUS_OPTIONS = [
  { value: "", label: "全部记录" },
  { value: "PENDING", label: "待确认" },
  { value: "ACCEPTED", label: "已接受" },
  { value: "REJECTED", label: "已拒绝" }
];

export const REJECT_FEEDBACK_OPTIONS = [
  { value: "TOO_EXPENSIVE", label: "太贵" },
  { value: "TOO_TROUBLESOME", label: "太麻烦" },
  { value: "WRONG_CRAVING", label: "不想吃这个口味" },
  { value: "NOT_IN_MOOD", label: "今天状态不对" }
];

export const EMPTY_FOOD_FORM = {
  id: null,
  name: "",
  emoji: "",
  steps: "",
  imageUrl: "",
  orderUrl: "",
  weight: 80,
  referencePrice: 20,
  tagIds: []
};

export const EMPTY_TAG_FORM = {
  id: null,
  name: "",
  icon: "",
  sort: 100,
  enabled: 1,
  tagType: "CUSTOM"
};
