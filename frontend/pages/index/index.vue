<template>
  <view class="page-root">
    <scroll-view class="page-shell" scroll-y="true">
      <view class="hero-card">
        <view class="hero-topbar">
          <text class="eyebrow">{{ activeNav.heroKicker }}</text>
          <text class="hero-tab-name">{{ activeNav.label }}</text>
        </view>
        <text class="page-title">{{ activeNav.heroTitle }}</text>
        <text class="page-subtitle">{{ activeNav.heroSubtitle }}</text>
        <view class="status-row">
          <view class="status-pill">
            <text class="status-label">标签</text>
            <text class="status-value">{{ tags.length }}</text>
          </view>
          <view class="status-pill">
            <text class="status-label">菜单</text>
            <text class="status-value">{{ foods.length }}</text>
          </view>
          <view class="status-pill">
            <text class="status-label">历史</text>
            <text class="status-value">{{ historyTotal }}</text>
          </view>
          <view v-if="quota" class="status-pill">
            <text class="status-label">今日</text>
            <text class="status-value">{{ quota.usedCount }}/{{ quota.dailyLimit }}</text>
          </view>
        </view>
      </view>
      <view v-if="message" class="banner success">{{ message }}</view>
      <view v-if="error" class="banner error">{{ error }}</view>
      <view v-if="loading" class="banner muted">处理中, 请稍等...</view>

    <view v-if="activeTab === 'shake'" class="section-card shake-card">
      <text class="section-kicker">今晚决策</text>
      <text class="section-title">摇一摇主流程</text>
      <text class="shake-caption">先选模式, 再点主按钮直接出结果。</text>
      <view v-if="showOnboardingCard" class="guide-card onboarding-card">
        <view class="group-head">
          <view class="group-copy">
            <text class="group-title">首次使用引导</text>
            <text class="group-tip">先把基础数据补齐, 后面的推荐才会稳定。</text>
          </view>
          <text class="mini-stat">{{ onboardingProgressText }}</text>
        </view>
        <view class="guide-checklist">
          <view v-for="item in onboardingSteps" :key="item.key" class="guide-check-item">
            <text :class="item.done ? 'tag-pill active-soft' : 'tag-pill subtle'">{{ item.done ? '已完成' : '待处理' }}</text>
            <view class="guide-check-copy">
              <text class="guide-check-title">{{ item.title }}</text>
              <text class="guide-check-text">{{ item.text }}</text>
            </view>
          </view>
        </view>
        <view class="action-bar compact">
          <button class="primary-btn half" @click="goToNextOnboardingStep">{{ onboardingActionLabel }}</button>
          <button class="ghost-btn half" @click="dismissOnboarding">先隐藏</button>
        </view>
      </view>
      <view class="rule-panel">
        <view class="group-head">
          <view class="group-copy">
            <text class="group-title">当前规则</text>
            <text class="group-tip">推荐不是纯随机, 会结合预算和最近选择做修正。</text>
          </view>
        </view>
        <view class="rule-list">
          <view v-for="item in shakeRuleItems" :key="item.label" class="rule-item">
            <text class="rule-label">{{ item.label }}</text>
            <text class="rule-text">{{ item.text }}</text>
          </view>
        </view>
      </view>
      <view class="overview-panel">
        <view class="group-head">
          <view class="group-copy">
            <text class="group-title">当前状态</text>
            <text class="group-tip">先看一眼今天能不能直接摇, 数据准备到了哪一步。</text>
          </view>
        </view>
        <view class="overview-grid">
          <view v-for="item in shakeOverviewItems" :key="item.label" class="overview-card">
            <text class="overview-value">{{ item.value }}</text>
            <text class="overview-label">{{ item.label }}</text>
            <text class="overview-text">{{ item.text }}</text>
          </view>
        </view>
      </view>
      <view v-if="quota" class="detail-box">
        <text class="detail-title">今日次数</text>
        <view class="result-stat-row">
          <text class="tag-pill active-soft">已摇 {{ quota.usedCount }} 次</text>
          <text class="tag-pill subtle">剩余 {{ quota.remainingCount }} 次</text>
          <text class="tag-pill subtle">上限 {{ quota.dailyLimit }} 次</text>
        </view>
      </view>
      <view v-if="needsFoodSetup" class="guide-card">
        <text class="guide-title">先把菜单和标签补齐</text>
        <text class="guide-text">当前还没有足够的菜单数据, 摇一摇很难给出可靠结果。先去维护菜单库和标签。</text>
        <view class="action-bar compact">
          <button class="primary-btn" @click="goToFoodTab">去维护菜单</button>
        </view>
      </view>
      <view v-else>
      <view v-if="needsPreferenceSetup" class="guide-card slim-guide-card">
        <text class="guide-title">口味偏好还没设置</text>
        <text class="guide-text">你现在也可以直接摇一摇. 如果先补预算和喜欢/避免标签, 结果会更贴近实际选择。</text>
        <view class="action-bar compact">
          <button class="ghost-btn" @click="goToPreferenceTab">去设置偏好</button>
        </view>
      </view>
      <view class="mode-grid">
        <view
          v-for="(mode, index) in MODE_OPTIONS"
          :key="index"
          :class="shakeMode === mode.value ? 'mode-chip active' : 'mode-chip'"
          @click="selectShakeMode(mode.value)"
        >
          <text class="mode-name">{{ mode.label }}</text>
          <text class="mode-desc">{{ mode.desc }}</text>
        </view>
      </view>
      <view class="shake-cta-panel">
        <view class="shake-cta-head">
          <view class="shake-cta-copy">
            <text class="group-title">当前模式</text>
            <text class="group-tip">{{ currentShakeModeLabel }}</text>
          </view>
          <text :class="needsPreferenceSetup ? 'tag-pill subtle shake-cta-badge' : 'tag-pill active-soft shake-cta-badge'">
            {{ preferenceReadyText }}
          </text>
        </view>
        <button class="primary-btn cta-btn" @click="handleShake">现在摇一摇</button>
        <view class="shake-cta-meta">
          <text class="shake-cta-note">预算 {{ preferenceBudgetText }}</text>
          <text class="shake-cta-note">会结合标签和最近偏好给出推荐</text>
        </view>
      </view>

      <view v-if="shakeResult" class="result-card">
        <view class="result-head">
          <text class="result-emoji">{{ shakeEmoji }}</text>
          <view class="result-main">
            <text class="result-name">{{ shakeResult.foodName }}</text>
            <text class="result-meta">今晚推荐已经生成, 可以直接确认或继续再摇。</text>
          </view>
        </view>
        <view class="result-stat-row">
          <text class="tag-pill active-soft">模式 {{ shakeModeLabel }}</text>
          <text class="tag-pill subtle">AI {{ shakeResult.aiSource }}</text>
          <text class="tag-pill subtle">¥{{ shakePriceText }}</text>
        </view>
        <view class="tag-row">
          <text v-for="(tagName, index) in shakeTagNames" :key="index" class="tag-pill">{{ tagName }}</text>
        </view>
        <view class="detail-box">
          <text class="detail-title">推荐理由</text>
          <text class="detail-text">{{ shakeAiTipText }}</text>
        </view>
        <view v-if="shakeDecisionNote" class="detail-box hint-box">
          <text class="detail-title">规则提示</text>
          <text class="detail-text">{{ shakeDecisionNote }}</text>
        </view>
        <view v-if="shakeScoreDetailItems.length" class="detail-box">
          <text class="detail-title">推荐原因明细</text>
          <view class="rule-list">
            <view v-for="item in shakeScoreDetailItems" :key="item.label" class="rule-item">
              <text class="rule-label">{{ item.label }}</text>
              <text class="rule-text">{{ item.value }}</text>
            </view>
          </view>
        </view>
        <view class="detail-box">
          <text class="detail-title">下单步骤</text>
          <text class="detail-text">{{ shakeStepsText }}</text>
        </view>
        <view v-if="shakeResult.orderUrl" class="detail-box">
          <text class="detail-title">平台搜索入口</text>
          <text class="detail-link">{{ shakeResult.orderUrl }}</text>
          <view class="inline-actions">
            <button class="ghost-btn" @click="openShakeOrderActions">复制或打开</button>
          </view>
        </view>
        <view class="inline-actions result-actions">
          <button class="primary-btn half" @click="handleDecision('accept')">就吃这个</button>
          <button class="ghost-btn half" @click="handleRejectDecision">再摇一次</button>
        </view>
      </view>
      </view>
    </view>

    <view v-if="activeTab === 'preference'" class="section-card">
      <text class="section-kicker">口味配置</text>
      <text class="section-title">偏好设置</text>
      <text class="form-caption">把预算和口味偏好先收口, 后面摇一摇会更准。</text>
      <view class="preference-summary-row">
        <text class="tag-pill active-soft">默认 {{ preferenceModeLabel }}</text>
        <text class="tag-pill subtle">预算 {{ preferenceBudgetText }}</text>
        <text class="tag-pill subtle">喜欢 {{ favoriteTagCount }}</text>
        <text class="tag-pill subtle">避免 {{ avoidTagCount }}</text>
      </view>
      <view class="form-panel">
        <view class="form-group">
          <view class="group-head">
            <view class="group-copy">
              <text class="group-title">基础偏好</text>
              <text class="group-tip">先确定默认模式和预算边界。</text>
            </view>
          </view>
          <view class="field-block">
            <text class="field-label">默认模式</text>
            <view class="mode-grid compact">
              <view
                v-for="(mode, index) in MODE_OPTIONS"
                :key="index"
                :class="preference.lastMode === mode.value ? 'mode-chip active' : 'mode-chip'"
                @click="selectPreferenceMode(mode.value)"
              >
                <text class="mode-name">{{ mode.label }}</text>
              </view>
            </view>
          </view>
          <view class="field-block">
            <text class="field-label">预算上限</text>
            <input class="text-input" type="number" :value="preference.maxBudget" placeholder="请输入预算" @input="onBudgetInput" />
          </view>
        </view>
        <view class="form-group">
          <view class="group-head">
            <text class="group-title">喜欢标签</text>
            <text class="mini-stat">{{ favoriteTagCount }}</text>
          </view>
          <text class="group-tip">会优先命中这些口味</text>
          <view class="tag-row food-tag-row">
            <text
              v-for="tag in tags"
              :key="tag.id"
              :class="favoriteTagMap[tag.id] ? 'tag-pill active' : 'tag-pill'"
              @click="togglePreferenceTag('favoriteTagIds', tag.id)"
            >
              {{ tag.name }}
            </text>
          </view>
        </view>
        <view class="form-group">
          <view class="group-head">
            <text class="group-title">避免标签</text>
            <text class="mini-stat">{{ avoidTagCount }}</text>
          </view>
          <text class="group-tip">这些标签会尽量避开</text>
          <view class="tag-row food-tag-row">
            <text
              v-for="tag in tags"
              :key="tag.id"
              :class="avoidTagMap[tag.id] ? 'tag-pill danger' : 'tag-pill'"
              @click="togglePreferenceTag('avoidTagIds', tag.id)"
            >
              {{ tag.name }}
            </text>
          </view>
        </view>
        <view :class="preferenceValidationError ? 'form-hint danger' : 'form-hint good'">
          <text class="form-hint-text">{{ preferenceAssistText }}</text>
        </view>
        <view class="action-bar">
          <button class="primary-btn" @click="savePreference">保存偏好</button>
        </view>
      </view>
    </view>

    <view v-if="activeTab === 'food'" class="section-card">
      <text class="section-kicker">菜单维护</text>
      <text class="section-title">新增或编辑菜单</text>
      <text class="form-caption">先录入基础信息, 再补充下单线索和标签。</text>
      <view class="form-panel">
        <view class="form-group">
          <text class="group-title">基础信息</text>
          <view class="field-block">
            <text class="field-label">名称</text>
            <input class="text-input" :value="foodForm.name" placeholder="例如: 黄焖鸡米饭" @input="updateFoodField('name', $event)" />
          </view>
          <view class="field-block">
            <text class="field-label">Emoji</text>
            <input class="text-input" :value="foodForm.emoji" placeholder="🍗" @input="updateFoodField('emoji', $event)" />
          </view>
          <view class="double-grid">
            <view class="field-block">
              <text class="field-label">权重</text>
              <input class="text-input" type="number" :value="foodForm.weight" placeholder="80" @input="updateFoodField('weight', $event)" />
            </view>
            <view class="field-block">
              <text class="field-label">参考价格</text>
              <input class="text-input" type="number" :value="foodForm.referencePrice" placeholder="20" @input="updateFoodField('referencePrice', $event)" />
            </view>
          </view>
        </view>

        <view class="form-group">
          <text class="group-title">下单线索</text>
          <view class="field-block">
            <text class="field-label">步骤</text>
            <textarea class="text-area" auto-height :value="foodForm.steps" placeholder="输入执行步骤或平台搜索说明" @input="updateFoodField('steps', $event)" />
          </view>
          <view class="field-block">
            <text class="field-label">图片地址</text>
            <input class="text-input" :value="foodForm.imageUrl" placeholder="https://..." @input="updateFoodField('imageUrl', $event)" />
          </view>
          <view class="field-block">
            <text class="field-label">平台搜索链接</text>
            <input class="text-input" :value="foodForm.orderUrl" placeholder="留空则按菜名和价格自动生成" @input="updateFoodField('orderUrl', $event)" />
          </view>
        </view>

        <view class="form-group">
          <view class="group-head">
            <text class="group-title">绑定标签</text>
            <text class="group-tip">至少选择 1 个</text>
          </view>
          <view class="tag-row food-tag-row">
            <text
              v-for="tag in tags"
              :key="tag.id"
              :class="selectedFoodTagMap[tag.id] ? 'tag-pill active' : 'tag-pill'"
              @click="toggleFoodTag(tag.id)"
            >
              {{ tag.name }}
            </text>
          </view>
        </view>
        <view :class="foodValidationError ? 'form-hint danger' : 'form-hint good'">
          <text class="form-hint-text">{{ foodAssistText }}</text>
        </view>

        <view class="action-bar">
          <button class="primary-btn half" @click="submitFood">{{ foodSubmitText }}</button>
          <button class="ghost-btn half" @click="resetFood">清空表单</button>
        </view>
      </view>

      <view class="form-panel tag-manager-panel">
        <view class="group-head">
          <view class="group-copy">
            <text class="group-title">标签库</text>
            <text class="group-tip">系统标签保留为基础规则, 自定义标签在这里维护。</text>
          </view>
          <text class="mini-stat">{{ tags.length }} 个</text>
        </view>
        <view class="tag-summary-row">
          <text class="tag-pill subtle">系统 {{ systemTagCount }}</text>
          <text class="tag-pill subtle">自定义 {{ customTagCount }}</text>
        </view>
        <view class="tag-editor-panel">
          <view class="group-head">
            <view class="group-copy">
              <text class="group-title">{{ tagEditorTitle }}</text>
              <text class="group-tip">名称, 图标和状态控制放在这里处理。</text>
            </view>
            <text :class="tagForm.id ? 'tag-pill active-soft' : 'tag-pill subtle'">{{ tagSubmitText }}</text>
          </view>
          <view class="field-block">
            <text class="field-label">标签名称</text>
            <input class="text-input" :value="tagForm.name" placeholder="例如: 夜宵, 重辣, 面食" @input="updateTagField('name', $event)" />
          </view>
          <view class="double-grid">
            <view class="field-block">
              <text class="field-label">图标</text>
              <input class="text-input" :value="tagForm.icon" placeholder="🌶️" @input="updateTagField('icon', $event)" />
            </view>
            <view class="field-block">
              <text class="field-label">排序</text>
              <input class="text-input" type="number" :value="tagForm.sort" placeholder="50" @input="updateTagField('sort', $event)" />
            </view>
          </view>
          <view class="field-block">
            <text class="field-label">状态</text>
            <view class="tag-row filter-chip-row">
              <text :class="tagForm.enabled === 1 ? 'tag-pill active' : 'tag-pill'" @click="setTagEnabled(1)">启用</text>
              <text :class="tagForm.enabled === 0 ? 'tag-pill active' : 'tag-pill'" @click="setTagEnabled(0)">停用</text>
            </view>
          </view>
          <view :class="tagValidationError ? 'form-hint danger' : 'form-hint good'">
            <text class="form-hint-text">{{ tagAssistText }}</text>
          </view>
          <view class="action-bar">
            <button class="primary-btn half" @click="submitTag">{{ tagSubmitText }}</button>
            <button class="ghost-btn half" @click="resetTagForm">清空表单</button>
          </view>
        </view>
        <view v-if="displayTags.length === 0" class="empty-box">还没有标签, 先新增 1 个口味标签再继续录菜单。</view>
        <view v-else class="tag-library-list">
          <view v-for="tag in displayTags" :key="tag.id" class="tag-library-card">
            <view class="tag-library-head">
              <text class="tag-library-name">{{ tag.displayName }}</text>
              <text :class="tag.isSystemTag ? 'tag-pill subtle' : 'tag-pill active-soft'">{{ tag.displayTypeText }}</text>
              <text :class="tag.enabled === 1 ? 'tag-pill subtle' : 'tag-pill danger'">{{ tag.displayEnabledText }}</text>
            </view>
            <text class="tag-library-meta">排序 {{ tag.sort }}</text>
            <view class="inline-actions wrap">
              <button class="ghost-btn tiny" @click="pickTag(tag.id)">编辑</button>
              <button v-if="!tag.isSystemTag" class="danger-btn tiny" @click="confirmDeleteTag(tag.id)">删除</button>
            </view>
          </view>
        </view>
      </view>

      <view class="form-panel">
        <view class="group-head">
          <view class="group-copy">
            <text class="group-title">菜单导入导出</text>
            <text class="group-tip">导出当前菜单为 JSON, 或粘贴 JSON 做批量导入。</text>
          </view>
        </view>
        <view class="action-bar">
          <button class="ghost-btn half" @click="handleFoodExport">导出 JSON</button>
          <button class="primary-btn half" @click="handleFoodImport">批量导入</button>
        </view>
        <view class="field-block">
          <text class="field-label">JSON 内容</text>
          <textarea
            class="text-area"
            auto-height
            :value="foodImportText"
            placeholder="粘贴导出的 JSON 数组, 支持按名称新增或更新菜单"
            @input="onFoodImportInput"
          />
        </view>
      </view>

      <view class="list-block">
        <view class="list-head compact-head">
          <view class="group-head">
            <view class="group-copy">
              <text class="section-title small">菜单列表</text>
              <text class="group-tip">点击下方按钮可编辑, 停用或复制链接</text>
            </view>
            <text class="mini-stat">{{ foodListSummary }}</text>
          </view>
        </view>
        <view class="filter-panel compact-filter-panel">
          <input class="text-input search-input" :value="foodKeyword" placeholder="搜索菜单名, Emoji 或标签" @input="onFoodKeywordInput" />
          <view class="tag-row filter-chip-row">
            <text :class="foodEnabledFilter === 'ALL' ? 'tag-pill active' : 'tag-pill'" @click="changeFoodEnabledFilter('ALL')">全部</text>
            <text :class="foodEnabledFilter === 'ENABLED' ? 'tag-pill active' : 'tag-pill'" @click="changeFoodEnabledFilter('ENABLED')">仅启用</text>
            <text :class="foodEnabledFilter === 'DISABLED' ? 'tag-pill active' : 'tag-pill'" @click="changeFoodEnabledFilter('DISABLED')">仅停用</text>
            <text v-if="hasFoodFilters" class="tag-pill subtle" @click="clearFoodFilters">清空筛选</text>
          </view>
        </view>
        <view v-if="displayFoods.length === 0" class="empty-box">
          {{ foods.length === 0 ? '还没有菜单, 先新增一条常吃项。' : '没有匹配的菜单结果, 试试换个关键词或筛选条件。' }}
        </view>
        <view v-for="food in displayFoods" :key="food.id" class="list-card">
          <view class="list-top">
            <text class="list-name">{{ food.displayEmoji }} {{ food.name }}</text>
            <text class="list-meta">菜单项已整理好, 可以直接编辑或复制入口。</text>
          </view>
          <view class="food-card-stats">
            <text class="tag-pill subtle">¥{{ food.displayPrice }}</text>
            <text class="tag-pill subtle">权重 {{ food.weight }}</text>
            <text :class="food.enabled === 1 ? 'tag-pill active-soft' : 'tag-pill danger'">{{ food.displayEnabledText }}</text>
          </view>
          <view class="tag-row">
            <text v-for="(tagName, index) in food.displayTagNames" :key="index" class="tag-pill subtle">{{ tagName }}</text>
          </view>
          <view class="inline-actions wrap food-actions">
            <button class="ghost-btn tiny" @click="pickFood(food.id)">编辑</button>
            <button class="ghost-btn tiny" @click="toggleFoodEnabled(food)">{{ food.displayToggleText }}</button>
            <button v-if="food.orderUrl" class="ghost-btn tiny" @click="openFoodOrderActions(food.id)">复制链接</button>
            <button class="danger-btn tiny" @click="confirmDeleteFood(food.id)">删除</button>
          </view>
        </view>
      </view>
    </view>

    <view v-if="activeTab === 'stats'" class="section-card">
      <text class="section-kicker">数据看板</text>
      <text class="section-title">最近决策趋势</text>
      <text class="form-caption">接受率, 模式分布和拒绝原因都在这里复盘。</text>
      <view v-if="statsLoading" class="empty-box">统计加载中...</view>
      <view v-else-if="!stats" class="empty-box">暂无统计数据。</view>
      <view v-else>
        <view class="overview-grid">
          <view v-for="item in statsOverviewItems" :key="item.label" class="overview-card">
            <text class="overview-value">{{ item.value }}</text>
            <text class="overview-label">{{ item.label }}</text>
            <text class="overview-text">{{ item.text }}</text>
          </view>
        </view>
        <view class="detail-box">
          <text class="detail-title">模式分布</text>
          <view v-if="!statsModeItems.length" class="empty-box">暂无模式数据。</view>
          <view v-else class="rule-list">
            <view v-for="item in statsModeItems" :key="item.code" class="rule-item">
              <text class="rule-label">{{ item.label }}</text>
              <text class="rule-text">{{ item.count }}</text>
            </view>
          </view>
        </view>
        <view class="detail-box">
          <text class="detail-title">拒绝原因分布</text>
          <view v-if="!statsRejectItems.length" class="empty-box">还没有拒绝反馈。</view>
          <view v-else class="rule-list">
            <view v-for="item in statsRejectItems" :key="item.code" class="rule-item">
              <text class="rule-label">{{ item.label }}</text>
              <text class="rule-text">{{ item.count }}</text>
            </view>
          </view>
        </view>
        <view class="detail-box">
          <text class="detail-title">最近 7 天</text>
          <view v-if="!statsDailyItems.length" class="empty-box">暂无最近 7 天数据。</view>
          <view v-else class="rule-list">
            <view v-for="item in statsDailyItems" :key="item.day" class="rule-item">
              <text class="rule-label">{{ item.day }}</text>
              <text class="rule-text">{{ item.count }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="activeTab === 'history'" class="section-card">
      <text class="section-kicker">决策记录</text>
      <text class="section-title">历史列表</text>
      <view class="list-head compact-head top-space">
        <view class="group-head">
          <view class="group-copy">
            <text class="group-tip">按关键词和状态筛选最近的决策记录。</text>
          </view>
          <text class="mini-stat">{{ historyListSummary }}</text>
        </view>
      </view>
      <view class="filter-panel compact-filter-panel">
        <input class="text-input search-input" :value="historyKeyword" placeholder="搜索菜名, 模式或推荐文案" @input="onHistoryKeywordInput" />
        <view class="tag-row status-filter-row">
          <text
            v-for="(status, index) in STATUS_OPTIONS"
            :key="index"
            :class="historyStatus === status.value ? 'tag-pill active' : 'tag-pill'"
            @click="changeHistoryStatus(status.value)"
          >
            {{ status.label }}
          </text>
          <text v-if="hasHistoryFilters" class="tag-pill subtle" @click="clearHistoryFilters">清空筛选</text>
        </view>
      </view>
      <view v-if="historyLoading" class="empty-box">历史加载中...</view>
      <view v-else-if="displayHistories.length === 0" class="empty-box">
        {{ histories.length === 0 ? '还没有历史记录, 先去摇一次。' : '没有匹配的历史记录, 试试换个关键词或筛选条件。' }}
      </view>
      <view v-else class="list-block history-list">
        <view v-for="record in displayHistories" :key="record.id" class="list-card history-card">
          <view class="list-top">
            <text class="list-name">{{ record.foodName }}</text>
            <text class="list-meta">保留了当时的推荐信息和处理结果。</text>
          </view>
          <view class="history-card-stats">
            <text class="tag-pill active-soft">{{ record.displayModeLabel }}</text>
            <text :class="record.resultStatus === 'REJECTED' ? 'tag-pill danger' : 'tag-pill subtle'">{{ record.displayStatusLabel }}</text>
            <text class="tag-pill subtle">{{ record.displayShakeTime }}</text>
          </view>
          <view class="detail-box preview-box">
            <text class="detail-title">推荐摘要</text>
            <text class="preview-text">{{ record.aiTipPreview }}</text>
          </view>
          <view class="inline-actions wrap history-actions">
            <button class="ghost-btn tiny" @click="openHistory(record.id)">查看详情</button>
          </view>
        </view>
        <view class="inline-actions">
          <button class="ghost-btn half" :disabled="!canPrevHistoryPage" @click="changeHistoryPage(historyPageNo - 1)">上一页</button>
          <button class="ghost-btn half" :disabled="!canNextHistoryPage" @click="changeHistoryPage(historyPageNo + 1)">下一页</button>
        </view>
        <view class="detail-box">
          <text class="detail-title">分页</text>
          <text class="detail-text">第 {{ historyPageNo }} / {{ historyPageCount }} 页, 共 {{ historyTotal }} 条</text>
        </view>
      </view>
    </view>

    <view v-if="historyDetail" class="modal-mask" @click="closeHistoryDetail">
      <view class="modal-card" @click.stop>
        <text class="section-title">{{ historyDetail.foodName }}</text>
        <text class="list-meta">这次决策的完整记录如下。</text>
        <view class="result-stat-row detail-stat-row">
          <text class="tag-pill active-soft">{{ historyDetailModeLabel }}</text>
          <text :class="historyDetail.resultStatus === 'REJECTED' ? 'tag-pill danger' : 'tag-pill subtle'">{{ historyDetailStatusLabel }}</text>
          <text class="tag-pill subtle">¥{{ historyDetailPriceText }}</text>
          <text class="tag-pill subtle">{{ historyDetailTimeText }}</text>
        </view>
        <view class="detail-box">
          <text class="detail-title">推荐理由</text>
          <text class="detail-text">{{ historyDetailAiTipText }}</text>
        </view>
        <view v-if="historyScoreDetailItems.length" class="detail-box">
          <text class="detail-title">推荐原因明细</text>
          <view class="rule-list">
            <view v-for="item in historyScoreDetailItems" :key="item.label" class="rule-item">
              <text class="rule-label">{{ item.label }}</text>
              <text class="rule-text">{{ item.value }}</text>
            </view>
          </view>
        </view>
        <view v-if="historyRejectFeedbackText" class="detail-box">
          <text class="detail-title">拒绝原因</text>
          <text class="detail-text">{{ historyRejectFeedbackText }}</text>
        </view>
        <view class="detail-box">
          <text class="detail-title">下单步骤</text>
          <text class="detail-text">{{ historyDetailStepsText }}</text>
        </view>
        <view v-if="historyDetail.orderUrl" class="detail-box">
          <text class="detail-title">平台搜索入口</text>
          <text class="detail-link">{{ historyDetail.orderUrl }}</text>
          <view class="inline-actions modal-actions">
            <button class="ghost-btn half" @click="openHistoryOrderActions">复制或打开</button>
            <button class="ghost-btn half" @click="copyHistorySearchKeyword">复制搜索词</button>
          </view>
        </view>
        <button class="primary-btn" @click="closeHistoryDetail">关闭</button>
      </view>
    </view>
    </scroll-view>
    <view class="bottom-nav">
      <view
        v-for="(item, index) in NAVS"
        :key="index"
        :class="activeTab === item.key ? 'bottom-nav-item active' : 'bottom-nav-item'"
        @click="selectTab(item.key)"
      >
        <text class="bottom-nav-icon">{{ item.icon }}</text>
        <text class="bottom-nav-label">{{ item.label }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import {
  acceptShake,
  createTag,
  createFood,
  deleteTag,
  deleteFood,
  exportFoods,
  getAllTags,
  getFood,
  getFoods,
  getHistories,
  getHistory,
  getHistoryStats,
  getPreference,
  getShakeQuota,
  importFoods,
  rejectShake,
  shake,
  updateTag,
  updateFood,
  updateFoodEnabled,
  updatePreference
} from '../../common/api';
import { MODE_OPTIONS, NAVS, REJECT_FEEDBACK_OPTIONS, STATUS_OPTIONS } from '../../common/constants';
import {
  buildOrderKeyword,
  formatPrice,
  formatTime,
  getTagNames,
  mapModeLabel,
  mapRejectFeedbackLabel,
  mapStatusLabel,
  normalizeFoodForm,
  resetFoodForm
} from '../../common/utils';

function resolveFoodEmoji(food) {
  return food && food.emoji ? food.emoji : '🍽️';
}

function resolveFoodEnabledText(food) {
  return food && food.enabled === 1 ? '启用' : '停用';
}

function resolveFoodToggleText(food) {
  return food && food.enabled === 1 ? '停用' : '启用';
}

function formatAcceptanceRate(value) {
  if (value === null || value === undefined || value === '') {
    return '-';
  }
  const number = Number(value);
  return Number.isNaN(number) ? String(value) : `${number}%`;
}

function buildScoreDetailItems(scoreDetail) {
  if (!scoreDetail) {
    return [];
  }
  const items = [];
  if (scoreDetail.baseWeight !== undefined && scoreDetail.baseWeight !== null) {
    items.push({ label: '基础权重', value: String(scoreDetail.baseWeight) });
  }
  if (scoreDetail.modeHit !== undefined && scoreDetail.modeHit !== null) {
    items.push({ label: '模式命中', value: scoreDetail.modeHit ? '是' : '否' });
  }
  if (scoreDetail.modeFactor !== undefined && scoreDetail.modeFactor !== null) {
    items.push({ label: '模式修正', value: String(scoreDetail.modeFactor) });
  }
  if (scoreDetail.favoriteHit !== undefined && scoreDetail.favoriteHit !== null) {
    items.push({ label: '偏好命中', value: scoreDetail.favoriteHit ? '是' : '否' });
  }
  if (scoreDetail.favoriteFactor !== undefined && scoreDetail.favoriteFactor !== null) {
    items.push({ label: '偏好修正', value: String(scoreDetail.favoriteFactor) });
  }
  if (scoreDetail.budgetFactor !== undefined && scoreDetail.budgetFactor !== null) {
    items.push({ label: '预算修正', value: String(scoreDetail.budgetFactor) });
  }
  if (scoreDetail.diversityFactor !== undefined && scoreDetail.diversityFactor !== null) {
    items.push({ label: '多样性修正', value: String(scoreDetail.diversityFactor) });
  }
  if (scoreDetail.finalScore !== undefined && scoreDetail.finalScore !== null) {
    items.push({ label: '最终分数', value: String(scoreDetail.finalScore) });
  }
  if (Array.isArray(scoreDetail.reasonLabels) && scoreDetail.reasonLabels.length) {
    items.push({ label: '命中标签', value: scoreDetail.reasonLabels.join('、') });
  }
  return items;
}

const ONBOARDING_STORAGE_KEY = 'shakeeat_onboarding_hidden_v1';
const REJECT_COOLDOWN_MINUTES = 30;
const HISTORY_PAGE_SIZE = 10;

function createEmptyTagForm() {
  return {
    id: null,
    name: '',
    icon: '',
    sort: 50,
    enabled: 1
  };
}

function normalizeCompareText(value) {
  return String(value || '').trim().toLowerCase();
}

function parseLocalDateTime(value) {
  if (!value) {
    return null;
  }
  const normalized = String(value).replace(' ', 'T');
  const parsed = new Date(normalized);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

function isSameDay(date, reference) {
  if (!date || !reference) {
    return false;
  }
  return date.getFullYear() === reference.getFullYear()
    && date.getMonth() === reference.getMonth()
    && date.getDate() === reference.getDate();
}

function isWithinMinutes(date, reference, minutes) {
  if (!date || !reference) {
    return false;
  }
  const diff = reference.getTime() - date.getTime();
  return diff >= 0 && diff <= minutes * 60 * 1000;
}

function isValidHttpUrl(value) {
  return /^https?:\/\//i.test(String(value || '').trim());
}

export default {
  data() {
    return {
      NAVS,
      MODE_OPTIONS,
      REJECT_FEEDBACK_OPTIONS,
      STATUS_OPTIONS,
      activeTab: 'shake',
      tags: [],
      foods: [],
      histories: [],
      historyTotal: 0,
      historyStatus: '',
      historyKeyword: '',
      historyPageNo: 1,
      historyDetail: null,
      historyLoading: false,
      loading: false,
      onboardingDismissed: false,
      message: '',
      error: '',
      shakeMode: 'LAZY',
      shakeResult: null,
      quota: null,
      stats: null,
      statsLoading: false,
      preference: {
        favoriteTagIds: [],
        avoidTagIds: [],
        maxBudget: 30,
        lastMode: 'LAZY'
      },
      tagForm: createEmptyTagForm(),
      foodKeyword: '',
      foodEnabledFilter: 'ALL',
      foodForm: resetFoodForm(),
      foodImportText: ''
    };
  },
  computed: {
    activeNav() {
      return this.NAVS.find((item) => item.key === this.activeTab) || this.NAVS[0];
    },
    shakeTagNames() {
      if (!this.shakeResult) {
        return [];
      }
      return getTagNames(this.tags, this.shakeResult.tagIds || []);
    },
    shakeEmoji() {
      return this.shakeResult && this.shakeResult.emoji ? this.shakeResult.emoji : '🍽️';
    },
    shakeModeLabel() {
      return this.shakeResult ? mapModeLabel(this.shakeResult.mode) : '-';
    },
    currentShakeModeLabel() {
      return mapModeLabel(this.shakeMode);
    },
    shakeScoreDetailItems() {
      return buildScoreDetailItems(this.shakeResult && this.shakeResult.scoreDetail);
    },
    historyScoreDetailItems() {
      return buildScoreDetailItems(this.historyDetail && this.historyDetail.scoreDetail);
    },
    onboardingSteps() {
      return [
        {
          key: 'tags',
          title: '整理标签',
          text: this.tags.length ? '标签库已经可以用了。' : '先准备口味标签, 后面菜单和偏好都要用到。',
          done: this.tags.length > 0
        },
        {
          key: 'foods',
          title: '录入菜单',
          text: this.foods.length ? '菜单库已经有可推荐内容。' : '至少准备几道常吃项, 摇一摇才不会空转。',
          done: this.foods.length > 0
        },
        {
          key: 'preference',
          title: '设置偏好',
          text: this.needsPreferenceSetup ? '预算和喜欢/避免标签还没补齐。' : '偏好已经生效, 推荐会更贴近你。',
          done: !this.needsPreferenceSetup
        }
      ];
    },
    onboardingProgressText() {
      const doneCount = this.onboardingSteps.filter((item) => item.done).length;
      return doneCount + ' / ' + this.onboardingSteps.length;
    },
    showOnboardingCard() {
      const hasPending = this.onboardingSteps.some((item) => !item.done);
      return hasPending && !this.onboardingDismissed;
    },
    onboardingActionLabel() {
      if (!this.tags.length || !this.foods.length) {
        return '去补菜单';
      }
      if (this.needsPreferenceSetup) {
        return '去设偏好';
      }
      return '继续使用';
    },
    preferenceReadyText() {
      return this.needsPreferenceSetup ? '偏好待补充' : '偏好已设置';
    },
    preferenceModeLabel() {
      return mapModeLabel(this.preference.lastMode);
    },
    preferenceBudgetText() {
      const value = formatPrice(this.preference.maxBudget);
      return value === '-' ? '未设预算' : '¥' + value;
    },
    enabledFoodCount() {
      return this.foods.filter((food) => food.enabled === 1).length;
    },
    disabledFoodCount() {
      return this.foods.filter((food) => food.enabled !== 1).length;
    },
    pendingHistoryCount() {
      return this.histories.filter((record) => record.resultStatus === 'PENDING').length;
    },
    favoriteTagCount() {
      return this.preference.favoriteTagIds.length;
    },
    avoidTagCount() {
      return this.preference.avoidTagIds.length;
    },
    shakePriceText() {
      return this.shakeResult ? formatPrice(this.shakeResult.referencePrice) : '-';
    },
    recentRejectedNames() {
      const now = new Date();
      return this.histories
        .filter((record) => record.resultStatus === 'REJECTED' && isWithinMinutes(parseLocalDateTime(record.shakeTime), now, REJECT_COOLDOWN_MINUTES))
        .map((record) => record.foodName)
        .filter(Boolean);
    },
    recentAcceptedTodayNames() {
      const now = new Date();
      return this.histories
        .filter((record) => record.resultStatus === 'ACCEPTED' && isSameDay(parseLocalDateTime(record.shakeTime), now))
        .map((record) => record.foodName)
        .filter(Boolean);
    },
    shakeRuleItems() {
      const cooldownText = this.recentRejectedNames.length
        ? '最近拒绝: ' + this.recentRejectedNames.slice(0, 2).join('、') + ', 30 分钟内建议别再点同一道。'
        : '最近 30 分钟没有拒绝记录, 当前没有冷却项。';
      const recentAcceptedText = this.recentAcceptedTodayNames.length
        ? '今天已接受: ' + this.recentAcceptedTodayNames.slice(0, 2).join('、') + ', 后端会尽量避免连续重复。'
        : '今天还没有确认过菜单, 当前没有重复压力。';
      return [
        {
          label: '预算',
          text: this.preference.maxBudget ? '超出 ' + this.preferenceBudgetText + ' 的菜单会降权, 但不会被绝对排除。' : '还没设置预算, 目前按全量菜单推荐。'
        },
        {
          label: '去重',
          text: '后端会避开上一条结果, 最近 7 次结果会自动降权. ' + recentAcceptedText
        },
        {
          label: '冷却',
          text: cooldownText
        }
      ];
    },
    shakeOverviewItems() {
      const quotaValue = this.quota ? `${this.quota.usedCount}/${this.quota.dailyLimit}` : '-';
      return [
        {
          label: '可摇菜单',
          value: String(this.enabledFoodCount),
          text: this.enabledFoodCount ? '当前参与推荐的菜单数' : '还没有启用中的菜单'
        },
        {
          label: '今日次数',
          value: quotaValue,
          text: this.quota ? `剩余 ${this.quota.remainingCount} 次` : '正在等待配额数据'
        },
        {
          label: '偏好标签',
          value: String(this.favoriteTagCount + this.avoidTagCount),
          text: this.favoriteTagCount + this.avoidTagCount ? '喜欢和避免标签都已纳入规则' : '还没设置偏好标签'
        },
        {
          label: '待确认',
          value: String(this.pendingHistoryCount),
          text: this.pendingHistoryCount ? '还有最近未处理的推荐记录' : '当前没有待确认的推荐'
        }
      ];
    },
    shakeDecisionNote() {
      if (!this.shakeResult) {
        return '';
      }
      const notes = [];
      const resultName = this.shakeResult.foodName;
      const priceNumber = Number(this.shakeResult.referencePrice);
      const budgetNumber = Number(this.preference.maxBudget);
      if (resultName && this.recentRejectedNames.indexOf(resultName) >= 0) {
        notes.push('这道菜在最近 30 分钟内被拒绝过, 如果你还是没感觉, 可以直接再摇一次。');
      }
      if (!Number.isNaN(priceNumber) && !Number.isNaN(budgetNumber) && budgetNumber > 0 && priceNumber > budgetNumber) {
        notes.push('这道菜高于当前预算, 它是降权后仍然命中的备选结果。');
      }
      return notes.join(' ');
    },
    shakeAiTipText() {
      return this.shakeResult && this.shakeResult.aiTip ? this.shakeResult.aiTip : '暂无文案';
    },
    shakeStepsText() {
      return this.shakeResult && this.shakeResult.steps ? this.shakeResult.steps : '当前没有步骤说明。';
    },
    foodSubmitText() {
      return this.foodForm.id ? '更新菜单' : '新增菜单';
    },
    foodListSummary() {
      return this.displayFoods.length + ' / ' + this.foods.length;
    },
    tagSubmitText() {
      return this.tagForm.id ? '更新标签' : '新增标签';
    },
    tagEditorTitle() {
      return this.tagForm.id ? '编辑标签' : '新增标签';
    },
    systemTagCount() {
      return this.tags.filter((tag) => tag.tagType === 'SYSTEM').length;
    },
    customTagCount() {
      return this.tags.filter((tag) => tag.tagType !== 'SYSTEM').length;
    },
    displayTags() {
      return this.tags.map((tag) => ({
        ...tag,
        displayName: (tag.icon ? tag.icon + ' ' : '') + tag.name,
        displayTypeText: tag.tagType === 'SYSTEM' ? '系统标签' : '自定义',
        displayEnabledText: tag.enabled === 1 ? '启用' : '停用',
        isSystemTag: tag.tagType === 'SYSTEM'
      }));
    },
    favoriteTagMap() {
      return this.preference.favoriteTagIds.reduce((result, tagId) => {
        result[tagId] = true;
        return result;
      }, {});
    },
    avoidTagMap() {
      return this.preference.avoidTagIds.reduce((result, tagId) => {
        result[tagId] = true;
        return result;
      }, {});
    },
    selectedFoodTagMap() {
      return this.foodForm.tagIds.reduce((result, tagId) => {
        result[tagId] = true;
        return result;
      }, {});
    },
    filteredFoods() {
      const keyword = this.foodKeyword.trim().toLowerCase();
      return this.foods.filter((food) => {
        const matchKeyword = !keyword || [food.name, food.emoji, ...(getTagNames(this.tags, food.tagIds || []))]
          .filter(Boolean)
          .join(' ')
          .toLowerCase()
          .includes(keyword);
        const matchEnabled = this.foodEnabledFilter === 'ALL'
          || (this.foodEnabledFilter === 'ENABLED' && food.enabled === 1)
          || (this.foodEnabledFilter === 'DISABLED' && food.enabled !== 1);
        return matchKeyword && matchEnabled;
      });
    },
    displayFoods() {
      return this.filteredFoods.map((food) => ({
        ...food,
        displayEmoji: resolveFoodEmoji(food),
        displayPrice: formatPrice(food.referencePrice),
        displayEnabledText: resolveFoodEnabledText(food),
        displayToggleText: resolveFoodToggleText(food),
        displayTagNames: getTagNames(this.tags, food.tagIds || [])
      }));
    },
    filteredHistories() {
      const keyword = this.historyKeyword.trim().toLowerCase();
      return this.histories.filter((record) => {
        if (!keyword) {
          return true;
        }
        return [record.foodName, record.aiTipPreview, mapModeLabel(record.mode), mapStatusLabel(record.resultStatus)]
          .filter(Boolean)
          .join(' ')
          .toLowerCase()
          .includes(keyword);
      });
    },
    displayHistories() {
      return this.filteredHistories.map((record) => ({
        ...record,
        displayModeLabel: mapModeLabel(record.mode),
        displayStatusLabel: mapStatusLabel(record.resultStatus),
        displayShakeTime: formatTime(record.shakeTime)
      }));
    },
    historyListSummary() {
      return this.displayHistories.length + ' / ' + this.historyTotal;
    },
    historyPageCount() {
      return Math.max(1, Math.ceil((this.historyTotal || 0) / HISTORY_PAGE_SIZE));
    },
    canPrevHistoryPage() {
      return this.historyPageNo > 1;
    },
    canNextHistoryPage() {
      return this.historyPageNo < this.historyPageCount;
    },
    needsFoodSetup() {
      return !this.foods.length || !this.tags.length;
    },
    needsPreferenceSetup() {
      return !this.preference.favoriteTagIds.length && !this.preference.avoidTagIds.length;
    },
    hasFoodFilters() {
      return !!this.foodKeyword.trim() || this.foodEnabledFilter !== 'ALL';
    },
    hasHistoryFilters() {
      return !!this.historyKeyword.trim() || !!this.historyStatus;
    },
    isTagFormPristine() {
      return !this.tagForm.id
        && !normalizeCompareText(this.tagForm.name)
        && !normalizeCompareText(this.tagForm.icon)
        && Number(this.tagForm.sort) === 50
        && Number(this.tagForm.enabled) === 1;
    },
    tagValidationError() {
      return this.isTagFormPristine ? '' : this.validateTagForm();
    },
    tagAssistText() {
      if (this.isTagFormPristine) {
        return '新增后会立刻出现在偏好和菜单表单里。';
      }
      return this.tagValidationError || '标签信息完整, 保存后就能直接用于筛选和推荐。';
    },
    isFoodFormPristine() {
      return !this.foodForm.id
        && !normalizeCompareText(this.foodForm.name)
        && !normalizeCompareText(this.foodForm.emoji)
        && !normalizeCompareText(this.foodForm.steps)
        && !normalizeCompareText(this.foodForm.imageUrl)
        && !normalizeCompareText(this.foodForm.orderUrl)
        && Number(this.foodForm.weight) === 80
        && Number(this.foodForm.referencePrice) === 20
        && !(this.foodForm.tagIds || []).length;
    },
    foodValidationError() {
      return this.isFoodFormPristine ? '' : this.validateFoodForm();
    },
    foodAssistText() {
      if (this.isFoodFormPristine) {
        return '先填名称, 价格和至少 1 个标签, 提交后就能参与摇一摇。';
      }
      return this.foodValidationError || '菜单信息完整, 保存后这道菜就会进入推荐池。';
    },
    preferenceValidationError() {
      return this.validatePreferenceForm();
    },
    preferenceAssistText() {
      return this.preferenceValidationError || '预算和口味设置正常, 保存后会影响后续推荐权重。';
    },
    historyDetailModeLabel() {
      return this.historyDetail ? mapModeLabel(this.historyDetail.mode) : '-';
    },
    historyDetailStatusLabel() {
      return this.historyDetail ? mapStatusLabel(this.historyDetail.resultStatus) : '-';
    },
    historyDetailTimeText() {
      return this.historyDetail ? formatTime(this.historyDetail.shakeTime) : '-';
    },
    historyDetailAiTipText() {
      return this.historyDetail && this.historyDetail.aiTip ? this.historyDetail.aiTip : '暂无文案';
    },
    historyDetailStepsText() {
      return this.historyDetail && this.historyDetail.steps ? this.historyDetail.steps : '暂无步骤';
    },
    historyDetailPriceText() {
      return this.historyDetail ? formatPrice(this.historyDetail.referencePrice) : '-';
    },
    historyRejectFeedbackText() {
      if (!this.historyDetail) {
        return '';
      }
      return this.historyDetail.rejectFeedbackLabel || mapRejectFeedbackLabel(this.historyDetail.rejectFeedbackCode) || '';
    },
    statsOverviewItems() {
      if (!this.stats) {
        return [];
      }
      return [
        {
          label: '总摇一摇次数',
          value: String(this.stats.totalCount || 0),
          text: '累计触发过多少次推荐'
        },
        {
          label: '接受率',
          value: formatAcceptanceRate(this.stats.acceptanceRate),
          text: '已接受 / 已完成决策'
        },
        {
          label: '已接受',
          value: String(this.stats.acceptedCount || 0),
          text: '今晚确认就吃它的次数'
        },
        {
          label: '已拒绝',
          value: String(this.stats.rejectedCount || 0),
          text: '明确拒绝并继续再摇的次数'
        }
      ];
    },
    statsModeItems() {
      return ((this.stats && this.stats.modeStats) || []).map((item) => ({
        ...item,
        label: mapModeLabel(item.code)
      }));
    },
    statsRejectItems() {
      return ((this.stats && this.stats.rejectFeedbackStats) || []).map((item) => ({
        ...item,
        label: item.label || mapRejectFeedbackLabel(item.code)
      }));
    },
    statsDailyItems() {
      return (this.stats && this.stats.recentDailyStats) || [];
    }
  },
  onLoad() {
    this.onboardingDismissed = !!uni.getStorageSync(ONBOARDING_STORAGE_KEY);
    this.updateNavigationBarTitle();
    this.bootstrap();
  },
  methods: {
    selectTab(key) {
      this.activeTab = key;
      this.updateNavigationBarTitle();
      if (key === 'stats' && !this.stats) {
        this.loadStats();
      }
    },
    selectShakeMode(mode) {
      this.shakeMode = mode;
    },
    selectPreferenceMode(mode) {
      this.preference.lastMode = mode;
    },
    goToNextOnboardingStep() {
      if (!this.tags.length || !this.foods.length) {
        this.goToFoodTab();
        return;
      }
      if (this.needsPreferenceSetup) {
        this.goToPreferenceTab();
        return;
      }
      this.dismissOnboarding();
    },
    dismissOnboarding() {
      this.onboardingDismissed = true;
      uni.setStorageSync(ONBOARDING_STORAGE_KEY, 1);
    },
    onBudgetInput(event) {
      this.preference.maxBudget = event.detail.value;
    },
    onFoodKeywordInput(event) {
      this.foodKeyword = event.detail.value || '';
    },
    onHistoryKeywordInput(event) {
      this.historyKeyword = event.detail.value || '';
    },
    onFoodImportInput(event) {
      this.foodImportText = event.detail.value || '';
    },
    updateFoodField(field, event) {
      this.foodForm[field] = event.detail.value;
    },
    updateTagField(field, event) {
      this.tagForm[field] = event.detail.value;
    },
    setTagEnabled(value) {
      this.tagForm.enabled = value;
    },
    changeFoodEnabledFilter(value) {
      this.foodEnabledFilter = value;
    },
    clearFoodFilters() {
      this.foodKeyword = '';
      this.foodEnabledFilter = 'ALL';
    },
    clearHistoryFilters() {
      this.historyKeyword = '';
      this.historyStatus = '';
      this.historyPageNo = 1;
      this.loadHistories('', 1);
    },
    goToFoodTab() {
      this.selectTab('food');
    },
    goToPreferenceTab() {
      this.selectTab('preference');
    },
    updateNavigationBarTitle() {
      const titleMap = {
        shake: '下班摇摇吃',
        preference: '口味偏好',
        food: '菜单维护',
        stats: '统计看板',
        history: '决策历史'
      };
      uni.setNavigationBarTitle({
        title: titleMap[this.activeTab] || '下班摇摇吃'
      });
    },
    clearNotice() {
      this.message = '';
      this.error = '';
    },
    syncTagReferences() {
      const validTagMap = this.tags.reduce((result, tag) => {
        result[String(tag.id)] = true;
        return result;
      }, {});
      const filterIds = (tagIds) => (tagIds || []).filter((tagId) => validTagMap[String(tagId)]);
      this.preference.favoriteTagIds = filterIds(this.preference.favoriteTagIds);
      this.preference.avoidTagIds = filterIds(this.preference.avoidTagIds);
      this.foodForm.tagIds = filterIds(this.foodForm.tagIds);
    },
    showMessage(text) {
      this.message = text;
      this.error = '';
    },
    showError(text) {
      this.error = text;
      this.message = '';
    },
    validatePreferenceForm() {
      const budget = Number(this.preference.maxBudget);
      if (Number.isNaN(budget) || budget <= 0 || budget > 9999) {
        return '预算上限需要填写 1 到 9999 之间的数字';
      }
      const overlap = this.preference.favoriteTagIds.some((tagId) => this.preference.avoidTagIds.indexOf(tagId) >= 0);
      if (overlap) {
        return '同一个标签不能同时出现在喜欢和避免里';
      }
      return '';
    },
    validateTagForm() {
      const name = normalizeCompareText(this.tagForm.name);
      if (!name) {
        return '标签名称不能为空';
      }
      const duplicate = this.tags.some((tag) => tag.id !== this.tagForm.id && normalizeCompareText(tag.name) === name);
      if (duplicate) {
        return '标签名称已存在, 请换一个';
      }
      const sort = Number(this.tagForm.sort);
      if (!Number.isInteger(sort) || sort < 0 || sort > 9999) {
        return '标签排序需要填写 0 到 9999 之间的整数';
      }
      return '';
    },
    validateFoodForm() {
      const name = normalizeCompareText(this.foodForm.name);
      if (!name) {
        return '菜单名称不能为空';
      }
      const duplicate = this.foods.some((food) => food.id !== this.foodForm.id && normalizeCompareText(food.name) === name);
      if (duplicate) {
        return '菜单名称已存在, 建议编辑原有菜单而不是重复新增';
      }
      if (!this.foodForm.tagIds.length) {
        return '请至少选择一个标签';
      }
      const weight = Number(this.foodForm.weight);
      if (!Number.isInteger(weight) || weight <= 0 || weight > 1000) {
        return '权重需要填写 1 到 1000 之间的整数';
      }
      const price = Number(this.foodForm.referencePrice);
      if (Number.isNaN(price) || price < 0 || price > 9999) {
        return '参考价格需要填写 0 到 9999 之间的数字';
      }
      if (this.foodForm.imageUrl && !isValidHttpUrl(this.foodForm.imageUrl)) {
        return '图片地址需要以 http:// 或 https:// 开头';
      }
      if (this.foodForm.orderUrl && !isValidHttpUrl(this.foodForm.orderUrl)) {
        return '平台搜索链接需要以 http:// 或 https:// 开头';
      }
      return '';
    },
    async bootstrap() {
      this.loading = true;
      this.clearNotice();
      try {
        const result = await Promise.all([
          getAllTags(),
          getFoods(),
          getPreference(),
          getHistories(this.historyStatus, this.historyPageNo, HISTORY_PAGE_SIZE),
          getShakeQuota(),
          getHistoryStats()
        ]);
        const tagList = result[0] || [];
        const foodList = result[1] || [];
        const preferenceData = result[2] || {};
        const historyPage = result[3] || {};
        this.tags = tagList;
        this.foods = foodList;
        this.histories = historyPage.records || [];
        this.historyTotal = historyPage.total || 0;
        this.preference.favoriteTagIds = preferenceData.favoriteTagIds || [];
        this.preference.avoidTagIds = preferenceData.avoidTagIds || [];
        this.preference.maxBudget = preferenceData.maxBudget !== undefined ? preferenceData.maxBudget : 30;
        this.preference.lastMode = preferenceData.lastMode || 'LAZY';
        this.shakeMode = this.preference.lastMode;
        this.quota = result[4] || null;
        this.stats = result[5] || null;
        this.syncTagReferences();
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    async loadTags() {
      this.tags = await getAllTags();
      this.syncTagReferences();
    },
    async loadFoods() {
      this.foods = await getFoods();
    },
    async loadQuota() {
      this.quota = await getShakeQuota();
    },
    async loadStats() {
      this.statsLoading = true;
      try {
        this.stats = await getHistoryStats();
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.statsLoading = false;
      }
    },
    async loadHistories(status, pageNo = this.historyPageNo) {
      this.historyLoading = true;
      try {
        const page = await getHistories(status || '', pageNo, HISTORY_PAGE_SIZE);
        this.histories = page.records || [];
        this.historyTotal = page.total || 0;
        this.historyPageNo = pageNo;
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.historyLoading = false;
      }
    },
    async handleShake() {
      if (this.needsFoodSetup) {
        this.showError('请先补齐菜单和标签, 再开始摇一摇');
        return;
      }
      this.loading = true;
      this.clearNotice();
      try {
        this.shakeResult = await shake({ mode: this.shakeMode, triggerType: 'CLICK' });
        const note = this.shakeDecisionNote ? ' 推荐规则已在结果里标出来了。' : '';
        this.showMessage('推荐已生成, 可以直接确认今晚吃什么了。' + note);
        await Promise.all([
          this.loadHistories(this.historyStatus, this.historyPageNo),
          this.loadQuota(),
          this.loadStats()
        ]);
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    async handleDecision(action, rejectFeedbackCode) {
      if (!this.shakeResult || !this.shakeResult.historyId) {
        return;
      }
      this.loading = true;
      this.clearNotice();
      try {
        if (action === 'accept') {
          await acceptShake(this.shakeResult.historyId);
          this.showMessage('已确认, 今晚就按这个吃。');
        } else {
          await rejectShake(this.shakeResult.historyId, { rejectFeedbackCode });
          this.shakeResult = null;
          this.showMessage('已拒绝, 再摇一次就会生成新的推荐。');
        }
        await Promise.all([
          this.loadHistories(this.historyStatus, this.historyPageNo),
          this.loadQuota(),
          this.loadStats()
        ]);
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    handleRejectDecision() {
      const self = this;
      uni.showActionSheet({
        itemList: this.REJECT_FEEDBACK_OPTIONS.map((item) => item.label),
        success(res) {
          const option = self.REJECT_FEEDBACK_OPTIONS[res.tapIndex];
          if (!option) {
            return;
          }
          self.handleDecision('reject', option.value);
        }
      });
    },
    async savePreference() {
      const preferenceError = this.validatePreferenceForm();
      if (preferenceError) {
        this.showError(preferenceError);
        return;
      }
      this.loading = true;
      this.clearNotice();
      try {
        const saved = await updatePreference({
          favoriteTagIds: this.preference.favoriteTagIds,
          avoidTagIds: this.preference.avoidTagIds,
          maxBudget: Number(this.preference.maxBudget || 0),
          lastMode: this.preference.lastMode
        });
        this.preference.favoriteTagIds = saved.favoriteTagIds || [];
        this.preference.avoidTagIds = saved.avoidTagIds || [];
        this.preference.maxBudget = saved.maxBudget !== undefined ? saved.maxBudget : 30;
        this.preference.lastMode = saved.lastMode || 'LAZY';
        this.shakeMode = this.preference.lastMode;
        this.showMessage('偏好已保存。');
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    togglePreferenceTag(listKey, tagId) {
      const current = this.preference[listKey] || [];
      if (current.indexOf(tagId) >= 0) {
        this.preference[listKey] = current.filter((id) => id !== tagId);
        return;
      }
      this.preference[listKey] = current.concat(tagId);
    },
    toggleFoodTag(tagId) {
      if (this.foodForm.tagIds.indexOf(tagId) >= 0) {
        this.foodForm.tagIds = this.foodForm.tagIds.filter((id) => id !== tagId);
        return;
      }
      this.foodForm.tagIds = this.foodForm.tagIds.concat(tagId);
    },
    async submitTag() {
      const tagError = this.validateTagForm();
      if (tagError) {
        this.showError(tagError);
        return;
      }
      this.loading = true;
      this.clearNotice();
      try {
        const payload = {
          name: this.tagForm.name.trim(),
          icon: this.tagForm.icon ? this.tagForm.icon.trim() : '',
          sort: Number(this.tagForm.sort || 0),
          enabled: Number(this.tagForm.enabled)
        };
        if (this.tagForm.id) {
          await updateTag(this.tagForm.id, payload);
          this.showMessage('标签已更新。');
        } else {
          await createTag(payload);
          this.showMessage('标签已新增。');
        }
        this.tagForm = createEmptyTagForm();
        await this.loadTags();
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    pickTag(tagId) {
      const target = this.tags.find((tag) => tag.id === tagId);
      if (!target) {
        return;
      }
      this.tagForm = {
        id: target.id,
        name: target.name || '',
        icon: target.icon || '',
        sort: target.sort !== undefined ? target.sort : 50,
        enabled: target.enabled === 0 ? 0 : 1
      };
      this.showMessage('标签详情已载入。');
    },
    resetTagForm() {
      this.tagForm = createEmptyTagForm();
      this.clearNotice();
    },
    confirmDeleteTag(id) {
      const self = this;
      uni.showModal({
        title: '确认删除',
        content: '确认删除这条标签吗?',
        success: async function(result) {
          if (!result.confirm) {
            return;
          }
          self.loading = true;
          self.clearNotice();
          try {
            await deleteTag(id);
            if (self.tagForm.id === id) {
              self.tagForm = createEmptyTagForm();
            }
            self.showMessage('标签已删除。');
            await self.loadTags();
          } catch (requestError) {
            self.showError(requestError.message);
          } finally {
            self.loading = false;
          }
        }
      });
    },
    async submitFood() {
      const foodError = this.validateFoodForm();
      if (foodError) {
        this.showError(foodError);
        return;
      }
      this.loading = true;
      this.clearNotice();
      try {
        const payload = {
          name: this.foodForm.name,
          emoji: this.foodForm.emoji,
          steps: this.foodForm.steps,
          imageUrl: this.foodForm.imageUrl,
          orderUrl: this.foodForm.orderUrl,
          weight: Number(this.foodForm.weight || 0),
          referencePrice: Number(this.foodForm.referencePrice || 0),
          tagIds: this.foodForm.tagIds
        };
        if (this.foodForm.id) {
          await updateFood(this.foodForm.id, payload);
          this.showMessage('菜单已更新。');
        } else {
          await createFood(payload);
          this.showMessage('菜单已新增。');
        }
        this.foodForm = resetFoodForm();
        await this.loadFoods();
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    async handleFoodExport() {
      this.loading = true;
      this.clearNotice();
      try {
        const items = await exportFoods();
        this.foodImportText = JSON.stringify(items, null, 2);
        this.showMessage(`已导出 ${items.length} 条菜单 JSON。`);
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    async handleFoodImport() {
      const source = String(this.foodImportText || '').trim();
      if (!source) {
        this.showError('请先粘贴要导入的 JSON。');
        return;
      }
      this.loading = true;
      this.clearNotice();
      try {
        const items = JSON.parse(source);
        if (!Array.isArray(items)) {
          throw new Error('导入内容必须是 JSON 数组。');
        }
        this.foodImportText = JSON.stringify(items, null, 2);
        const result = await importFoods({ items });
        this.showMessage(`导入完成: 新增 ${result.createdCount} 条, 更新 ${result.updatedCount} 条, 跳过 ${result.skippedCount} 条。`);
        await this.loadFoods();
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    async pickFood(foodId) {
      this.loading = true;
      this.clearNotice();
      try {
        const detail = await getFood(foodId);
        this.foodForm = normalizeFoodForm(detail);
        this.activeTab = 'food';
        this.updateNavigationBarTitle();
        this.showMessage('菜单详情已载入。');
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    resetFood() {
      this.foodForm = resetFoodForm();
      this.clearNotice();
    },
    confirmDeleteFood(id) {
      const self = this;
      uni.showModal({
        title: '确认删除',
        content: '确认删除这条菜单吗?',
        success: async function(result) {
          if (!result.confirm) {
            return;
          }
          self.loading = true;
          self.clearNotice();
          try {
            await deleteFood(id);
            if (self.foodForm.id === id) {
              self.foodForm = resetFoodForm();
            }
            self.showMessage('菜单已删除。');
            await self.loadFoods();
          } catch (requestError) {
            self.showError(requestError.message);
          } finally {
            self.loading = false;
          }
        }
      });
    },
    async toggleFoodEnabled(food) {
      this.loading = true;
      this.clearNotice();
      try {
        await updateFoodEnabled(food.id, food.enabled !== 1);
        this.showMessage(food.enabled === 1 ? '菜单已停用。' : '菜单已启用。');
        await this.loadFoods();
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    async changeHistoryStatus(status) {
      this.historyStatus = status;
      this.historyPageNo = 1;
      await this.loadHistories(status, 1);
    },
    async changeHistoryPage(pageNo) {
      if (pageNo < 1 || pageNo > this.historyPageCount || pageNo === this.historyPageNo) {
        return;
      }
      await this.loadHistories(this.historyStatus, pageNo);
    },
    async openHistory(id) {
      this.loading = true;
      this.clearNotice();
      try {
        this.historyDetail = await getHistory(id);
      } catch (requestError) {
        this.showError(requestError.message);
      } finally {
        this.loading = false;
      }
    },
    closeHistoryDetail() {
      this.historyDetail = null;
    },
    openShakeOrderActions() {
      this.openOrderActions(this.shakeResult);
    },
    openHistoryOrderActions() {
      this.openOrderActions(this.historyDetail);
    },
    openFoodOrderActions(foodId) {
      const food = this.foods.find((item) => item.id === foodId);
      this.openOrderActions(food);
    },
    openOrderActions(item) {
      if (!item || !item.orderUrl) {
        return;
      }
      const self = this;
      uni.showActionSheet({
        itemList: ['复制搜索链接', '复制搜索词', '尝试 web-view 打开'],
        success(res) {
          if (res.tapIndex === 0) {
            self.copyText(item.orderUrl, '链接已复制');
            return;
          }
          if (res.tapIndex === 1) {
            self.copySearchKeyword(item);
            return;
          }
          uni.navigateTo({
            url: '/pages/webview/index?url=' + encodeURIComponent(item.orderUrl)
          });
        }
      });
    },
    copyHistorySearchKeyword() {
      this.copySearchKeyword(this.historyDetail);
    },
    copySearchKeyword(item) {
      const keyword = buildOrderKeyword(item);
      if (!keyword) {
        this.showError('没有可复制的搜索词');
        return;
      }
      this.copyText(keyword, '搜索词已复制');
    },
    copyText(value, toastTitle) {
      uni.setClipboardData({
        data: value,
        success() {
          uni.showToast({ title: toastTitle, icon: 'none' });
        }
      });
    }
  }
};
</script>

<style lang="scss">
.page-root {
  min-height: 100vh;
  --bg-accent: rgba(206, 92, 71, 0.14);
  --bg-start: #f8f1e7;
  --bg-mid: #f5ecdf;
  --bg-end: #f1e4d4;
  --surface: #fffaf4;
  --surface-soft: #fbf2e8;
  --surface-alt: #f7f0e7;
  --surface-ghost: #f6eee4;
  --surface-chip: #f1e2d2;
  --border: #ead8c6;
  --border-soft: #efe2d5;
  --text-main: #35241f;
  --text-strong: #4d3730;
  --text-muted: #876c63;
  --text-subtle: #a0867c;
  --primary: #cf5c47;
  --primary-dark: #a84433;
  --primary-soft: rgba(207, 92, 71, 0.1);
  --primary-soft-strong: rgba(207, 92, 71, 0.18);
  --danger-bg: #f6dfd9;
  --danger-border: #ecc1b8;
  --danger-text: #a94d40;
  --shadow: rgba(79, 47, 38, 0.08);
}

.page-shell {
  height: 100vh;
  padding: 20rpx 20rpx calc(176rpx + env(safe-area-inset-bottom));
  background:
    radial-gradient(circle at top left, var(--bg-accent), transparent 34%),
    linear-gradient(180deg, var(--bg-start) 0%, var(--bg-mid) 48%, var(--bg-end) 100%);
}

.hero-card,
.section-card,
.list-card,
.modal-card,
.banner {
  background: var(--surface);
  border: 2rpx solid var(--border);
  border-radius: 28rpx;
  box-shadow: 0 12rpx 32rpx var(--shadow);
}

.shake-card {
  padding-bottom: 18rpx;
}

.hero-card,
.section-card,
.banner {
  padding: 20rpx;
  margin-bottom: 16rpx;
}

.hero-card {
  position: relative;
  overflow: hidden;
  padding: 26rpx 24rpx 22rpx;
  background: linear-gradient(180deg, #fffaf4 0%, #fffcf8 100%);
}

.hero-topbar {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-card::after {
  content: '';
  position: absolute;
  right: -22rpx;
  top: -28rpx;
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: var(--primary-soft);
}

.eyebrow,
.section-kicker,
.mode-desc,
.list-meta,
.preview-text,
.time-text,
.detail-title,
.page-subtitle,
.result-meta {
  color: var(--text-muted);
}

.page-title,
.section-title,
.result-name,
.list-name {
  display: block;
  color: var(--text-main);
  font-weight: 700;
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 42rpx;
  line-height: 1.16;
  margin-top: 10rpx;
}

.page-subtitle {
  display: block;
  margin-top: 8rpx;
  line-height: 1.5;
  font-size: 24rpx;
  max-width: none;
  position: relative;
  z-index: 1;
}

.hero-tab-name {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 80rpx;
  padding: 6rpx 12rpx;
  border-radius: 999rpx;
  background: var(--primary-soft);
  color: var(--primary-dark);
  font-size: 20rpx;
  font-weight: 700;
  position: relative;
  z-index: 1;
}

.status-row,
.tag-row,
.inline-actions,
.mode-grid,
.double-grid {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
}

.status-row,
.mode-grid,
.field-block,
.list-block,
.detail-box {
  margin-top: 14rpx;
}

.bottom-nav {
  position: fixed;
  left: 16rpx;
  right: 16rpx;
  bottom: calc(12rpx + env(safe-area-inset-bottom));
  z-index: 20;
  display: flex;
  gap: 10rpx;
  padding: 10rpx;
  border-radius: 28rpx;
  background: rgba(255, 250, 244, 0.98);
  border: 2rpx solid rgba(234, 216, 198, 0.95);
  box-shadow: 0 14rpx 30rpx rgba(79, 47, 38, 0.12);
  backdrop-filter: blur(16rpx);
}

.bottom-nav-item {
  flex: 1 1 0;
  min-width: 0;
  min-height: 80rpx;
  padding: 10rpx 8rpx;
  border-radius: 22rpx;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  color: var(--text-muted);
  transition: all 0.2s ease;
}

.bottom-nav-item::before {
  content: '';
  position: absolute;
  top: 8rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 30rpx;
  height: 5rpx;
  border-radius: 999rpx;
  background: transparent;
}

.bottom-nav-item.active {
  background: linear-gradient(180deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: #fff8f4;
  box-shadow: 0 10rpx 20rpx rgba(168, 68, 51, 0.24);
}

.bottom-nav-item.active::before {
  background: rgba(255, 248, 244, 0.92);
}

.bottom-nav-icon,
.bottom-nav-label {
  display: block;
  text-align: center;
}

.bottom-nav-icon {
  width: 38rpx;
  height: 38rpx;
  border-radius: 50%;
  background: var(--primary-soft);
  color: var(--primary);
  font-size: 22rpx;
  font-weight: 700;
  line-height: 38rpx;
  box-shadow: inset 0 0 0 2rpx rgba(207, 92, 71, 0.08);
}

.bottom-nav-label {
  font-size: 24rpx;
  font-weight: 700;
  line-height: 1.2;
}

.bottom-nav-item.active .bottom-nav-icon {
  background: rgba(255, 248, 244, 0.18);
  color: #fff8f4;
  box-shadow: inset 0 0 0 2rpx rgba(255, 248, 244, 0.16);
}

.status-pill,
.tag-pill,
.mode-chip {
  border-radius: 22rpx;
}

.status-pill,
.tag-pill {
  padding: 8rpx 18rpx;
  background: var(--surface-chip);
  color: var(--text-strong);
  font-size: 22rpx;
}

.status-pill {
  flex: 1 1 0;
  min-height: 76rpx;
  min-width: 0;
  padding: 10rpx 8rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4rpx;
  text-align: center;
  background: rgba(255, 250, 244, 0.92);
  border: 2rpx solid rgba(234, 216, 198, 0.9);
  position: relative;
  z-index: 1;
}

.status-label,
.status-value {
  display: block;
}

.status-label {
  color: var(--text-subtle);
  font-size: 20rpx;
}

.status-value {
  color: var(--text-main);
  font-size: 28rpx;
  font-weight: 700;
}

.tag-pill.active,
.mode-chip.active {
  background: var(--primary);
  color: #fff8f4;
  border-color: var(--primary);
  box-shadow: 0 8rpx 16rpx rgba(168, 68, 51, 0.16);
}

.tag-pill.danger {
  background: var(--danger-bg);
  color: var(--danger-text);
}

.tag-pill.subtle {
  background: var(--surface-soft);
}

.tag-pill.active-soft {
  background: #f5ddd7;
  color: var(--primary-dark);
}

.mode-chip {
  flex: 1 1 44%;
  min-height: 80rpx;
  min-width: 0;
  background: var(--surface-soft);
  padding: 14rpx;
  border: 2rpx solid var(--border);
  box-shadow: 0 8rpx 18rpx rgba(79, 47, 38, 0.05);
}

.mode-name {
  display: block;
  font-weight: 700;
  font-size: 28rpx;
  line-height: 1.25;
}

.mode-desc {
  display: block;
  margin-top: 4rpx;
  line-height: 1.4;
  font-size: 20rpx;
}

.mode-grid.compact .mode-chip {
  width: auto;
  min-width: 200rpx;
  min-height: 76rpx;
  padding: 14rpx 18rpx;
}

.primary-btn,
.ghost-btn,
.danger-btn {
  border-radius: 24rpx;
  font-size: 24rpx;
  min-height: 74rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1.2;
}

.primary-btn {
  background: linear-gradient(180deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: #fff8f4;
  box-shadow: 0 10rpx 20rpx rgba(168, 68, 51, 0.18);
}

.ghost-btn {
  background: var(--surface-ghost);
  color: var(--primary-dark);
  border: 2rpx solid #e7d4c4;
}

.danger-btn {
  background: var(--danger-bg);
  color: var(--danger-text);
  border: 2rpx solid var(--danger-border);
}

.cta-btn {
  margin-top: 12rpx;
  min-height: 84rpx;
  font-size: 28rpx;
  font-weight: 700;
}

.half {
  flex: 1 1 0;
}

.tiny {
  min-width: 144rpx;
  min-height: 58rpx;
  padding: 0 18rpx;
  font-size: 22rpx;
}

.wrap {
  align-items: center;
}

.result-card,
.list-card,
.detail-box {
  background: #fcfbf7;
  border-radius: 24rpx;
  padding: 18rpx;
  border: 2rpx solid var(--border-soft);
}

.banner,
.empty-box,
.detail-box {
  position: relative;
  overflow: hidden;
}

.banner::before,
.empty-box::before,
.detail-box::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 8rpx;
  border-radius: 8rpx;
  background: var(--primary-soft-strong);
}

.result-head,
.list-top {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.result-card {
  margin-top: 14rpx;
  padding: 18rpx;
  background: linear-gradient(180deg, #fff6ed 0%, #fffaf5 100%);
  box-shadow: 0 14rpx 28rpx rgba(79, 47, 38, 0.08);
}

.result-emoji {
  font-size: 60rpx;
}

.result-main {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.result-stat-row {
  display: flex;
  gap: 10rpx;
  flex-wrap: wrap;
  margin-top: 12rpx;
}

.food-card-stats,
.history-card-stats,
.detail-stat-row {
  display: flex;
  gap: 10rpx;
  flex-wrap: wrap;
  margin-top: 12rpx;
}

.result-meta,
.list-meta,
.preview-text,
.time-text,
.detail-title {
  font-size: 22rpx;
  line-height: 1.55;
}

.section-kicker,
.eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
  background: var(--primary-soft);
  color: var(--primary-dark);
  font-size: 20rpx;
  font-weight: 600;
  position: relative;
  z-index: 1;
}

.section-title {
  margin-top: 8rpx;
  font-size: 34rpx;
  line-height: 1.2;
}

.section-title.small {
  font-size: 28rpx;
}

.shake-caption {
  display: block;
  margin-top: 8rpx;
  color: var(--text-muted);
  font-size: 22rpx;
  line-height: 1.5;
}

.top-space {
  margin-top: 10rpx;
}

.guide-card,
.filter-panel,
.overview-panel {
  margin-top: 14rpx;
  padding: 16rpx;
  border-radius: 22rpx;
  background: rgba(251, 248, 242, 0.92);
  border: 2rpx solid var(--border-soft);
}

.slim-guide-card {
  padding: 14rpx 16rpx;
}

.onboarding-card,
.rule-panel {
  margin-top: 14rpx;
  padding: 16rpx;
  border-radius: 22rpx;
  background: rgba(255, 248, 242, 0.92);
  border: 2rpx solid var(--border-soft);
}

.guide-checklist,
.rule-list,
.overview-grid {
  margin-top: 12rpx;
}

.guide-check-item + .guide-check-item,
.rule-item + .rule-item {
  margin-top: 10rpx;
}

.guide-check-item,
.rule-item {
  display: flex;
  gap: 12rpx;
  align-items: flex-start;
}

.overview-grid {
  display: flex;
  gap: 12rpx;
  flex-wrap: wrap;
}

.overview-card {
  flex: 1 1 42%;
  min-width: 0;
  padding: 16rpx;
  border-radius: 20rpx;
  background: rgba(255, 252, 248, 0.96);
  border: 2rpx solid rgba(234, 216, 198, 0.88);
}

.overview-value,
.overview-label,
.overview-text {
  display: block;
}

.overview-value {
  color: var(--text-main);
  font-size: 34rpx;
  line-height: 1.2;
  font-weight: 700;
}

.overview-label {
  margin-top: 4rpx;
  color: var(--text-strong);
  font-size: 22rpx;
  font-weight: 700;
}

.overview-text {
  margin-top: 6rpx;
  color: var(--text-muted);
  font-size: 20rpx;
  line-height: 1.45;
}

.guide-check-copy {
  flex: 1;
  min-width: 0;
}

.guide-check-title,
.rule-label {
  display: block;
  color: var(--text-main);
  font-size: 24rpx;
  font-weight: 700;
  line-height: 1.35;
}

.guide-check-text,
.rule-text {
  display: block;
  margin-top: 4rpx;
  color: var(--text-muted);
  font-size: 22rpx;
  line-height: 1.5;
}

.guide-title,
.guide-text {
  display: block;
}

.guide-title {
  color: var(--text-main);
  font-size: 28rpx;
  font-weight: 700;
}

.guide-text {
  margin-top: 6rpx;
  color: var(--text-muted);
  font-size: 22rpx;
  line-height: 1.55;
}

.shake-cta-panel {
  margin-top: 14rpx;
  padding: 16rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, rgba(255, 244, 237, 0.98) 0%, rgba(255, 249, 243, 0.94) 100%);
  border: 2rpx solid #ecd7c8;
  box-shadow: 0 10rpx 20rpx rgba(79, 47, 38, 0.06);
}

.shake-cta-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12rpx;
}

.shake-cta-copy {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 4rpx;
}

.shake-cta-badge {
  flex: 0 0 auto;
}

.shake-cta-meta {
  display: flex;
  gap: 12rpx;
  flex-wrap: wrap;
  margin-top: 10rpx;
}

.shake-cta-note {
  display: inline-flex;
  align-items: center;
  min-height: 44rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  background: var(--primary-soft);
  color: var(--primary-dark);
  font-size: 20rpx;
  line-height: 1.2;
}

.form-caption,
.group-tip {
  display: block;
  color: var(--text-muted);
  font-size: 22rpx;
  line-height: 1.5;
}

.form-caption {
  margin-top: 8rpx;
}

.preference-summary-row {
  display: flex;
  gap: 10rpx;
  flex-wrap: wrap;
  margin-top: 14rpx;
}

.form-panel {
  margin-top: 14rpx;
  padding: 18rpx;
  background: rgba(250, 249, 244, 0.92);
  border: 2rpx solid var(--border-soft);
  border-radius: 24rpx;
}

.form-group + .form-group {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 2rpx dashed #e2d2c6;
}

.group-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.group-copy {
  flex: 1;
  min-width: 0;
}

.group-title {
  display: block;
  color: var(--text-main);
  font-size: 24rpx;
  font-weight: 700;
}

.mini-stat {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 88rpx;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: var(--surface-chip);
  color: var(--primary-dark);
  font-size: 22rpx;
  font-weight: 700;
}

.food-tag-row {
  margin-top: 12rpx;
}

.food-tag-row .tag-pill {
  min-height: 60rpx;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 18rpx;
  border: 2rpx solid transparent;
}

.action-bar {
  margin-top: 18rpx;
  padding-top: 16rpx;
  border-top: 2rpx solid #ebdfd4;
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
}

.form-hint {
  margin-top: 14rpx;
  padding: 14rpx 16rpx;
  border-radius: 20rpx;
  border: 2rpx solid transparent;
}

.form-hint.good {
  background: rgba(255, 247, 239, 0.92);
  border-color: rgba(234, 216, 198, 0.88);
}

.form-hint.danger {
  background: var(--danger-bg);
  border-color: var(--danger-border);
}

.form-hint-text {
  display: block;
  color: var(--text-muted);
  font-size: 22rpx;
  line-height: 1.5;
}

.form-hint.danger .form-hint-text {
  color: var(--danger-text);
}

.action-bar.compact {
  margin-top: 12rpx;
  padding-top: 0;
  border-top: 0;
}

.list-head {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.compact-head {
  margin-top: 14rpx;
}

.compact-filter-panel {
  background: rgba(255, 250, 244, 0.96);
  box-shadow: inset 0 0 0 2rpx rgba(234, 216, 198, 0.35);
}

.filter-chip-row {
  margin-top: 12rpx;
}

.status-filter-row {
  margin-top: 14rpx;
}

.tag-summary-row {
  margin-top: 12rpx;
}

.tag-editor-panel {
  margin-top: 14rpx;
  padding: 16rpx;
  border-radius: 22rpx;
  background: rgba(255, 244, 237, 0.9);
  border: 2rpx solid #ecd7c8;
}

.tag-library-list {
  margin-top: 16rpx;
}

.tag-library-card + .tag-library-card {
  margin-top: 12rpx;
}

.tag-library-card {
  padding: 16rpx;
  border-radius: 22rpx;
  background: #fcfbf7;
  border: 2rpx solid var(--border-soft);
}

.tag-library-head {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8rpx;
}

.tag-library-name {
  display: block;
  color: var(--text-main);
  font-size: 28rpx;
  font-weight: 700;
  line-height: 1.3;
}

.tag-library-meta {
  display: block;
  margin-top: 8rpx;
  color: var(--text-muted);
  font-size: 22rpx;
  line-height: 1.4;
}

.result-actions {
  margin-top: 14rpx;
}

.food-actions,
.history-actions,
.modal-actions {
  margin-top: 12rpx;
}

.hint-box {
  background: #fff4ec;
}

.preview-box {
  margin-top: 12rpx;
  background: #fbf2e8;
}

.history-list {
  position: relative;
}

.history-list::before {
  content: '';
  position: absolute;
  left: 18rpx;
  top: 16rpx;
  bottom: 16rpx;
  width: 2rpx;
  background: linear-gradient(180deg, rgba(207, 92, 71, 0.26) 0%, rgba(207, 92, 71, 0.08) 100%);
}

.history-card {
  position: relative;
  margin-left: 18rpx;
  padding-left: 28rpx;
}

.history-card::before {
  content: '';
  position: absolute;
  left: -12rpx;
  top: 26rpx;
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  background: var(--primary);
  box-shadow: 0 0 0 6rpx rgba(207, 92, 71, 0.12);
}

.list-name,
.result-name {
  font-size: 30rpx;
  line-height: 1.3;
}

.detail-text,
.detail-link {
  display: block;
  line-height: 1.7;
  word-break: break-all;
}

.preview-box .preview-text {
  display: block;
}

.result-card .detail-box,
.modal-card .detail-box {
  background: #fbf2e8;
}

.detail-box {
  padding-left: 24rpx;
}

.detail-link {
  color: var(--primary-dark);
}

.field-label {
  display: block;
  margin-bottom: 6rpx;
  font-weight: 600;
  color: var(--text-strong);
  font-size: 24rpx;
}

.field-block {
  width: 100%;
}

.double-grid {
  align-items: flex-start;
}

.double-grid .field-block {
  flex: 1 1 0;
  min-width: 0;
  margin-top: 0;
}

.text-input,
.text-area {
  display: block;
  width: 100%;
  background: #fcfbf7;
  border: 2rpx solid var(--border);
  border-radius: 20rpx;
  font-size: 26rpx;
  color: var(--text-main);
  box-sizing: border-box;
}

.text-input {
  height: 74rpx;
  line-height: 74rpx;
  padding: 0 20rpx;
}

.search-input {
  background: #fffdf9;
}

.text-area {
  min-height: 156rpx;
  padding: 16rpx 20rpx;
  line-height: 1.6;
}

.inline-actions {
  width: 100%;
}

.inline-actions .half {
  flex: 1 1 0;
}

.action-bar .half {
  flex: 1 1 0;
}

.inline-actions.wrap .tiny,
.inline-actions.wrap .time-text {
  flex: 0 0 auto;
}

.time-text {
  padding: 6rpx 12rpx;
  border-radius: 999rpx;
  background: var(--surface-chip);
}

.empty-box {
  margin-top: 14rpx;
  padding: 18rpx;
  text-align: center;
  color: var(--text-muted);
  background: #fcfbf7;
  border-radius: 22rpx;
  border: 2rpx dashed var(--border);
}

.banner {
  padding-left: 32rpx;
}

.banner.success {
  background: linear-gradient(180deg, #fff4eb 0%, #fff8f2 100%);
  border-color: #ebd7c8;
  color: var(--primary-dark);
}

.banner.success::before {
  background: #d17249;
}

.banner.success {
  color: var(--primary-dark);
}

.banner.error {
  background: linear-gradient(180deg, #f9f1ef 0%, #fcf8f7 100%);
  border-color: var(--danger-border);
  color: var(--danger-text);
}

.banner.error::before {
  background: var(--danger-text);
}

.banner.muted {
  background: linear-gradient(180deg, #fcf5ec 0%, #fffaf5 100%);
  border-color: #e7d9cc;
  color: var(--text-muted);
}

.banner.muted::before,
.empty-box::before {
  background: #b8a79a;
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(53, 36, 31, 0.42);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 18rpx 0 0;
}

.modal-card {
  position: relative;
  width: 100%;
  max-height: 78vh;
  padding: 30rpx 20rpx calc(20rpx + env(safe-area-inset-bottom));
  overflow: auto;
  border-radius: 32rpx 32rpx 0 0;
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
  box-shadow: 0 -12rpx 40rpx rgba(79, 47, 38, 0.16);
}

.modal-card > .primary-btn {
  margin-top: 16rpx;
}

.modal-card::before {
  content: '';
  position: absolute;
  top: 14rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 88rpx;
  height: 8rpx;
  border-radius: 999rpx;
  background: rgba(160, 134, 124, 0.4);
}

@media screen and (max-width: 375px) {
  .status-pill,
  .mode-chip,
  .half,
  .overview-card {
    width: 100%;
  }

  .status-pill {
    min-height: 88rpx;
  }

  .mode-grid.compact .mode-chip {
    min-width: 0;
  }

  .double-grid .field-block {
    flex-basis: 100%;
  }

  .group-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-bar .half {
    flex-basis: 100%;
  }

  .shake-cta-copy {
    flex-direction: column;
    align-items: flex-start;
  }

  .shake-cta-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .history-list::before {
    left: 16rpx;
  }

  .history-card {
    margin-left: 16rpx;
    padding-left: 24rpx;
  }

  .bottom-nav {
    left: 10rpx;
    right: 10rpx;
    gap: 8rpx;
    padding: 8rpx;
  }

  .bottom-nav-item {
    min-height: 78rpx;
    padding: 10rpx 6rpx;
  }
}
</style>

