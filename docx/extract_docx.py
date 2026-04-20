#!/usr/bin/env python3
import os
import zipfile
import shutil
import xml.etree.ElementTree as ET
import tempfile
import re

SRC_DIR = "/Users/ryan/mycode/LiteMes/docx"
OUT_BASE = "/Users/ryan/mycode/LiteMes/docx"

NS = {
    "w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main",
    "w14": "http://schemas.microsoft.com/office/word/2010/wordml",
}

files = [f for f in os.listdir(SRC_DIR) if f.endswith(".docx") and not f.startswith(".~")]

def get_style_level(style_name):
    if not style_name:
        return None
    m = re.search(r'Heading(\d)', style_name)
    return int(m.group(1)) if m else None

def para_to_text(para, ns):
    runs = para.findall(".//w:t", ns)
    return "".join(r.text or "" for r in runs)

def extract_tables(doc_root, ns):
    parts = []
    for tbl in doc_root.findall(".//w:tbl", ns):
        rows_out = []
        for row in tbl.findall("w:tr", ns):
            cells = []
            for cell in row.findall("w:tc", ns):
                cell_text = "".join(t.text or "" for t in cell.findall(".//w:t", ns))
                cells.append(cell_text.strip())
            rows_out.append(cells)
        if rows_out:
            header = rows_out[0]
            sep = ["---"] * len(header)
            parts.append("")
            parts.append("| " + " | ".join(header) + " |")
            parts.append("| " + " | ".join(sep) + " |")
            for row in rows_out[1:]:
                parts.append("| " + " | ".join(row) + " |")
            parts.append("")
    return "\n".join(parts)

def convert_to_markdown(doc_root, ns):
    lines = []
    for para in doc_root.findall(".//w:p", ns):
        style_elem = para.find("w:pPr/w:pStyle", ns)
        style_name = style_elem.get(f"{{{ns['w']}}}val", "") if style_elem is not None else ""
        level = get_style_level(style_name)
        text = para_to_text(para, ns)
        if not text.strip():
            lines.append("")
            continue
        if level:
            lines.append(f"{'#' * level} {text}\n")
        else:
            lines.append(f"{text}\n")
    table_md = extract_tables(doc_root, ns)
    return "".join(lines) + table_md

for fname in files:
    src = os.path.join(SRC_DIR, fname)
    base = fname.replace(".docx", "")
    md_dir = os.path.join(OUT_BASE, base)
    img_dir = os.path.join(md_dir, "images")
    os.makedirs(md_dir, exist_ok=True)
    os.makedirs(img_dir, exist_ok=True)

    tmp_dir = tempfile.mkdtemp()
    try:
        with zipfile.ZipFile(src, "r") as z:
            z.extractall(tmp_dir)
            doc_path = os.path.join(tmp_dir, "word", "document.xml")
            tree = ET.parse(doc_path)
            root = tree.getroot()

            md_content = convert_to_markdown(root, NS)
            md_path = os.path.join(md_dir, f"{base}.md")
            with open(md_path, "w", encoding="utf-8") as f:
                f.write(f"# {base}\n\n")
                f.write(md_content)
            print(f"[OK] Markdown: {md_path}")

            media_src = os.path.join(tmp_dir, "word", "media")
            img_count = 0
            if os.path.isdir(media_src):
                for img in os.listdir(media_src):
                    shutil.copy2(os.path.join(media_src, img), os.path.join(img_dir, img))
                    img_count += 1
            print(f"[OK] 图片: {img_count} 张 -> {img_dir}")
    finally:
        shutil.rmtree(tmp_dir)

print(f"\n完成！共处理 {len(files)} 个文件。")
