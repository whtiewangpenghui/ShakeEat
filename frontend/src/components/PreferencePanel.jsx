import { MODE_OPTIONS } from "../constants";

function PreferencePanel({ onSubmit, preference, setPreference, tags, toggleTag }) {
  return (
    <section className="panel-grid preference-grid">
      <form className="card" onSubmit={onSubmit}>
        <p className="section-kicker">口味配置</p>
        <h2>偏好设置</h2>
        <label className="field">
          <span>默认模式</span>
          <select
            value={preference.lastMode}
            onChange={(event) =>
              setPreference((state) => ({ ...state, lastMode: event.target.value }))
            }
          >
            {MODE_OPTIONS.map((mode) => (
              <option key={mode.value} value={mode.value}>
                {mode.label}
              </option>
            ))}
          </select>
        </label>
        <label className="field">
          <span>预算上限</span>
          <input
            type="number"
            min="0"
            step="1"
            value={preference.maxBudget}
            onChange={(event) =>
              setPreference((state) => ({ ...state, maxBudget: Number(event.target.value) }))
            }
          />
        </label>
        <div className="tag-section">
          <span>喜欢标签</span>
          <div className="tag-cloud">
            {tags.map((tag) => (
              <button
                key={`favorite-${tag.id}`}
                type="button"
                className={preference.favoriteTagIds.includes(tag.id) ? "tag active" : "tag"}
                onClick={() => toggleTag("favoriteTagIds", tag.id)}
              >
                {tag.name}
              </button>
            ))}
          </div>
        </div>
        <div className="tag-section">
          <span>避免标签</span>
          <div className="tag-cloud">
            {tags.map((tag) => (
              <button
                key={`avoid-${tag.id}`}
                type="button"
                className={preference.avoidTagIds.includes(tag.id) ? "tag danger" : "tag"}
                onClick={() => toggleTag("avoidTagIds", tag.id)}
              >
                {tag.name}
              </button>
            ))}
          </div>
        </div>
        <button type="submit" className="primary-action">
          保存偏好
        </button>
      </form>

      <div className="card compact-card">
        <p className="section-kicker">规则提醒</p>
        <h2>当前偏好解释</h2>
        <ul className="bullet-list">
          <li>喜欢标签会抬高权重, 但不会完全锁死结果。</li>
          <li>避免标签会直接过滤候选, 是最强约束。</li>
          <li>预算超出只做轻量降权, 不会让菜单直接消失。</li>
          <li>默认模式会影响首页打开后的第一反应。</li>
        </ul>
      </div>
    </section>
  );
}

export default PreferencePanel;
