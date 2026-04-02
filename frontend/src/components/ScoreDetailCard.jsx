function ScoreDetailCard({ scoreDetail, title = "推荐原因" }) {
  if (!scoreDetail) {
    return null;
  }

  return (
    <div className="detail-box large">
      <span>{title}</span>
      <div className="score-grid">
        <div className="score-item">
          <strong>{scoreDetail.finalScore}</strong>
          <small>最终分数</small>
        </div>
        <div className="score-item">
          <strong>{scoreDetail.baseWeight}</strong>
          <small>基础权重</small>
        </div>
        <div className="score-item">
          <strong>x{scoreDetail.modeFactor}</strong>
          <small>{scoreDetail.modeHit ? "模式命中" : "模式未命中"}</small>
        </div>
        <div className="score-item">
          <strong>x{scoreDetail.favoriteFactor}</strong>
          <small>{scoreDetail.favoriteHit ? "喜欢标签命中" : "喜欢标签未命中"}</small>
        </div>
        <div className="score-item">
          <strong>x{scoreDetail.budgetFactor}</strong>
          <small>预算修正</small>
        </div>
        <div className="score-item">
          <strong>x{scoreDetail.diversityFactor}</strong>
          <small>重复惩罚</small>
        </div>
      </div>
      <ul className="score-reason-list">
        {(scoreDetail.reasonLabels || []).map((reason) => (
          <li key={reason}>{reason}</li>
        ))}
      </ul>
    </div>
  );
}

export default ScoreDetailCard;
