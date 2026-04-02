import { startTransition, useEffect, useState } from "react";
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
} from "./api";
import { EMPTY_FOOD_FORM, EMPTY_TAG_FORM } from "./constants";
import { normalizeFoodForm } from "./utils";
import FoodPanel from "./components/FoodPanel";
import HistoryDetailModal from "./components/HistoryDetailModal";
import HistoryPanel from "./components/HistoryPanel";
import PreferencePanel from "./components/PreferencePanel";
import ShakePanel from "./components/ShakePanel";
import Sidebar from "./components/Sidebar";
import StatsPanel from "./components/StatsPanel";
import TagPanel from "./components/TagPanel";

function App() {
  const HISTORY_PAGE_SIZE = 10;
  const [activeTab, setActiveTab] = useState("shake");
  const [tags, setTags] = useState([]);
  const [foods, setFoods] = useState([]);
  const [histories, setHistories] = useState([]);
  const [historyTotal, setHistoryTotal] = useState(0);
  const [historyStatus, setHistoryStatus] = useState("");
  const [historyPageNo, setHistoryPageNo] = useState(1);
  const [historyDetail, setHistoryDetail] = useState(null);
  const [preference, setPreference] = useState({
    favoriteTagIds: [],
    avoidTagIds: [],
    maxBudget: 30,
    lastMode: "LAZY"
  });
  const [shakeMode, setShakeMode] = useState("LAZY");
  const [shakeResult, setShakeResult] = useState(null);
  const [foodForm, setFoodForm] = useState(EMPTY_FOOD_FORM);
  const [foodImportText, setFoodImportText] = useState("");
  const [tagForm, setTagForm] = useState(EMPTY_TAG_FORM);
  const [loading, setLoading] = useState(false);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [stats, setStats] = useState(null);
  const [statsLoading, setStatsLoading] = useState(false);
  const [quota, setQuota] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    void bootstrap();
  }, []);

  useEffect(() => {
    void loadHistories(historyStatus, historyPageNo);
  }, [historyStatus, historyPageNo]);

  async function bootstrap() {
    setLoading(true);
    setError("");
    try {
      const [tagList, foodList, preferenceData, quotaData, statsData] = await Promise.all([
        getAllTags(),
        getFoods(),
        getPreference(),
        getShakeQuota(),
        getHistoryStats()
      ]);
      startTransition(() => {
        setTags(tagList);
        setFoods(foodList);
        setQuota(quotaData);
        setStats(statsData);
        setPreference({
          favoriteTagIds: preferenceData.favoriteTagIds || [],
          avoidTagIds: preferenceData.avoidTagIds || [],
          maxBudget: preferenceData.maxBudget ?? 30,
          lastMode: preferenceData.lastMode || "LAZY"
        });
        setShakeMode(preferenceData.lastMode || "LAZY");
      });
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function loadFoods() {
    const foodList = await getFoods();
    setFoods(foodList);
  }

  async function loadTags() {
    const tagList = await getAllTags();
    setTags(tagList);
    return tagList;
  }

  async function loadHistories(status, pageNo = 1) {
    setHistoryLoading(true);
    try {
      const page = await getHistories(status, pageNo, HISTORY_PAGE_SIZE);
      setHistories(page.records || []);
      setHistoryTotal(page.total || 0);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setHistoryLoading(false);
    }
  }

  async function loadQuota() {
    const quotaData = await getShakeQuota();
    setQuota(quotaData);
    return quotaData;
  }

  async function loadStats() {
    setStatsLoading(true);
    try {
      const statsData = await getHistoryStats();
      setStats(statsData);
      return statsData;
    } catch (requestError) {
      setError(requestError.message);
      throw requestError;
    } finally {
      setStatsLoading(false);
    }
  }

  async function handleShake() {
    setLoading(true);
    setMessage("");
    setError("");
    try {
      const data = await shake({ mode: shakeMode, triggerType: "CLICK" });
      setShakeResult(data);
      setMessage("推荐已生成, 可以直接确认今晚吃什么了。");
      await loadHistories(historyStatus, historyPageNo);
      await loadQuota();
      await loadStats();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleDecision(action, rejectFeedbackCode) {
    if (!shakeResult?.historyId) {
      return;
    }
    setLoading(true);
    setError("");
    try {
      if (action === "accept") {
        await acceptShake(shakeResult.historyId);
        setMessage("已确认, 今晚就按这个吃。");
      } else {
        await rejectShake(shakeResult.historyId, { rejectFeedbackCode });
        setShakeResult(null);
        setMessage("已拒绝, 再摇一次就会生成新的推荐。");
      }
      await loadHistories(historyStatus, historyPageNo);
      await loadQuota();
      await loadStats();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handlePreferenceSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");
    setError("");
    try {
      const saved = await updatePreference({
        ...preference,
        maxBudget: Number(preference.maxBudget || 0)
      });
      setPreference({
        favoriteTagIds: saved.favoriteTagIds || [],
        avoidTagIds: saved.avoidTagIds || [],
        maxBudget: saved.maxBudget ?? 30,
        lastMode: saved.lastMode || "LAZY"
      });
      setShakeMode(saved.lastMode || "LAZY");
      setMessage("偏好已保存, 后续推荐会按新口味走。");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleFoodSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");
    setError("");
    const payload = {
      name: foodForm.name,
      emoji: foodForm.emoji,
      steps: foodForm.steps,
      imageUrl: foodForm.imageUrl,
      orderUrl: foodForm.orderUrl,
      weight: Number(foodForm.weight),
      referencePrice: Number(foodForm.referencePrice),
      tagIds: foodForm.tagIds
    };
    try {
      if (foodForm.id) {
        await updateFood(foodForm.id, payload);
        setMessage("菜单已更新。");
      } else {
        await createFood(payload);
        setMessage("菜单已新增。");
      }
      setFoodForm(EMPTY_FOOD_FORM);
      await loadFoods();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleFoodExport() {
    setLoading(true);
    setMessage("");
    setError("");
    try {
      const items = await exportFoods();
      setFoodImportText(JSON.stringify(items, null, 2));
      setMessage(`已导出 ${items.length} 条菜单 JSON。`);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleFoodImport() {
    const source = foodImportText.trim();
    if (!source) {
      setError("请先粘贴要导入的 JSON。");
      return;
    }
    setLoading(true);
    setMessage("");
    setError("");
    try {
      const items = JSON.parse(source);
      if (!Array.isArray(items)) {
        throw new Error("导入内容必须是 JSON 数组。");
      }
      setFoodImportText(JSON.stringify(items, null, 2));
      const result = await importFoods({ items });
      setMessage(
        `导入完成: 新增 ${result.createdCount} 条, 更新 ${result.updatedCount} 条, 跳过 ${result.skippedCount} 条。`
      );
      await loadFoods();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleFoodDelete(id) {
    if (!window.confirm("确认删除这条菜单吗?")) {
      return;
    }
    setLoading(true);
    setError("");
    try {
      await deleteFood(id);
      setMessage("菜单已删除。");
      if (foodForm.id === id) {
        setFoodForm(EMPTY_FOOD_FORM);
      }
      await loadFoods();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleToggleFood(food) {
    setLoading(true);
    setError("");
    try {
      await updateFoodEnabled(food.id, food.enabled !== 1);
      setMessage(food.enabled === 1 ? "菜单已停用。" : "菜单已启用。");
      await loadFoods();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handlePickFood(foodId) {
    setLoading(true);
    setError("");
    try {
      const detail = await getFood(foodId);
      setFoodForm(normalizeFoodForm(detail));
      setActiveTab("food");
      setMessage("菜单详情已载入, 可以直接编辑。");
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleOpenHistory(recordId) {
    setLoading(true);
    setError("");
    try {
      const detail = await getHistory(recordId);
      setHistoryDetail(detail);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  function toggleTag(listKey, tagId) {
    const current = preference[listKey] || [];
    const next = current.includes(tagId)
      ? current.filter((id) => id !== tagId)
      : [...current, tagId];
    setPreference((state) => ({ ...state, [listKey]: next }));
  }

  function toggleFoodTag(tagId) {
    setFoodForm((state) => ({
      ...state,
      tagIds: state.tagIds.includes(tagId)
        ? state.tagIds.filter((id) => id !== tagId)
        : [...state.tagIds, tagId]
    }));
  }

  async function handleTagSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setMessage("");
    setError("");
    const payload = {
      name: tagForm.name.trim(),
      icon: tagForm.icon.trim(),
      sort: Number(tagForm.sort),
      enabled: Number(tagForm.enabled)
    };
    try {
      if (tagForm.id) {
        await updateTag(tagForm.id, payload);
        setMessage("标签已更新。");
      } else {
        await createTag(payload);
        setMessage("标签已新增。");
      }
      await loadTags();
      setTagForm(EMPTY_TAG_FORM);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleTagDelete(tag) {
    if (tag.tagType === "SYSTEM") {
      setError("系统标签不允许删除。");
      return;
    }
    if (!window.confirm(`确认删除标签「${tag.name}」吗?`)) {
      return;
    }
    setLoading(true);
    setError("");
    try {
      await deleteTag(tag.id);
      setMessage("标签已删除。");
      const tagList = await loadTags();
      const availableTagIds = new Set(tagList.map((item) => item.id));
      setPreference((state) => ({
        ...state,
        favoriteTagIds: state.favoriteTagIds.filter((id) => availableTagIds.has(id)),
        avoidTagIds: state.avoidTagIds.filter((id) => availableTagIds.has(id))
      }));
      setFoodForm((state) => ({
        ...state,
        tagIds: state.tagIds.filter((id) => availableTagIds.has(id))
      }));
      if (tagForm.id === tag.id) {
        setTagForm(EMPTY_TAG_FORM);
      }
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  function handlePickTag(tag) {
    setTagForm({
      id: tag.id,
      name: tag.name || "",
      icon: tag.icon || "",
      sort: tag.sort ?? 100,
      enabled: tag.enabled ?? 1,
      tagType: tag.tagType || "CUSTOM"
    });
    setActiveTab("tag");
    setMessage("标签详情已载入, 可以直接编辑。");
  }

  function handleHistoryStatusChange(status) {
    setHistoryStatus(status);
    setHistoryPageNo(1);
  }

  function handleHistoryPageChange(pageNo) {
    setHistoryPageNo(pageNo);
  }

  return (
    <>
      <div className="app-shell">
        <Sidebar
          activeTab={activeTab}
          foods={foods}
          historyTotal={historyTotal}
          quota={quota}
          setActiveTab={setActiveTab}
          tags={tags}
        />

        <main className="content-panel">
          {message ? <div className="banner success">{message}</div> : null}
          {error ? <div className="banner error">{error}</div> : null}
          {loading ? <div className="banner muted">处理中, 请稍等...</div> : null}

          {activeTab === "shake" ? (
            <ShakePanel
              onDecision={handleDecision}
              onShake={handleShake}
              quota={quota}
              shakeMode={shakeMode}
              setShakeMode={setShakeMode}
              shakeResult={shakeResult}
              tags={tags}
            />
          ) : null}

          {activeTab === "preference" ? (
            <PreferencePanel
              onSubmit={handlePreferenceSubmit}
              preference={preference}
              setPreference={setPreference}
              tags={tags}
              toggleTag={toggleTag}
            />
          ) : null}

          {activeTab === "tag" ? (
            <TagPanel
              onDelete={handleTagDelete}
              onPick={handlePickTag}
              onReset={() => setTagForm(EMPTY_TAG_FORM)}
              onSubmit={handleTagSubmit}
              setTagForm={setTagForm}
              tagForm={tagForm}
              tags={tags}
            />
          ) : null}

          {activeTab === "food" ? (
            <FoodPanel
              foodForm={foodForm}
              foodImportText={foodImportText}
              foods={foods}
              onDelete={handleFoodDelete}
              onExport={handleFoodExport}
              onImport={handleFoodImport}
              onPick={handlePickFood}
              onReset={() => setFoodForm(EMPTY_FOOD_FORM)}
              onSubmit={handleFoodSubmit}
              onToggle={handleToggleFood}
              setFoodForm={setFoodForm}
              setFoodImportText={setFoodImportText}
              tags={tags}
              toggleFoodTag={toggleFoodTag}
            />
          ) : null}

          {activeTab === "stats" ? <StatsPanel stats={stats} statsLoading={statsLoading} /> : null}

          {activeTab === "history" ? (
            <HistoryPanel
              histories={histories}
              historyLoading={historyLoading}
              historyPageNo={historyPageNo}
              historyStatus={historyStatus}
              historyTotal={historyTotal}
              onOpenHistory={handleOpenHistory}
              onPageChange={handleHistoryPageChange}
              onStatusChange={handleHistoryStatusChange}
              pageSize={HISTORY_PAGE_SIZE}
            />
          ) : null}
        </main>
      </div>

      <HistoryDetailModal historyDetail={historyDetail} onClose={() => setHistoryDetail(null)} />
    </>
  );
}

export default App;
