export const NAVS = [
  {
    key: 'shake',
    label: '摇一摇',
    hint: '去摇晚饭',
    icon: '摇',
    heroKicker: '今晚吃什么',
    heroTitle: '快速摇出一个结果',
    heroSubtitle: '选好模式, 一次给出今晚推荐。'
  },
  {
    key: 'preference',
    label: '偏好',
    hint: '设置口味',
    icon: '偏',
    heroKicker: '口味配置',
    heroTitle: '先把偏好设清楚',
    heroSubtitle: '预算, 喜好和忌口都会影响推荐结果。'
  },
  {
    key: 'food',
    label: '菜单',
    hint: '维护菜单',
    icon: '单',
    heroKicker: '菜单维护',
    heroTitle: '把常吃菜单整理好',
    heroSubtitle: '名称, 标签和链接维护好, 推荐会更准。'
  },
  {
    key: 'stats',
    label: '统计',
    hint: '看趋势',
    icon: '统',
    heroKicker: '数据看板',
    heroTitle: '看最近的决策趋势',
    heroSubtitle: '接受率, 模式分布和拒绝原因都在这里。'
  },
  {
    key: 'history',
    label: '历史',
    hint: '查看记录',
    icon: '记',
    heroKicker: '决策历史',
    heroTitle: '回看每一次选择',
    heroSubtitle: '接受, 拒绝和当时的推荐都能随时翻。'
  }
];

export const MODE_OPTIONS = [
  { value: 'LAZY', label: '懒人快决策', desc: '优先找外卖和低执行成本' },
  { value: 'CLEAR', label: '清库存', desc: '先消化家里已有食材' },
  { value: 'SPICY', label: '今天要点刺激', desc: '重口和辣味优先' },
  { value: 'LIGHT', label: '轻负担', desc: '轻食和健康优先' }
];

export const STATUS_OPTIONS = [
  { value: '', label: '全部记录' },
  { value: 'PENDING', label: '待确认' },
  { value: 'ACCEPTED', label: '已接受' },
  { value: 'REJECTED', label: '已拒绝' }
];

export const REJECT_FEEDBACK_OPTIONS = [
  { value: 'TOO_EXPENSIVE', label: '太贵' },
  { value: 'TOO_TROUBLESOME', label: '太麻烦' },
  { value: 'WRONG_CRAVING', label: '不想吃这个口味' },
  { value: 'NOT_IN_MOOD', label: '今天状态不对' }
];

export const EMPTY_FOOD_FORM = {
  id: null,
  name: '',
  emoji: '',
  steps: '',
  imageUrl: '',
  orderUrl: '',
  weight: 80,
  referencePrice: 20,
  tagIds: []
};
