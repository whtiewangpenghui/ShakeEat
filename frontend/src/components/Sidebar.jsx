import { NAVS } from "../constants";

function Sidebar({ activeTab, foods, historyTotal, quota, setActiveTab, tags }) {
  return (
    <aside className="side-panel">
      <p className="eyebrow">ShakeEat Console</p>
      <h1>下班摇摇吃</h1>
      <p className="intro">
        用一个控制台把今晚决策收紧. 前台负责快速出结果, 后台负责留痕和持续调权重.
      </p>
      <nav className="tab-list">
        {NAVS.map((item) => (
          <button
            key={item.key}
            type="button"
            className={activeTab === item.key ? "tab-item active" : "tab-item"}
            onClick={() => setActiveTab(item.key)}
          >
            <span>{item.label}</span>
            <small>{item.hint}</small>
          </button>
        ))}
      </nav>
      <div className="status-stack">
        <span className="pill">标签 {tags.length}</span>
        <span className="pill">菜单 {foods.length}</span>
        <span className="pill">历史 {historyTotal}</span>
        {quota ? <span className="pill">今日 {quota.usedCount}/{quota.dailyLimit}</span> : null}
      </div>
    </aside>
  );
}

export default Sidebar;
