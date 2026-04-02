import { mapModeLabel, mapRejectFeedbackLabel } from "../utils";

function StatsPanel({ stats, statsLoading }) {
  if (statsLoading) {
    return <div className="card">统计加载中...</div>;
  }

  if (!stats) {
    return <div className="card">暂无统计数据.</div>;
  }

  return (
    <section className="panel-grid stats-grid">
      <div className="card">
        <p className="section-kicker">总体情况</p>
        <h2>统计看板</h2>
        <div className="score-grid">
          <div className="score-item">
            <strong>{stats.totalCount}</strong>
            <small>总摇一摇次数</small>
          </div>
          <div className="score-item">
            <strong>{stats.acceptanceRate}%</strong>
            <small>接受率</small>
          </div>
          <div className="score-item">
            <strong>{stats.acceptedCount}</strong>
            <small>已接受</small>
          </div>
          <div className="score-item">
            <strong>{stats.rejectedCount}</strong>
            <small>已拒绝</small>
          </div>
          <div className="score-item">
            <strong>{stats.pendingCount}</strong>
            <small>待确认</small>
          </div>
        </div>
      </div>

      <div className="card compact-card">
        <p className="section-kicker">模式分布</p>
        <h2>哪种模式最常用</h2>
        <div className="stats-list">
          {(stats.modeStats || []).map((item) => (
            <div key={item.code} className="stats-row">
              <span>{mapModeLabel(item.code)}</span>
              <strong>{item.count}</strong>
            </div>
          ))}
          {!stats.modeStats?.length ? <div className="empty-state compact">暂无模式数据.</div> : null}
        </div>
      </div>

      <div className="card compact-card">
        <p className="section-kicker">拒绝反馈</p>
        <h2>为什么没选它</h2>
        <div className="stats-list">
          {(stats.rejectFeedbackStats || []).map((item) => (
            <div key={item.code} className="stats-row">
              <span>{item.label || mapRejectFeedbackLabel(item.code)}</span>
              <strong>{item.count}</strong>
            </div>
          ))}
          {!stats.rejectFeedbackStats?.length ? <div className="empty-state compact">还没有拒绝反馈.</div> : null}
        </div>
      </div>

      <div className="card compact-card">
        <p className="section-kicker">最近 7 天</p>
        <h2>每日摇一摇次数</h2>
        <div className="stats-list">
          {(stats.recentDailyStats || []).map((item) => (
            <div key={item.day} className="stats-row">
              <span>{item.day}</span>
              <strong>{item.count}</strong>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default StatsPanel;
