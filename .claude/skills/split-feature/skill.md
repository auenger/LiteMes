---
description: 'Split a large feature into sub-features, convert original to module index, and update queue.yaml.'
---

# Skill: split-feature

Split an existing feature into smaller sub-features based on requirement analysis. The original feature is converted into a module-level index that references all sub-features.

## Usage

```
/split-feature <feature-id>           # Interactive: analyze spec and propose split
/split-feature <feature-id> --from <doc-path>  # Split based on external requirement doc
```

## Execution Steps

### Step 1: Load Feature

1. Read `feature-workflow/queue.yaml` to find the feature entry
2. Read `features/pending-{id}/spec.md` (or `active-{id}`)
3. If `--from <doc-path>` provided, read the external requirement document

### Step 2: Analyze and Propose Split

Analyze the feature spec (and external doc if provided) to identify independent sub-features.

**Split criteria:**
- Each sub-feature should be independently deliverable
- Each sub-feature should have clear data model boundaries
- Group by business domain, not technical layers
- Target size: S or M (avoid creating new L features)

**Output a split proposal in this format:**

```
Split Proposal for {id}
─────────────────────────────────
Original: {name} (L)

Sub-features:
  1. {id}-xxx  {name}  [M]  depends: []
  2. {id}-yyy  {name}  [S]  depends: [{id}-xxx]
  3. {id}-zzz  {name}  [M]  depends: []
  ...

Dependency chain:
  {visual tree}

Confirm? [Y/n/edit]
```

### Step 3: Confirm with User

Present the proposal and wait for confirmation. User can:
- **Confirm**: proceed with the split
- **Edit**: adjust names, dependencies, or granularity
- **Cancel**: abort the operation

### Step 4: Generate Sub-Feature IDs

- Format: descriptive slug, not `{parent}-{suffix}` necessarily
- Example: parent `feat-enterprise-org` → children `feat-company`, `feat-factory`, `feat-department`
- ID should be self-descriptive and meaningful on its own

### Step 5: Create Sub-Feature Directories

For each sub-feature, create `features/pending-{id}/` with:

**spec.md:**
```markdown
# Feature: {id} {name}

## 基本信息
- **ID**: {id}
- **名称**: {name}
- **优先级**: {priority}
- **规模**: {S|M}
- **依赖**: {dependencies}
- **父模块**: {parent_id}
- **创建时间**: {date}

## 需求来源
- {reference to parent spec or external doc}

## 需求描述
{business requirements from analysis}

## 数据模型
{entity definitions with field-level detail}

## 验收标准 (Gherkin)
{acceptance scenarios}
```

**task.md:**
```markdown
# Tasks: {id} {name}

## 任务清单

### 后端
- [ ] ...

### 前端
- [ ] ...

### 通用
- [ ] ...

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| {date} | 已拆分 | 从 {parent_id} 拆分 |
```

### Step 6: Convert Original Feature to Module Index

Rewrite the original feature's `spec.md` to be a module index:

```markdown
# Feature: {id} {模块名称}

## 基本信息
- **ID**: {id}
- **名称**: {模块名称}
- **优先级**: {priority}
- **规模**: L（模块级）
- **依赖**: {dependencies}
- **创建时间**: {original_date}

## 需求来源
- {reference docs}

## 模块概述
{high-level module description}

## 子 Feature 列表

| ID | 名称 | 优先级 | 规模 | 依赖 |
|----|------|--------|------|------|
| [{child1}](../pending-{child1}/spec.md) | {name} | {p} | {s} | {deps} |
| [{child2}](../pending-{child2}/spec.md) | {name} | {p} | {s} | {deps} |
...

## 依赖关系图
{visual dependency tree}

## 整体数据模型关系
{ER-style relationships across sub-features}

## 模块进度
| 子 Feature | 状态 | 备注 |
|-----------|------|------|
| {child1} | pending | |
...
```

Rewrite the original feature's `task.md`:

```markdown
# Tasks: {id} {模块名称}

> 本 feature 为模块级 feature，不直接开发，通过子 feature 推进。

## 子 Feature 进度

- [ ] [{child1}](../pending-{child1}/task.md) — {name}
- [ ] [{child2}](../pending-{child2}/task.md) — {name}
...

## 完成条件
所有子 feature 完成后，本模块 feature 标记为完成。

---

## 进度记录
| 日期 | 进度 | 备注 |
|------|------|------|
| {date} | 模块拆分完成 | 拆分为 N 个子 feature |
```

### Step 7: Update queue.yaml

Read `feature-workflow/queue.yaml` and update:

1. **Modify parent entry** — add `children: [child_ids]` field
2. **Add sub-feature entries** — each with `parent: {parent_id}`, proper priority and dependencies
3. **Update downstream dependencies** — other features that depended on the parent should keep depending on the parent (not individual children)
4. **Sort** pending list by priority descending
5. **Update** `meta.last_updated`

**queue.yaml entry format:**

```yaml
# Parent entry (module index)
- id: feat-enterprise-org
  name: "企业管理模块"
  priority: 90
  size: L
  dependencies:
    - feat-project-scaffold
  children:
    - feat-company
    - feat-factory
    - feat-department
    ...

# Child entries
- id: feat-company
  name: "公司管理"
  priority: 95
  size: M
  parent: feat-enterprise-org
  dependencies:
    - feat-project-scaffold

- id: feat-factory
  name: "工厂管理"
  priority: 94
  size: M
  parent: feat-enterprise-org
  dependencies:
    - feat-company
...
```

### Step 8: Verify Consistency

After all changes, verify:
1. Every child ID in parent's `children` list has a corresponding directory and queue entry
2. Every child's `parent` field matches the parent ID
3. Dependency references are valid (no dangling references)
4. No duplicate IDs in queue
5. Priority ordering is logical (children prioritized before parent)

Report summary to user.

## Output

```
Feature split completed!

Parent: {id} (模块索引，不直接开发)
├── {child1} [{size}] ← {name}
├── {child2} [{size}] ← {name}
├── {child3} [{size}] ← {name}
└── {child4} [{size}] ← {name}

Files created:
  features/pending-{child1}/spec.md, task.md
  features/pending-{child2}/spec.md, task.md
  ...

Files updated:
  features/pending-{parent}/spec.md  (→ module index)
  features/pending-{parent}/task.md  (→ progress tracker)
  feature-workflow/queue.yaml        (parent/children linked)

Queue: N sub-features added, downstream dependencies updated.
```

## Error Handling

| Error | Description | Solution |
|-------|-------------|----------|
| FEATURE_NOT_FOUND | Feature ID not in queue | Check ID spelling, use /list-features |
| FEATURE_ACTIVE | Feature is currently active | Complete or block before splitting |
| ALREADY_SPLIT | Feature already has children | Use /list-features --verbose to check |
| QUEUE_PARSE_ERROR | queue.yaml malformed | Fix YAML syntax manually |
| ID_CONFLICT | Generated child ID already exists | Use different slug |
| PERMISSION_ERROR | Cannot write to features/ | Check filesystem permissions |

## Notes

1. **Business-first splitting** — split by business domain boundaries, not technical layers
2. **Parent as index** — original feature becomes a read-only module index
3. **Downstream deps stay on parent** — external features depend on the module, not individual children
4. **Children are independently actionable** — each can be started/completed independently
5. **Priority ordering** — children have higher priority than parent to ensure they complete first
