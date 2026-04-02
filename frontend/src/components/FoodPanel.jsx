import { formatPrice, getTagNames } from "../utils";

function FoodPanel({
  foodForm,
  foodImportText,
  foods,
  onDelete,
  onExport,
  onImport,
  onPick,
  onReset,
  onSubmit,
  onToggle,
  setFoodForm,
  setFoodImportText,
  tags,
  toggleFoodTag
}) {
  return (
    <section className="panel-grid food-grid">
      <form className="card" onSubmit={onSubmit}>
        <p className="section-kicker">菜单维护</p>
        <h2>{foodForm.id ? "编辑菜单" : "新增菜单"}</h2>
        <div className="double-field">
          <label className="field">
            <span>名称</span>
            <input
              value={foodForm.name}
              onChange={(event) => setFoodForm((state) => ({ ...state, name: event.target.value }))}
              required
            />
          </label>
          <label className="field">
            <span>Emoji</span>
            <input
              value={foodForm.emoji}
              onChange={(event) => setFoodForm((state) => ({ ...state, emoji: event.target.value }))}
            />
          </label>
        </div>
        <div className="double-field">
          <label className="field">
            <span>权重</span>
            <input
              type="number"
              min="0"
              max="1000"
              value={foodForm.weight}
              onChange={(event) => setFoodForm((state) => ({ ...state, weight: event.target.value }))}
              required
            />
          </label>
          <label className="field">
            <span>参考价格</span>
            <input
              type="number"
              min="0"
              step="1"
              value={foodForm.referencePrice}
              onChange={(event) =>
                setFoodForm((state) => ({ ...state, referencePrice: event.target.value }))
              }
            />
          </label>
        </div>
        <label className="field">
          <span>步骤</span>
          <textarea
            rows="4"
            value={foodForm.steps}
            onChange={(event) => setFoodForm((state) => ({ ...state, steps: event.target.value }))}
          />
        </label>
        <label className="field">
          <span>图片地址</span>
          <input
            value={foodForm.imageUrl}
            onChange={(event) => setFoodForm((state) => ({ ...state, imageUrl: event.target.value }))}
          />
        </label>
        <label className="field">
          <span>平台搜索链接</span>
          <input
            value={foodForm.orderUrl}
            onChange={(event) => setFoodForm((state) => ({ ...state, orderUrl: event.target.value }))}
            placeholder="留空则按菜名和价格自动生成美团搜索链接"
          />
        </label>
        {foodForm.imageUrl ? (
          <div className="image-preview">
            <img src={foodForm.imageUrl} alt={foodForm.name || "食物预览"} />
          </div>
        ) : null}
        <div className="tag-section">
          <span>绑定标签</span>
          <div className="tag-cloud">
            {tags.map((tag) => (
              <button
                key={`food-${tag.id}`}
                type="button"
                className={foodForm.tagIds.includes(tag.id) ? "tag active" : "tag"}
                onClick={() => toggleFoodTag(tag.id)}
              >
                {tag.name}
              </button>
            ))}
          </div>
        </div>
        <div className="inline-actions">
          <button type="submit" className="primary-action slim">
            {foodForm.id ? "更新菜单" : "新增菜单"}
          </button>
          <button type="button" className="ghost" onClick={onReset}>
            清空表单
          </button>
        </div>
      </form>

      <div className="card table-card">
        <div className="section-head">
          <div>
            <p className="section-kicker">已入库菜单</p>
            <h2>菜单列表</h2>
          </div>
          <div className="inline-actions">
            <button type="button" className="ghost small" onClick={onExport}>
              导出 JSON
            </button>
            <span className="mode-badge">{foods.length} 条</span>
          </div>
        </div>

        <label className="field">
          <span>批量导入 JSON</span>
          <textarea
            rows="10"
            value={foodImportText}
            onChange={(event) => setFoodImportText(event.target.value)}
            placeholder="粘贴导出的 JSON 数组, 支持按名称新增或更新菜单"
          />
        </label>
        <div className="inline-actions">
          <button type="button" className="primary-action slim" onClick={onImport}>
            批量导入
          </button>
        </div>

        <div className="food-list">
          {foods.map((food) => (
            <div key={food.id} className="food-item">
              <div className="food-main">
                <h3>
                  {food.emoji || "🍽️"} {food.name}
                </h3>
                <p>
                  价格 ¥{formatPrice(food.referencePrice)} · 权重 {food.weight} · 状态{" "}
                  {food.enabled === 1 ? "启用" : "停用"}
                </p>
                {food.orderUrl ? (
                  <a className="inline-link" href={food.orderUrl} target="_blank" rel="noreferrer">
                    打开美团搜索链接
                  </a>
                ) : null}
                <div className="chip-row">
                  {getTagNames(tags, food.tagIds).map((tagName) => (
                    <span key={`${food.id}-${tagName}`} className="tag-chip subtle">
                      {tagName}
                    </span>
                  ))}
                </div>
              </div>
              <div className="item-actions">
                <button type="button" className="ghost small" onClick={() => onPick(food.id)}>
                  编辑
                </button>
                <button type="button" className="ghost small" onClick={() => onToggle(food)}>
                  {food.enabled === 1 ? "停用" : "启用"}
                </button>
                <button type="button" className="danger small" onClick={() => onDelete(food.id)}>
                  删除
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default FoodPanel;
