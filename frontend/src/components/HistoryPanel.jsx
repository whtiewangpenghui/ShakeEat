import { STATUS_OPTIONS } from "../constants";
import { formatTime, mapModeLabel, mapStatusLabel } from "../utils";

function HistoryPanel({
  histories,
  historyLoading,
  historyPageNo,
  historyStatus,
  historyTotal,
  onOpenHistory,
  onPageChange,
  onStatusChange,
  pageSize
}) {
  const pageCount = Math.max(1, Math.ceil(historyTotal / pageSize));

  return (
    <section className="panel-grid">
      <div className="card table-card">
        <div className="section-head">
          <div>
            <p className="section-kicker">决策记录</p>
            <h2>历史列表</h2>
          </div>
          <select
            className="filter-select"
            value={historyStatus}
            onChange={(event) => onStatusChange(event.target.value)}
          >
            {STATUS_OPTIONS.map((status) => (
              <option key={status.value || "all"} value={status.value}>
                {status.label}
              </option>
            ))}
          </select>
        </div>

        {historyLoading ? <div className="empty-state">历史加载中...</div> : null}
        {!historyLoading && histories.length === 0 ? (
          <div className="empty-state">还没有历史记录, 先去摇一次.</div>
        ) : null}

        <div className="history-list">
          {histories.map((record) => (
            <div key={record.id} className="history-item">
              <div className="history-main">
                <h3>{record.foodName}</h3>
                <p>{record.aiTipPreview}</p>
              </div>
              <div className="history-meta">
                <span>{mapModeLabel(record.mode)}</span>
                <span>{mapStatusLabel(record.resultStatus)}</span>
                <span>{formatTime(record.shakeTime)}</span>
              </div>
              <div className="item-actions">
                <button type="button" className="ghost small" onClick={() => onOpenHistory(record.id)}>
                  查看详情
                </button>
              </div>
            </div>
          ))}
        </div>

        <div className="pagination-bar">
          <span className="pagination-meta">
            第 {historyPageNo} / {pageCount} 页 · 共 {historyTotal} 条
          </span>
          <div className="item-actions">
            <button
              type="button"
              className="ghost small"
              disabled={historyPageNo <= 1}
              onClick={() => onPageChange(historyPageNo - 1)}
            >
              上一页
            </button>
            <button
              type="button"
              className="ghost small"
              disabled={historyPageNo >= pageCount}
              onClick={() => onPageChange(historyPageNo + 1)}
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}

export default HistoryPanel;
