from pathlib import Path
import json

from playwright.sync_api import sync_playwright


APP_URL = "http://127.0.0.1:5173"
OUTPUT_DIR = Path(__file__).resolve().parent / "artifacts"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)


def read_text(locator):
    return locator.text_content().strip() if locator.count() else ""


with sync_playwright() as playwright:
    browser = playwright.chromium.launch(headless=True)
    page = browser.new_page(viewport={"width": 1440, "height": 1024})
    summary = {"steps": [], "screenshots": []}

    try:
        page.goto(APP_URL, wait_until="networkidle")
        summary["steps"].append("首页打开成功")

        page.screenshot(path=str(OUTPUT_DIR / "home.png"), full_page=True)
        summary["screenshots"].append("home.png")

        page.get_by_role("button", name="偏好").click()
        page.wait_for_load_state("networkidle")
        page.locator("h2", has_text="偏好设置").wait_for()
        summary["steps"].append("偏好页加载成功")

        page.get_by_role("button", name="菜单").click()
        page.wait_for_load_state("networkidle")
        page.locator("h2", has_text="菜单列表").wait_for()
        if page.get_by_role("button", name="编辑").count():
            page.get_by_role("button", name="编辑").first.click()
            page.locator("input").first.wait_for()
            summary["steps"].append("菜单编辑详情加载成功")
        else:
            summary["steps"].append("菜单页无可编辑数据")

        page.get_by_role("button", name="历史").click()
        page.wait_for_load_state("networkidle")
        page.locator("h2", has_text="历史列表").wait_for()
        if page.get_by_role("button", name="查看详情").count():
            page.get_by_role("button", name="查看详情").first.click()
            page.locator(".detail-modal").wait_for()
            page.get_by_role("button", name="关闭").click()
            summary["steps"].append("历史详情弹层打开成功")
        else:
            summary["steps"].append("历史页暂无详情数据")

        page.get_by_role("button", name="摇一摇").click()
        page.wait_for_load_state("networkidle")
        page.get_by_role("button", name="现在摇一摇").click()
        page.locator(".result-card, .banner.error").first.wait_for(timeout=25000)
        if page.get_by_role("button", name="就吃这个").count():
            result_title = read_text(page.locator(".result-title h3").first)
            summary["steps"].append(f"摇一摇成功, 命中: {result_title}")
        else:
            error_text = read_text(page.locator(".banner.error").first)
            raise RuntimeError(error_text or "摇一摇未返回结果")

        page.screenshot(path=str(OUTPUT_DIR / "shake-result.png"), full_page=True)
        summary["screenshots"].append("shake-result.png")
        print(json.dumps(summary, ensure_ascii=False, indent=2))
    finally:
        browser.close()
