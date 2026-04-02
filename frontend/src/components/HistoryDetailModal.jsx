import { formatPrice, formatTime, mapModeLabel, mapRejectFeedbackLabel, mapStatusLabel } from "../utils";
import ScoreDetailCard from "./ScoreDetailCard";

function HistoryDetailModal({ historyDetail, onClose }) {
  if (!historyDetail) {
    return null;
  }

  return (
    <div className="modal-mask" onClick={onClose}>
      <div className="detail-modal" onClick={(event) => event.stopPropagation()}>
        <div className="section-head">
          <div>
            <p className="section-kicker">历史详情</p>
            <h2>{historyDetail.foodName}</h2>
          </div>
          <button type="button" className="ghost small" onClick={onClose}>
            关闭
          </button>
        </div>
        <div className="detail-grid">
          <div className="detail-box">
            <span>模式</span>
            <p>{mapModeLabel(historyDetail.mode)}</p>
          </div>
          <div className="detail-box">
            <span>结果状态</span>
            <p>{mapStatusLabel(historyDetail.resultStatus)}</p>
          </div>
          <div className="detail-box">
            <span>触发方式</span>
            <p>{historyDetail.triggerType}</p>
          </div>
          <div className="detail-box">
            <span>文案来源</span>
            <p>{historyDetail.aiSource}</p>
          </div>
          <div className="detail-box">
            <span>摇出时间</span>
            <p>{formatTime(historyDetail.shakeTime)}</p>
          </div>
          <div className="detail-box">
            <span>确认时间</span>
            <p>{formatTime(historyDetail.confirmTime)}</p>
          </div>
          <div className="detail-box">
            <span>价格</span>
            <p>¥{formatPrice(historyDetail.referencePrice)}</p>
          </div>
        </div>
        <div className="detail-box large">
          <span>AI 文案</span>
          <p>{historyDetail.aiTip || "暂无文案"}</p>
        </div>
        {historyDetail.rejectFeedbackCode ? (
          <div className="detail-box large">
            <span>拒绝原因</span>
            <p>{historyDetail.rejectFeedbackLabel || mapRejectFeedbackLabel(historyDetail.rejectFeedbackCode)}</p>
          </div>
        ) : null}
        <ScoreDetailCard scoreDetail={historyDetail.scoreDetail} title="当时为什么会推荐它" />
        {historyDetail.tagNames?.length ? (
          <div className="detail-box large">
            <span>标签快照</span>
            <div className="chip-row detail-chip-row">
              {historyDetail.tagNames.map((tagName) => (
                <span key={`history-tag-${tagName}`} className="tag-chip">
                  {tagName}
                </span>
              ))}
            </div>
          </div>
        ) : null}
        <div className="detail-box large">
          <span>步骤</span>
          <p>{historyDetail.steps || "当前没有记录步骤。"}</p>
        </div>
        {historyDetail.orderUrl ? (
          <div className="detail-box large">
            <span>平台搜索入口</span>
            <a className="order-link" href={historyDetail.orderUrl} target="_blank" rel="noreferrer">
              去美团搜索: {historyDetail.foodName} · ¥{formatPrice(historyDetail.referencePrice)}
            </a>
          </div>
        ) : null}
      </div>
    </div>
  );
}

export default HistoryDetailModal;
