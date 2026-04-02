function TagPanel({ onDelete, onPick, onReset, onSubmit, setTagForm, tagForm, tags }) {
  return (
    <section className="panel-grid food-grid">
      <form className="card" onSubmit={onSubmit}>
        <p className="section-kicker">标签维护</p>
        <h2>{tagForm.id ? "编辑标签" : "新增标签"}</h2>
        <div className="double-field">
          <label className="field">
            <span>名称</span>
            <input
              value={tagForm.name}
              onChange={(event) => setTagForm((state) => ({ ...state, name: event.target.value }))}
              required
            />
          </label>
          <label className="field">
            <span>图标</span>
            <input
              value={tagForm.icon}
              onChange={(event) => setTagForm((state) => ({ ...state, icon: event.target.value }))}
              placeholder="例如 🌶️"
            />
          </label>
        </div>
        <div className="double-field">
          <label className="field">
            <span>排序</span>
            <input
              type="number"
              min="0"
              max="9999"
              value={tagForm.sort}
              onChange={(event) => setTagForm((state) => ({ ...state, sort: event.target.value }))}
              required
            />
          </label>
          <label className="field">
            <span>状态</span>
            <select
              value={tagForm.enabled}
              onChange={(event) =>
                setTagForm((state) => ({ ...state, enabled: Number(event.target.value) }))
              }
            >
              <option value={1}>启用</option>
              <option value={0}>停用</option>
            </select>
          </label>
        </div>
        <div className="detail-box">
          <span>标签类型</span>
          <p>{tagForm.tagType === "SYSTEM" ? "系统标签" : "自定义标签"}</p>
        </div>
        <div className="inline-actions">
          <button type="submit" className="primary-action slim">
            {tagForm.id ? "更新标签" : "新增标签"}
          </button>
          <button type="button" className="ghost" onClick={onReset}>
            清空表单
          </button>
        </div>
      </form>

      <div className="card table-card">
        <div className="section-head">
          <div>
            <p className="section-kicker">当前标签池</p>
            <h2>标签列表</h2>
          </div>
          <span className="mode-badge">{tags.length} 个</span>
        </div>
        <div className="food-list">
          {tags.map((tag) => (
            <div key={tag.id} className="food-item">
              <div className="food-main">
                <h3>
                  {tag.icon || "🏷️"} {tag.name}
                </h3>
                <p>
                  类型 {tag.tagType} · 排序 {tag.sort} · 状态 {tag.enabled === 1 ? "启用" : "停用"}
                </p>
              </div>
              <div className="item-actions">
                <button type="button" className="ghost small" onClick={() => onPick(tag)}>
                  编辑
                </button>
                {tag.tagType === "SYSTEM" ? (
                  <span className="tag-chip subtle">系统保留</span>
                ) : (
                  <button type="button" className="danger small" onClick={() => onDelete(tag)}>
                    删除
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default TagPanel;
