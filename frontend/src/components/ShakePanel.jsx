import { REJECT_FEEDBACK_OPTIONS, MODE_OPTIONS } from "../constants";
import { formatPrice, getTagNames, mapModeLabel } from "../utils";
import ScoreDetailCard from "./ScoreDetailCard";
import { useEffect, useState } from "react";

function ShakePanel({ onDecision, onShake, quota, shakeMode, setShakeMode, shakeResult, tags }) {
  const [showRejectOptions, setShowRejectOptions] = useState(false);
  const resultTags = getTagNames(tags, shakeResult?.tagIds);

  useEffect(() => {
    setShowRejectOptions(false);
  }, [shakeResult?.historyId]);

  return (
    <section className="panel-grid">
      <div className="card hero-card">
        <div className="section-head">
          <div>
            <p className="section-kicker">今晚决策</p>
            <h2>摇一摇主流程</h2>
          </div>
          <span className="mode-badge">{mapModeLabel(shakeMode)}</span>
        </div>

        <div className="mode-selector">
          {MODE_OPTIONS.map((mode) => (
            <button
              key={mode.value}
              type="button"
              className={shakeMode === mode.value ? "choice active" : "choice"}
              onClick={() => setShakeMode(mode.value)}
            >
              <strong>{mode.label}</strong>
              <span>{mode.desc}</span>
            </button>
          ))}
        </div>

        <button type="button" className="primary-action" onClick={onShake}>
          现在摇一摇
        </button>

        {quota ? (
          <div className="quota-strip">
            <span>今日已摇 {quota.usedCount} 次</span>
            <span>剩余 {quota.remainingCount} 次</span>
            <span>上限 {quota.dailyLimit} 次</span>
          </div>
        ) : null}

        {shakeResult ? (
          <div className="result-card">
            <div className="result-title">
              <span className="result-emoji">{shakeResult.emoji || "🍽️"}</span>
              <div>
                <h3>{shakeResult.foodName}</h3>
                <p>
                  模式 {mapModeLabel(shakeResult.mode)} · AI 来源 {shakeResult.aiSource} · 价格 ¥{formatPrice(shakeResult.referencePrice)}
                </p>
              </div>
            </div>
            <div className="chip-row">
              {resultTags.map((tagName) => (
                <span key={`shake-${tagName}`} className="tag-chip">
                  {tagName}
                </span>
              ))}
            </div>
            <p className="tip-text">{shakeResult.aiTip}</p>
            <ScoreDetailCard scoreDetail={shakeResult.scoreDetail} title="这次为什么选中它" />
            <div className="detail-box">
              <span>步骤</span>
              <p>{shakeResult.steps || "当前食物还没有补步骤说明。"}</p>
            </div>
            {shakeResult.orderUrl ? (
              <div className="detail-box">
                <span>平台搜索入口</span>
                <a className="order-link" href={shakeResult.orderUrl} target="_blank" rel="noreferrer">
                  去美团搜索: {shakeResult.foodName} · ¥{formatPrice(shakeResult.referencePrice)}
                </a>
              </div>
            ) : null}
            <div className="inline-actions">
              <button type="button" className="accept" onClick={() => onDecision("accept")}>
                就吃这个
              </button>
              <button type="button" className="ghost" onClick={() => setShowRejectOptions((state) => !state)}>
                再摇一次
              </button>
            </div>
            {showRejectOptions ? (
              <div className="detail-box large">
                <span>这次为什么不选它</span>
                <div className="tag-cloud">
                  {REJECT_FEEDBACK_OPTIONS.map((option) => (
                    <button
                      key={option.value}
                      type="button"
                      className="tag danger"
                      onClick={() => onDecision("reject", option.value)}
                    >
                      {option.label}
                    </button>
                  ))}
                </div>
              </div>
            ) : null}
          </div>
        ) : (
          <div className="empty-state">
            先选模式再点击按钮. 推荐结果会直接展示 AI 文案、步骤和标签.
          </div>
        )}
      </div>

      <div className="card compact-card">
        <p className="section-kicker">联调提示</p>
        <h2>当前推荐策略</h2>
        <ul className="bullet-list">
          <li>优先走文本模型候选链路, 本地已切到 Ollama。</li>
          <li>前一个模型失败后自动切下一个, 全部失败才回退模板。</li>
          <li>确认或拒绝都会立刻刷新历史, 方便验证状态流转。</li>
          <li>历史详情会保留触发方式、文案来源和时间点。</li>
        </ul>
      </div>
    </section>
  );
}

export default ShakePanel;
